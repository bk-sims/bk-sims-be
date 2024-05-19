package com.dalv.bksims.controllers.social_points_management;

import com.dalv.bksims.models.dtos.social_points_management.AcceptInvitationResponse;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRegistrationRequest;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRequest;
import com.dalv.bksims.models.dtos.social_points_management.ParticipantResponse;
import com.dalv.bksims.models.dtos.social_points_management.RemoveParticipantRequest;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.entities.social_points_management.ActivityInvitation;
import com.dalv.bksims.models.entities.social_points_management.ActivityInvitationId;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import com.dalv.bksims.services.social_points_management.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThan;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Activity")
@Controller
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Activity> createActivity(@ModelAttribute @Valid ActivityRequest activityRequest) throws Exception {
        Activity activity = activityService.createActivity(activityRequest);
        return new ResponseEntity<>(activity, HttpStatus.CREATED);
    }

    @GetMapping("/{title}")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Activity> findOneActivityByTitle(@PathVariable String title) {
        Activity activity = activityService.findOneActivityByTitle(title);
        return new ResponseEntity<>(activity, HttpStatus.OK);
    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Page<Activity>> findActivityWithPagination(
            @PathVariable int offset,
            @PathVariable int pageSize,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") String order,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
            @Or({
                    @Spec(path = "location", params = "query", spec = Like.class),
                    @Spec(path = "title", params = "query", spec = Like.class)
            })
            Specification<Activity> activitySpec,
            @Spec(path = "endDate", constVal = "#{T(java.time.LocalDate).now()}", valueInSpEL = true, spec = LessThanOrEqual.class)
            Specification<Activity> AcitivityWithClosedStatus,
            @Spec(path = "endDate", constVal = "#{T(java.time.LocalDate).now()}", valueInSpEL = true, spec = GreaterThan.class)
            Specification<Activity> AcitivityWithOpenStatus
    ) {
        return switch (status) {
            case "OPEN" -> {
                Specification<Activity> finalActivitySpec = activitySpec == null ? AcitivityWithOpenStatus : activitySpec.and(
                        AcitivityWithOpenStatus);
                Page<Activity> activitiesWithPaginationOpen = activityService.findActivityWithPagination(
                        finalActivitySpec, offset, pageSize, order, type);
                yield new ResponseEntity<>(activitiesWithPaginationOpen, HttpStatus.OK);
            }
            case "CLOSED" -> {
                Specification<Activity> finalActivitySpec = activitySpec == null ? AcitivityWithClosedStatus : activitySpec.and(
                        AcitivityWithClosedStatus);
                Page<Activity> activitiesWithPaginationClosed = activityService.findActivityWithPagination(
                        finalActivitySpec, offset, pageSize, order, type);
                yield new ResponseEntity<>(activitiesWithPaginationClosed, HttpStatus.OK);
            }
            default -> {
                Page<Activity> activitiesWithPagination = activityService.findActivityWithPagination(activitySpec,
                        offset, pageSize,
                        order, type);
                yield new ResponseEntity<>(activitiesWithPagination, HttpStatus.OK);
            }
        };
    }

    @PatchMapping("/{title}")
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Activity> updateActivityByTitle(
            @PathVariable String title,
            @ModelAttribute @Valid ActivityRequest activityUpdateRequest
    ) {
        Activity activity = activityService.updateActivityByTitle(title, activityUpdateRequest);
        return new ResponseEntity<>(activity, HttpStatus.OK);
    }

    @PostMapping("/register")
    @Secured({"ROLE_STUDENT"})
    public ResponseEntity<ActivityParticipationId> registerActivity(@RequestBody Map<String, String> payload) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        ActivityRegistrationRequest activityRegistrationRequest = new ActivityRegistrationRequest(
                UUID.fromString(payload.get("activityId")), userEmail);
        ActivityParticipation activityParticipation = activityService.registerActivity(activityRegistrationRequest);
        return new ResponseEntity<>(activityParticipation.getActivityParticipationId(), HttpStatus.OK);
    }

    @PostMapping("/deregister")
    @Secured({"ROLE_STUDENT"})
    public ResponseEntity<ActivityParticipationId> deregisterActivity(@RequestBody Map<String, String> payload) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        ActivityRegistrationRequest activityRegistrationRequest = new ActivityRegistrationRequest(
                UUID.fromString(payload.get("activityId")), userEmail);
        ActivityParticipation activityParticipation = activityService.deregisterActivity(activityRegistrationRequest);
        return new ResponseEntity<>(activityParticipation.getActivityParticipationId(), HttpStatus.OK);
    }

    @GetMapping("/{title}/participants")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<ParticipantResponse>> getParticipantsByActivityTitle(@PathVariable String title) {
        List<ParticipantResponse> participants = activityService.getParticipantsByActivityTitle(title);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @DeleteMapping("/participants")
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<ParticipantResponse>> removeParticipantsByActivityId(@RequestBody RemoveParticipantRequest request) {
        UUID activityId = UUID.fromString(request.activityId());

        List<ParticipantResponse> participants = activityService.removeParticipantsByActivityId(
                activityId, request.participantsIds());
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }

    @GetMapping("/{title}/invitations")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<ActivityInvitation>> getInvitationsByActivityTitle(
            @PathVariable String title
    ) throws Exception {
        List<ActivityInvitation> invitations = activityService.getInvitationsByActivityTitle(title);
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    @PostMapping("/{title}/invitations")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<ActivityInvitation>> inviteUserToActivity(
            @PathVariable String title,
            @RequestBody Map<String, String> payload
    ) {
        UUID userId = UUID.fromString(payload.get("userId"));

        List<ActivityInvitation> invitations = activityService.inviteUserToActivity(title, userId);

        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    @PostMapping("/invitations/accept")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<AcceptInvitationResponse> acceptInvitation(
            @RequestBody Map<String, String> payload,
            HttpServletResponse response
    ) {
        UUID activityId = UUID.fromString(payload.get("activityId"));
        UUID userId = UUID.fromString(payload.get("userId"));
        ActivityInvitationId activityInvitationId = ActivityInvitationId.builder()
                .activityId(activityId)
                .userId(userId)
                .build();
        String invitationLink = payload.get("invitationLink");
        AcceptInvitationResponse acceptInvitationResponse = activityService.acceptInvitation(activityInvitationId,
                invitationLink);
        return new ResponseEntity<>(acceptInvitationResponse, HttpStatus.OK);
    }

    @PostMapping("/approve")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Activity> approveActivity(@RequestBody Map<String, String> payload) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UUID activityId = UUID.fromString(payload.get("activityId"));
        Activity activity = activityService.approveActivity(activityId, userEmail);
        return new ResponseEntity<>(activity, HttpStatus.OK);
    }

    @PostMapping("/reject")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Activity> rejectActivity(@RequestBody Map<String, String> payload) throws Exception {
        UUID activityId = UUID.fromString(payload.get("activityId"));
        Activity activity = activityService.rejectActivity(activityId);
        return new ResponseEntity<>(activity, HttpStatus.OK);
    }
}
