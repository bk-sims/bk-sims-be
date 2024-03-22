package com.dalv.bksims.services.social_points_management;

import com.dalv.bksims.exceptions.ActivityTitleAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRegistrationRequest;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRequest;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import com.dalv.bksims.models.entities.social_points_management.ActivityType;
import com.dalv.bksims.models.entities.social_points_management.Organization;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.enums.Status;
import com.dalv.bksims.models.repositories.social_points_management.ActivityParticipationRepository;
import com.dalv.bksims.models.repositories.social_points_management.ActivityRepository;
import com.dalv.bksims.models.repositories.social_points_management.OrganizationRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import com.dalv.bksims.services.common.S3Service;
import com.dalv.bksims.validations.ActivityValidator;
import com.dalv.bksims.validations.DateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.joda.time.LocalDate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepo;

    private final UserRepository userRepo;

    private final ActivityParticipationRepository activityParticipationRepo;

    private final OrganizationRepository organizationRepo;

    private final S3Service s3Service;

    @Transactional
    public Activity createActivity(ActivityRequest activityRequest) {
        Activity post = activityRepo.findOneByTitle(activityRequest.title());
        if (post != null) {
            throw new ActivityTitleAlreadyExistsException("Activity title already exists");
        }

        // Check organization name
        String organizationRequestName = (activityRequest.organization() == null) ? "Other School-level Units" : activityRequest.organization();
        Organization organization = organizationRepo.findByName(organizationRequestName);

        if (organization == null) {
            throw new EntityNotFoundException("Organization with name " + organizationRequestName + " not found");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User owner = userRepo.findByEmail(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User with id " + organizationRequestName + " not found"));

        if (owner == null) {
            throw new EntityNotFoundException("Owner with email " + userEmail + " not found");
        }

        String organizationName = organization.getName();

        // Check validity of dates
        DateValidator.validateStartDateAndEndDate(activityRequest.startDate(), activityRequest.endDate());
        if (activityRequest.registrationStartDate() != null && activityRequest.registrationEndDate() != null) {
            DateValidator.validateStartDateAndEndDate(activityRequest.registrationStartDate(),
                                                      activityRequest.registrationEndDate());
        }
        // Check validity of banner file
        MultipartFile bannerFile = activityRequest.bannerFile();
        ActivityValidator.validateBannerFile(bannerFile);

        // Check validity of regulations file
        MultipartFile regulationsFile = activityRequest.regulationsFile();
        if (regulationsFile != null) {
            ActivityValidator.validateRegulationsFile(regulationsFile);
        }

        // Upload file to S3
        String bannerFileName = s3Service.uploadFileForActivity(bannerFile, organizationName);
        String regulationsFileName = null;

        if (regulationsFile != null) {
            regulationsFileName = s3Service.uploadFileForActivity(regulationsFile, organizationName);
        }

        // Get file urls
        String bannerFileUrl = s3Service.getFileUrl(bannerFileName, organizationName + "/");
        String regulationsFileUrl = (regulationsFileName == null) ? null : s3Service.getFileUrl(regulationsFileName,
                                                                                                organizationName + "/");

        String activityType = null;

        if (activityRequest.activityType() != null && !activityRequest.activityType().isBlank()) {
            activityType = activityRequest.activityType();
        }

        Activity activity = Activity.builder()
                .title(activityRequest.title())
                .bannerFileName(bannerFileName)
                .bannerFileUrl(bannerFileUrl)
                .description(activityRequest.description())
                .location(activityRequest.location())
                .startDate(activityRequest.startDate())
                .endDate(activityRequest.endDate())
                .numberOfParticipants(activityRequest.numberOfParticipants())
                .canParticipantsInvite(activityRequest.canParticipantsInvite())
                .points(activityRequest.points())
                .regulationsFileName(regulationsFileName)
                .regulationsFileUrl(regulationsFileUrl)
                .registrationStartDate(activityRequest.registrationStartDate())
                .registrationEndDate(activityRequest.registrationEndDate())
                .activityType(activityType)
                .status(Status.PENDING.toString())
                .createdAt(LocalDate.now().toString())
                .organization(organization)
                .owner(owner)
                .build();

        activityRepo.save(activity);
        return activity;
    }

    @Transactional
    public Activity updateActivityByTitle(String title, ActivityRequest activityUpdateRequest) {
        Activity activity = activityRepo.findOneByTitle(title);
        if (activity == null) {
            throw new EntityNotFoundException("Activity with title " + title + " not found!");
        }
        activity.setTitle(activityUpdateRequest.title());
        activity.setDescription(activityUpdateRequest.description());
        activity.setLocation(activityUpdateRequest.location());
        activity.setNumberOfParticipants(activityUpdateRequest.numberOfParticipants());
        activity.setCanParticipantsInvite(activityUpdateRequest.canParticipantsInvite());
        activity.setPoints(activityUpdateRequest.points());

        String activityType = null;
        if (activityUpdateRequest.activityType() != null && !activityUpdateRequest.activityType().isBlank()) {
            activityType = activityUpdateRequest.activityType();
        }

        activity.setActivityType(activityType);

        // Check organization name
        String organizationRequestName = (activityUpdateRequest.organization() == null) ? "Other School-level Units" : activityUpdateRequest.organization();
        Organization organization = organizationRepo.findByName(organizationRequestName);

        if (organization == null) {
            throw new EntityNotFoundException("Organization with name " + organizationRequestName + " not found");
        }

        String organizationName = organization.getName();
        activity.setOrganization(organization);

        // Check validity of dates
        DateValidator.validateStartDateAndEndDate(activityUpdateRequest.startDate(), activityUpdateRequest.endDate());
        if (activityUpdateRequest.registrationStartDate() != null && activityUpdateRequest.registrationEndDate() != null) {
            DateValidator.validateStartDateAndEndDate(activityUpdateRequest.registrationStartDate(),
                                                      activityUpdateRequest.registrationEndDate());
        }

        activity.setStartDate(activityUpdateRequest.startDate());
        activity.setEndDate(activityUpdateRequest.endDate());
        activity.setRegistrationStartDate(activityUpdateRequest.registrationStartDate());
        activity.setRegistrationEndDate(activityUpdateRequest.registrationEndDate());

        // Update banner file
        MultipartFile bannerFile = activityUpdateRequest.bannerFile();
        String currentBannerFileName = activity.getBannerFileName();
        String currentBannerFileUrl = activity.getBannerFileUrl();
        String bannerFileName = null;
        String bannerFileUrl = null;

        if (bannerFile == null) {
            bannerFileName = currentBannerFileName;
            bannerFileUrl = currentBannerFileUrl;
        } else {
            // Check validity of banner file
            ActivityValidator.validateBannerFile(bannerFile);

            // Delete old file in S3
            if (currentBannerFileUrl != null) {
                s3Service.deleteFileForActivity(currentBannerFileUrl);
            }
            // Upload new file to S3
            bannerFileName = s3Service.uploadFileForActivity(bannerFile, organizationName);
            bannerFileUrl = s3Service.getFileUrl(bannerFileName, organizationName + "/");
        }

        activity.setBannerFileName(bannerFileName);
        activity.setBannerFileUrl(bannerFileUrl);

        // Update regulations file
        MultipartFile regulationsFile = activityUpdateRequest.regulationsFile();
        String currentRegulationsFileUrl = activity.getRegulationsFileUrl();
        String regulationsFileName = null;
        String regulationsFileUrl = null;

        if (regulationsFile != null) {
            // Check validity of regulations file
            ActivityValidator.validateRegulationsFile(regulationsFile);

            // Delete old file in S3
            if (currentRegulationsFileUrl != null) {
                s3Service.deleteFileForActivity(currentRegulationsFileUrl);
            }
            // Upload new file to S3
            regulationsFileName = s3Service.uploadFileForActivity(regulationsFile, organizationName);
            regulationsFileUrl = s3Service.getFileUrl(regulationsFileName, organizationName + "/");
        }

        activity.setRegulationsFileName(regulationsFileName);
        activity.setRegulationsFileUrl(regulationsFileUrl);

        activityRepo.save(activity);
        return activity;
    }

    public Activity findOneActivityByTitle(String title) {
        Activity post = activityRepo.findOneByTitle(title);
        if (post != null) return post;
        throw new EntityNotFoundException("Activity with title " + title + " not found");
    }

    public Page<Activity> findActivityWithPagination(Specification<Activity> activitySpec, int offset, int pageSize, String order, String getMine) {
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("startDate").ascending() : Sort.by("startDate").descending();
        Pageable pageRequest = PageRequest.of(offset - 1, pageSize, sort);

        Page<Activity> activities = null;
        if (activitySpec == null) {
            activities = activityRepo.findAll(pageRequest);
        } else {
            activities = activityRepo.findAll(activitySpec, pageRequest);
        }

        if (getMine.equals("TRUE")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> user = userRepo.findByEmail(userEmail);
            if (user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + userEmail + " not found");
            }

            List<UUID> activityIds = activityParticipationRepo.findActivityIdByUserId(user.get().getId());
            if (activityIds.isEmpty()) {
                return null;
            }

            List<Activity> filteredActivities = activities.getContent().stream()
                    .filter(activity -> Arrays.stream(activityIds.toArray()).anyMatch(id -> id.equals(activity.getId()))).toList();
            return new PageImpl<>(filteredActivities, pageRequest, activities.getTotalElements());
        }

        return activities;
    }

    @Transactional
    public ActivityParticipation registerActivity(ActivityRegistrationRequest activityRegistrationRequest) {
        Activity activity = activityRepo.findOneById(activityRegistrationRequest.activityId());
        Optional<User> user = userRepo.findByEmail(activityRegistrationRequest.userEmail());

        if (activity == null) {
            throw new EntityNotFoundException("Activity with ID " + activityRegistrationRequest.activityId() + " not found");
        }
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + activityRegistrationRequest.userEmail() + " not found");
        }

        ActivityParticipation activityParticipation = new ActivityParticipation();
        ActivityParticipationId activityParticipationId = new ActivityParticipationId();

        activityParticipationId.setActivityId(activity.getId());
        activityParticipationId.setUserId(user.get().getId());
        activityParticipation.setActivityParticipationId(activityParticipationId);

        activityParticipation.setActivity(activity);
        activityParticipation.setUser(user.get());

        try {
            activityParticipationRepo.save(activityParticipation);
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
        return activityParticipation;
    }

    @Transactional
    public ActivityParticipation deregisterActivity(ActivityRegistrationRequest activityRegistrationRequest) {
        Activity activity = activityRepo.findOneById(activityRegistrationRequest.activityId());
        Optional<User> user = userRepo.findByEmail(activityRegistrationRequest.userEmail());

        if (activity == null) {
            throw new EntityNotFoundException("Activity with ID " + activityRegistrationRequest.activityId() + " not found");
        }
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with ID " + activityRegistrationRequest.userEmail() + " not found");
        }

        ActivityParticipationId activityParticipationId = new ActivityParticipationId().builder().activityId(activity.getId()).userId(user.get().getId()).build();
        Optional<ActivityParticipation> activityParticipation = activityParticipationRepo.findById(activityParticipationId);
        if (activityParticipation.isEmpty()) {
            throw new EntityNotFoundException("No activity with ID " + activityRegistrationRequest.activityId() + " associated with user " + activityRegistrationRequest.userEmail() + " found");
        }

        activityParticipationRepo.delete(activityParticipation.get());

        return activityParticipation.get();
    }

}
