package com.dalv.bksims.services.social_points_management;

import com.dalv.bksims.exceptions.ActivityTitleAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRequest;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.entities.social_points_management.Organization;
import com.dalv.bksims.models.enums.Status;
import com.dalv.bksims.models.repositories.social_points_management.ActivityRepository;
import com.dalv.bksims.models.repositories.social_points_management.OrganizationRepository;
import com.dalv.bksims.services.common.S3Service;
import com.dalv.bksims.validations.ActivityValidator;
import com.dalv.bksims.validations.DateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepo;

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

        String organizationName = organization.getName();

        // Check validity of dates
        DateValidator.validateStartDateAndEndDate(activityRequest.startDate(), activityRequest.endDate());
        if (activityRequest.registrationStartDate() != null && activityRequest.registrationEndDate() != null) {
            DateValidator.validateStartDateAndEndDate(activityRequest.registrationStartDate(), activityRequest.registrationEndDate());
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
                .activityType(activityRequest.activityType())
                .status(Status.PENDING)
                .createdAt(LocalDate.now().toString())
                .organization(organization)
                .build();

        activityRepo.save(activity);
        return activity;
    }

    @Transactional
    public Activity updateActivityInfo(String title, ActivityRequest activityUpdateRequest) {
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

        // Check organization name
        String organizationUpdateRequestName = (activityUpdateRequest.organization() == null) ? activity.getOrganization().getName() : activityUpdateRequest.organization();
        Organization organization = organizationRepo.findByName(organizationUpdateRequestName);

        if (organization == null) {
            throw new EntityNotFoundException("Organization with name " + organizationUpdateRequestName + " not found");
        }

        String organizationName = organization.getName();
        activity.setOrganization(organization);

        // Check validity of dates
        DateValidator.validateStartDateAndEndDate(activityUpdateRequest.startDate(), activityUpdateRequest.endDate());
        if (activityUpdateRequest.registrationStartDate() != null && activityUpdateRequest.registrationEndDate() != null) {
            DateValidator.validateStartDateAndEndDate(activityUpdateRequest.registrationStartDate(), activityUpdateRequest.registrationEndDate());
        }

        activity.setStartDate(activityUpdateRequest.startDate());
        activity.setEndDate(activityUpdateRequest.endDate());
        activity.setRegistrationStartDate(activityUpdateRequest.registrationStartDate());
        activity.setRegistrationEndDate(activityUpdateRequest.registrationEndDate());

        // Check validity of banner file
        MultipartFile bannerFile = activityUpdateRequest.bannerFile();
        ActivityValidator.validateBannerFile(bannerFile);

        // Delete old file in S3
        String currentBannerFileUrl = activity.getBannerFileUrl();
        if (currentBannerFileUrl != null) {
            s3Service.deleteFileForActivity(currentBannerFileUrl);
        }
        // Upload new file to S3
        String bannerFileName = s3Service.uploadFileForActivity(bannerFile, organizationName);
        String bannerFileUrl = s3Service.getFileUrl(bannerFileName, organizationName + "/");

        activity.setBannerFileName(bannerFileName);
        activity.setBannerFileUrl(bannerFileUrl);

        // Check validity of regulations file
        if (activityUpdateRequest.regulationsFile() != null) {
            MultipartFile regulationsFile = activityUpdateRequest.regulationsFile();
            ActivityValidator.validateRegulationsFile(regulationsFile);

            // Delete old file in S3
            String currentRegulationsFileUrl = activity.getRegulationsFileUrl();
            if (currentRegulationsFileUrl != null) {
                s3Service.deleteFileForActivity(currentRegulationsFileUrl);
            }
            // Upload new file to S3
            String regulationsFileName = s3Service.uploadFileForActivity(regulationsFile, organizationName);
            String regulationsFileUrl = s3Service.getFileUrl(regulationsFileName, organizationName + "/");

            activity.setRegulationsFileName(regulationsFileName);
            activity.setRegulationsFileUrl(regulationsFileUrl);
        }

        activityRepo.save(activity);
        return activity;
    }

    public Activity findOneActivityByTitle(String title) {
        Activity post = activityRepo.findOneByTitle(title);
        if (post != null) return post;
        throw new EntityNotFoundException("Activity with title " + title + " not found");
    }

    public Page<Activity> findActivityWithPagination(Specification<Activity> activitySpec, int offset, int pageSize, String order) {
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("startDate").ascending() : Sort.by("startDate").descending();
        return  activityRepo.findAll(activitySpec, PageRequest.of(offset - 1, pageSize, sort));
    };
}
