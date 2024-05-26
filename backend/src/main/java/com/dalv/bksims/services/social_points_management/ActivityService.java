package com.dalv.bksims.services.social_points_management;

import com.dalv.bksims.exceptions.ActivityStatusViolationException;
import com.dalv.bksims.exceptions.ActivityTitleAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.exceptions.NoPermissionException;
import com.dalv.bksims.exceptions.ParticipantsNotFoundException;
import com.dalv.bksims.models.dtos.social_points_management.AcceptInvitationResponse;
import com.dalv.bksims.models.dtos.social_points_management.ActivityEvidenceGetResponse;
import com.dalv.bksims.models.dtos.social_points_management.ActivityEvidenceRequest;
import com.dalv.bksims.models.dtos.social_points_management.ActivityHistoryResponse;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRegistrationRequest;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRequest;
import com.dalv.bksims.models.dtos.social_points_management.ParticipantResponse;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.entities.social_points_management.ActivityInvitation;
import com.dalv.bksims.models.entities.social_points_management.ActivityInvitationId;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import com.dalv.bksims.models.entities.social_points_management.Organization;
import com.dalv.bksims.models.entities.user.Role;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.enums.InvitationStatus;
import com.dalv.bksims.models.enums.Status;
import com.dalv.bksims.models.repositories.social_points_management.ActivityInvitationRepository;
import com.dalv.bksims.models.repositories.social_points_management.ActivityParticipationRepository;
import com.dalv.bksims.models.repositories.social_points_management.ActivityRepository;
import com.dalv.bksims.models.repositories.social_points_management.OrganizationRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import com.dalv.bksims.services.common.S3Service;
import com.dalv.bksims.services.email.EmailService;
import com.dalv.bksims.validations.ActivityValidator;
import com.dalv.bksims.validations.DateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepo;

    private final UserRepository userRepo;

    private final ActivityParticipationRepository activityParticipationRepo;

    private final ActivityInvitationRepository activityInvitationRepo;

    private final OrganizationRepository organizationRepo;

    private final S3Service s3Service;

    private final EmailService emailService;

    @Value("${application.frontend-base-url}")
    private String frontendBaseUrl;

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
                () -> new EntityNotFoundException("User with email " + userEmail + " not found"));

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

        // Add owner as a participant of the activity
        addUserToActivityParticipation(owner, activity);
        return activity;
    }

    @Transactional
    public ActivityParticipation addUserToActivityParticipation(User user, Activity activity) {
        ActivityParticipation activityParticipation = new ActivityParticipation();
        ActivityParticipationId activityParticipationId = new ActivityParticipationId();

        activityParticipationId.setActivityId(activity.getId());
        activityParticipationId.setUserId(user.getId());
        activityParticipation.setActivityParticipationId(activityParticipationId);
        activityParticipation.setPointsApproved(0);
        activityParticipation.setEvidenceUrl(null);

        activityParticipation.setActivity(activity);
        activityParticipation.setUser(user);

        return activityParticipationRepo.save(activityParticipation);
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

        if (Objects.equals(activity.getStatus(), "REJECTED")) {
            activity.setStatus("PENDING");
        }

        activityRepo.save(activity);
        return activity;
    }

    public Activity findOneActivityByTitle(String title) {
        Activity activity = activityRepo.findOneByTitle(title);
        if (activity != null) return activity;
        throw new EntityNotFoundException("Activity with title " + title + " not found");
    }

    public Page<Activity> findActivityWithPagination(
            Specification<Activity> activitySpec,
            int offset,
            int pageSize,
            String order,
            String type
    ) {
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("startDate").ascending() : Sort.by(
                "startDate").descending();
        Pageable pageRequest = PageRequest.of(offset - 1, pageSize, sort);

        Page<Activity> activities = null;
        if (activitySpec == null) {
            activities = activityRepo.findAll(pageRequest);
        } else {
            activities = activityRepo.findAll(activitySpec, pageRequest);
        }

        if (type.equals("MY")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            Optional<User> user = userRepo.findByEmail(userEmail);
            if (user.isEmpty()) {
                throw new EntityNotFoundException("User with email " + userEmail + " not found");
            }

            List<UUID> activityIds = activityParticipationRepo.findActivityIdByUserId(user.get().getId());
            if (activityIds.size() == 0) {
                return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
            }

            List<Activity> filteredActivities = activities.getContent().stream()
                    .filter(activity -> Arrays.stream(activityIds.toArray())
                            .anyMatch(id -> id.equals(activity.getId()))).toList();
            return new PageImpl<>(filteredActivities, pageRequest, activities.getTotalElements());
        }

        return activities;
    }

    @Transactional
    public ActivityParticipation registerActivity(ActivityRegistrationRequest activityRegistrationRequest) {
        Activity activity = activityRepo.findOneById(activityRegistrationRequest.activityId());
        Optional<User> user = userRepo.findByEmail(activityRegistrationRequest.userEmail());

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with ID " + activityRegistrationRequest.activityId() + " not found");
        }
        if (user.isEmpty()) {
            throw new EntityNotFoundException(
                    "User with email " + activityRegistrationRequest.userEmail() + " not found");
        }

        return addUserToActivityParticipation(user.get(), activity);
    }

    @Transactional
    public ActivityParticipation deregisterActivity(ActivityRegistrationRequest activityRegistrationRequest) {
        Activity activity = activityRepo.findOneById(activityRegistrationRequest.activityId());
        Optional<User> user = userRepo.findByEmail(activityRegistrationRequest.userEmail());

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with ID " + activityRegistrationRequest.activityId() + " not found");
        }
        if (user.isEmpty()) {
            throw new EntityNotFoundException(
                    "User with email " + activityRegistrationRequest.userEmail() + " not found");
        }

        ActivityParticipationId activityParticipationId = new ActivityParticipationId().builder()
                .activityId(activity.getId())
                .userId(user.get().getId())
                .build();
        Optional<ActivityParticipation> activityParticipation = activityParticipationRepo.findById(
                activityParticipationId
        );
        if (activityParticipation.isEmpty()) {
            throw new EntityNotFoundException(
                    "No activity with ID " + activityRegistrationRequest.activityId() + " associated with user " + activityRegistrationRequest.userEmail() + " found");
        }

        activityParticipationRepo.delete(activityParticipation.get());

        return activityParticipation.get();
    }

    public List<ParticipantResponse> getParticipantsByActivityTitle(String title) {
        Activity activity = activityRepo.findOneByTitle(title);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with title " + title + " not found");
        }

        return activityParticipationRepo.findParticipantsByActivityTitle(title);
    }

    @Transactional
    public List<ParticipantResponse> removeParticipantsByActivityId(UUID activityId, List<UUID> participantsIds) {
        Activity activity = activityRepo.findOneById(activityId);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with id " + activityId + " not found");
        }

        List<ParticipantResponse> participants = activityParticipationRepo.findParticipantsByActivityIdByIdIn(
                activityId, participantsIds
        );

        // Check if all requested IDs were found
        List<UUID> foundIds = participants.stream()
                .map(participantResponse -> participantResponse.user().getId())
                .toList();

        List<UUID> notFoundIds = participantsIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new ParticipantsNotFoundException("Participants not found for IDs: " + notFoundIds);
        }

        activityParticipationRepo.deleteByUserIdIn(foundIds);

        return activityParticipationRepo.findParticipantsByActivityId(
                activityId);
    }

    public List<ActivityInvitation> getInvitationsByActivityTitle(String title) {
        Activity activity = activityRepo.findOneByTitle(title);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with title " + title + " not found");
        }

        return activityInvitationRepo.findInvitationsByActivityTitle(title);
    }

    @Transactional
    public List<ActivityInvitation> inviteUserToActivity(String title, UUID userId) {
        Activity activity = activityRepo.findOneByTitle(title);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with title " + title + " not found");
        }

        User user = userRepo.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with ID " + userId + " not found"));

        // Check if the user exists in participation table
        boolean isUserInParticipants = activityParticipationRepo.existsByUserIdAndActivityId(userId, activity.getId());

        if (isUserInParticipants) {
            throw new EntityAlreadyExistsException(
                    "User with email " + user.getEmail() + " already participates in the activity");
        }

        // Add invitation to the activity
        ActivityInvitationId activityInvitationId = ActivityInvitationId.builder()
                .activityId(activity.getId())
                .userId(userId)
                .build();

        ActivityInvitation existingInvitation = activityInvitationRepo.findOneByActivityInvitationId(
                activityInvitationId);

        if (existingInvitation != null) {
            throw new EntityAlreadyExistsException(
                    "Invitation between the user and the activity already exists");
        }


        UUID randomUUID = UUID.randomUUID();
        String invitationLink = frontendBaseUrl + "/activities/invitations/accept/" + activity.getId() + "/" + randomUUID.toString();

        ActivityInvitation invitation = activityInvitationRepo.save(ActivityInvitation.builder()
                .activityInvitationId(activityInvitationId)
                .activity(activity)
                .user(user)
                .status(InvitationStatus.PENDING.toString())
                .invitationLink(invitationLink)
                .expired(false)
                .build());

        // Send email to the user
        String emailSubject = "[BKSims] Invitation to an activity";
        String emailContent = "<html><body><p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>"
                + "<p>You have been invited to join activity <b>" + activity.getTitle() + "</b>. Please click the link below to accept the invitation:</p>"
                + "<a href=\"" + invitationLink + "\">" + invitationLink + "</a>"
                + "<p>Best regards,<br/>BKSims</p></body></html>";
        emailService.sendHtmlEmail(user.getEmail(), emailSubject, emailContent);

        return activityInvitationRepo.findInvitationsByActivityTitle(activity.getTitle());
    }

    @Transactional
    public AcceptInvitationResponse acceptInvitation(ActivityInvitationId activityInvitationId, String invitationLink) {
        ActivityInvitation invitation = activityInvitationRepo.findOneByActivityInvitationIdAndInvitationLink(
                activityInvitationId, invitationLink);

        if (invitation == null) {
            return new AcceptInvitationResponse(400,
                    "Invitation not found",
                    "/activities/invitations/invalid-invitation");
        }

        Activity activity = invitation.getActivity();
        User user = invitation.getUser();

        if (activity == null) {
            return new AcceptInvitationResponse(400,
                    "Cannot find activity that you are invited to",
                    "/activities/invitations/invalid-invitation");
        }

        if (user == null) {
            return new AcceptInvitationResponse(400,
                    "Cannot find user of the invitation",
                    "/activities/invitations/invalid-invitation");
        }


        if (Objects.equals(invitation.getStatus(), InvitationStatus.ACCEPTED.toString())) {
            return new AcceptInvitationResponse(400,
                    "Invitation with to activity " + activityInvitationId.getActivityId() + " has already been invoked",
                    "/activities/invitations/invalid-invitation");
        }

        invitation.setStatus(InvitationStatus.ACCEPTED.toString());
        activityInvitationRepo.save(invitation);

        addUserToActivityParticipation(user, activity);

        return new AcceptInvitationResponse(200, "Invitation accepted",
                "/activities/" + activity.getTitle());
    }

    @Transactional
    public Activity approveActivity(UUID activityId, String userEmail) {
        Activity activity = activityRepo.findOneById(activityId);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with ID " + activityId + " not found");
        }

        Optional<User> user = userRepo.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with email " + userEmail + " not found");
        }
        if (user.get().getRole() != Role.ADMIN) {
            throw new NoPermissionException("User must be admin to approve activity");
        }

        if (Objects.equals(activity.getStatus(), "OPEN")) {
            throw new ActivityStatusViolationException(
                    "Admin cannot approve activity with status OPEN");
        }

        activity.setStatus("OPEN");
        return activityRepo.save(activity);
    }

    @Transactional
    public Activity rejectActivity(UUID activityId) {
        Activity activity = activityRepo.findOneById(activityId);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with ID " + activityId + " not found");
        }

        if (Objects.equals(activity.getStatus(), "REJECTED")) {
            throw new ActivityStatusViolationException(
                    "Admin cannot reject activity with status REJECTED");
        }

        activity.setStatus("REJECTED");
        return activityRepo.save(activity);
    }

    @Transactional
    public ActivityParticipation uploadEvidence(ActivityEvidenceRequest activityEvidenceRequest, String email) {
        UUID userId = userRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with " + email + " not found")).getId();
        UUID activityId = activityEvidenceRequest.activityId();

        ActivityParticipationId activityParticipationId = ActivityParticipationId.builder().userId(userId).activityId(activityId).build();
        ActivityParticipation activityParticipation = activityParticipationRepo.findById(activityParticipationId).orElseThrow(
                () -> new EntityNotFoundException("No user with email" + email + "participated in the activity with the id " + activityId));

        MultipartFile evidenceFile = activityEvidenceRequest.file();
        if (evidenceFile != null) {
            ActivityValidator.validateEvidenceFile(evidenceFile);
        }

        String evidenceFileName = s3Service.uploadFileForActivityEvidence(evidenceFile);
        String evidenceFileUrl = s3Service.getFileUrl(evidenceFileName, "activity_evidence/");

        String currentEvidenceFileUrl = activityParticipation.getEvidenceUrl();

        if (currentEvidenceFileUrl != null) {
            s3Service.deleteFileForActivity(currentEvidenceFileUrl);
        }
        activityParticipation.setEvidenceUrl(evidenceFileUrl);
        return activityParticipationRepo.save(activityParticipation);
    }

    public ActivityEvidenceGetResponse getEvidence(UUID activityId, String email) {
        UUID userId = userRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with " + email + " not found")).getId();
        ActivityParticipationId activityParticipationId = ActivityParticipationId.builder().userId(userId).activityId(activityId).build();

        ActivityParticipation activityParticipation = activityParticipationRepo.findById(activityParticipationId).orElseThrow(
                () -> new EntityNotFoundException("No user with email" + email + "participated in the activity with the id " + activityId));
        return new ActivityEvidenceGetResponse(userId, activityId, activityParticipation.getEvidenceUrl());
    }

    @Transactional
    public List<ParticipantResponse> approvePoints(UUID activityId, List<UUID> participantsIds) {
        Activity activity = activityRepo.findOneById(activityId);

        if (activity == null) {
            throw new EntityNotFoundException(
                    "Activity with id " + activityId + " not found");
        }

        Integer pointsApproved = activity.getPoints();

        List<ParticipantResponse> participants = activityParticipationRepo.findParticipantsByActivityIdByIdIn(
                activityId, participantsIds
        );

        List<UUID> foundIds = participants.stream()
                .map(participantResponse -> participantResponse.user().getId())
                .toList();

        List<UUID> notFoundIds = participantsIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new ParticipantsNotFoundException("Participants not found for IDs: " + notFoundIds);
        }

        for (UUID participantId : participantsIds) {
            ActivityParticipationId activityParticipationId = ActivityParticipationId.builder().userId(participantId).activityId(activityId).build();
            ActivityParticipation activityParticipation = activityParticipationRepo.findById(activityParticipationId).orElseThrow(
                    () -> new EntityNotFoundException("No students with the id" + participantId + "participated in the activity with the id " + activityId));
            activityParticipation.setPointsApproved(pointsApproved);
            activityParticipationRepo.save(activityParticipation);
        }
        return activityParticipationRepo.findParticipantsByActivityId(
                activityId);
    }

    public List<ActivityHistoryResponse> findActivityHistoryBasedOnUserId(UUID userId, String email) {
        if (email != null) {
            userId = userRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User with " + email + " not found")).getId();
        }
        return activityParticipationRepo.findActivityHistoryByUserId(userId);
    }
}
