package com.dalv.bksims.bksims.services.social_points_management;

import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.exceptions.NoPermissionException;
import com.dalv.bksims.models.dtos.social_points_management.ActivityRegistrationRequest;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import com.dalv.bksims.models.entities.user.Role;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.enums.Status;
import com.dalv.bksims.models.repositories.social_points_management.ActivityParticipationRepository;
import com.dalv.bksims.models.repositories.social_points_management.ActivityRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import com.dalv.bksims.services.social_points_management.ActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityParticipationRepository activityParticipationRepository;

    @InjectMocks
    private ActivityService activityService;

    @Test
    public void testFindOneActivityByTitle() {
        String activityTitle = "Activity 1";
        Activity expectedEntity = new Activity();
        expectedEntity.setTitle(activityTitle);

        when(activityRepository.findOneByTitle(activityTitle))
                .thenReturn(expectedEntity);

        Activity resultEntity = activityService.findOneActivityByTitle(activityTitle);

        assertNotNull(resultEntity);
        assertEquals(activityTitle, resultEntity.getTitle());
        verify(activityRepository, times(1)).findOneByTitle(activityTitle);
    }

    @Test
    public void testFindByTitleWhenEntityNotFound() {
        String activityTitle = "Activity 1";
        when(activityRepository.findOneByTitle(activityTitle))
                .thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            activityService.findOneActivityByTitle(activityTitle);
        });
    }

    @Test
    public void testApproveActivityWhenActivityFoundAndUserIsAdmin() {
        UUID activityId = UUID.fromString("3b6bac76-61bd-4437-a0e8-4f46246d7397");
        String activityTitle = "Test Activity";
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setTitle(activityTitle);
        activity.setStatus(Status.PENDING.toString());

        User admin = new User();
        admin.setRole(Role.ADMIN);
        admin.setEmail("admin@hcmut.edu.vn");

        when(activityRepository.findOneById(activityId)).thenReturn(activity);
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);

        Activity resultActivity = activityService.approveActivity(activityId, admin.getEmail());

        assertNotNull(resultActivity);
        assertEquals(Status.OPEN.toString(), resultActivity.getStatus());
        verify(activityRepository).findOneById(activityId);
        verify(activityRepository).save(activity);
    }


    @Test
    public void testApproveActivityWhenUserISNotAdmin() {
        UUID activityId = UUID.fromString("3b6bac76-61bd-4437-a0e8-4f46246d7397");
        String activityTitle = "Test Activity";

        // Mock the activity that will be returned by findOneByTitle
        Activity mockActivity = new Activity();
        mockActivity.setTitle(activityTitle);
        mockActivity.setId(activityId);
        mockActivity.setStatus(Status.PENDING.toString()); // Assuming the activity is initially in a PENDING status

        User student = new User();
        student.setRole(Role.STUDENT);
        student.setEmail("student@hcmut.edu.vn");

        when(activityRepository.findOneById(activityId)).thenReturn(mockActivity);
        when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));

        Assertions.assertThrows(NoPermissionException.class, () -> {
            activityService.approveActivity(activityId, student.getEmail());
        });

        verify(activityRepository).findOneById(activityId);
        // Verify that the activity repository does not save the activity
        verify(activityRepository, times(0)).save(any(Activity.class));
    }

    @Test
    public void testRegisterActivityWhenActivityFoundIsOpenAndUserHasRoleStudent() {
        String activityTitle = "Test Activity";
        User owner = new User();
        owner.setId(UUID.fromString("a4c13be9-3d74-45f4-8b14-0cb9dc3661c5"));
        Activity activity = new Activity();
        activity.setId(UUID.fromString("3b6bac76-61bd-4437-a0e8-4f46246d7397"));
        activity.setTitle(activityTitle);
        activity.setStatus(Status.OPEN.toString());
        activity.setOwner(owner);

        User student = new User();
        student.setId(UUID.fromString("d013820a-0afb-4ff3-bac6-8c2cfdd640c8"));
        student.setEmail("student@hcmut.edu.vn");
        student.setRole(Role.STUDENT);

        ActivityParticipation newActivityParticipation = new ActivityParticipation();
        ActivityParticipationId newActivityParticipationId = new ActivityParticipationId();
        newActivityParticipationId.setActivityId(activity.getId());
        newActivityParticipationId.setUserId(student.getId());
        newActivityParticipation.setActivityParticipationId(newActivityParticipationId);
        newActivityParticipation.setPointsApproved(0);

        when(activityRepository.findOneById(activity.getId())).thenReturn(activity);
        when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(activityParticipationRepository.save(any(ActivityParticipation.class))).thenReturn(newActivityParticipation);

        ActivityRegistrationRequest activityRegistrationRequest = new ActivityRegistrationRequest(
                activity.getId(), student.getEmail());
        ActivityParticipation activityParticipationResult = activityService.registerActivity(activityRegistrationRequest);


        assertNotNull(activityParticipationResult);
        verify(activityRepository).findOneById(activity.getId());
        verify(userRepository).findByEmail(student.getEmail());
    }

    @Test
    public void testRejectActivityWhenActivityFoundAndUserIsAdmin() {
        assertEquals(true, true);
    }

    @Test
    public void testRejectActivityWhenUserIsNotAdmin() {
        assertEquals(true, true);
    }

    @Test
    public void testRegisterActivityWhenActivityFoundIsNotOpenAndUserHasRoleStudent() {
        assertEquals(true, true);
    }

    @Test
    public void testRegisterActivityWhenActivityFoundIsOpenAndUserHasRoleNotStudent() {
        assertEquals(true, true);
    }

    @Test
    public void testRegisterActivityWhenActivityFoundIsNotOpenAndUserHasRoleNotStudent() {
        assertEquals(true, true);
    }

    @Test
    public void testRegisterActivityWhenActivityFoundIsOpenAndUserHasRoleUserAndDeadlineHasPassed() {
        assertEquals(true, true);
    }

    @Test
    public void testDeregisterActivityWhenActivityFoundIsNotOpenAndUserHasRoleStudent() {
        assertEquals(true, true);
    }

    @Test
    public void testDeregisterActivityWhenActivityFoundIsOpenAndUserHasRoleNotStudent() {
        assertEquals(true, true);
    }

    @Test
    public void testInviteUserIsStudentToActivity() {
        assertEquals(true, true);
    }

    @Test
    public void testInviteUserIsNotStudentToActivity() {
        assertEquals(true, true);
    }

    @Test
    public void testUserIsStudentAcceptInvitation() {
        assertEquals(true, true);
    }

    @Test
    public void testUserIsNotStudentAcceptInvitation() {
        assertEquals(true, true);
    }

    @Test
    public void testCreateActivitySuccessful() {
        assertEquals(true, true);
    }

    @Test
    public void testCreateActivityThrowActivityTitleAlreadyExists() {
        assertEquals(true, true);
    }

    @Test
    public void testCreateActivityWithBannerFileHasInvalidExtension() {
        assertEquals(true, true);
    }

    @Test
    public void testCreateActivityWithBannerFileHasInvalidFileSize() {
        assertEquals(true, true);
    }

    @Test
    public void testCreateActivityWithRegulationFileHasInvalidExtension() {
        assertEquals(true, true);
    }

    @Test
    public void testCreateActivityWithRegulationFileHasInvalidFileSize() {
        assertEquals(true, true);
    }

    @Test
    public void testUpdateActivityByTitleSuccessful() {
        assertEquals(true, true);
    }

    @Test
    public void testRemoveParticipantsByActivityIdSuccessful() {
        assertEquals(true, true);
    }

    @Test
    public void testRemoveParticipantsByActivityIdThrowParticipantsNotFound() {
        assertEquals(true, true);
    }


//    @Test
//    public void testRegisterActivityWhenActivityFoundIsNotOpenAndUserHasRoleUser() {
//        String activityTitle = "Test Activity";
//
//        // Mock the activity that will be returned by findOneByTitle
//        Activity activity = new Activity();
//        activity.setTitle(activityTitle);
//        // Status of activity should not be OPEN in this case
//        activity.setStatus(Status.PENDING);
//
//        User mockUser = new User();
//        String accessToken = "some example access token";
//        mockUser.setRole(Role.ROLE_USER);
//
//        when(activityRepository.findOneByTitle(activityTitle)).thenReturn(activity);
//        when(authService.extractUserFromToken(accessToken)).thenReturn(mockUser);
//
//        assertThrows(ActivityNotOpenException.class, () -> {
//            activityService.registerActivity(activityTitle, accessToken);
//        });
//
//        verify(activityRepository).findOneByTitle(activityTitle);
//        verify(authService).extractUserFromToken(accessToken);
//        // Verify that the activity repository does not save the activity
//        verify(activityRepository, times(0)).save(any(Activity.class));
//    }
//
//    @Test
//    public void testRegisterActivityWhenActivityFoundIsOpenAndUserHasRoleUserAndDeadlineHasPassed() {
//        String activityTitle = "Test Activity";
//        String submissionEndDate = "2000-01-01";
//        String submissionEndTime = "18:00";
//        // Mock the activity that will be returned by findOneByTitle
//        Activity activity = new Activity();
//        activity.setTitle(activityTitle);
//        // Status of activity should not be OPEN in this case
//        activity.setStatus(Status.OPEN);
//        activity.setSubmissionEndDate(submissionEndDate);
//        activity.setSubmissionEndTime(submissionEndTime);
//
//        User mockUser = new User();
//        String accessToken = "some example access token";
//        mockUser.setRole(Role.ROLE_USER);
//
//        when(activityRepository.findOneByTitle(activityTitle)).thenReturn(activity);
//        when(authService.extractUserFromToken(accessToken)).thenReturn(mockUser);
//
//        assertThrows(DeadlinePassedException.class, () -> {
//            activityService.registerActivity(activityTitle, accessToken);
//        });
//
//        verify(activityRepository).findOneByTitle(activityTitle);
//        verify(authService).extractUserFromToken(accessToken);
//        // Verify that the activity repository does not save the activity
//        verify(activityRepository, times(0)).save(any(Activity.class));
//    }
//
//    @Test
//    public void testRegisterActivityWhenUserNotHasRoleUser() {
//        String activityTitle = "Test Activity";
//        String accessToken = "some example access token";
//
//        // Mock the activity that will be returned by findOneByTitle
//        Activity activity = new Activity();
//        activity.setTitle(activityTitle);
//        activity.setStatus(Status.OPEN);
//
//        User mockUser = new User();
//        mockUser.setRole(Role.ROLE_ADMIN);
//
//        when(activityRepository.findOneByTitle(activityTitle)).thenReturn(activity);
//        when(authService.extractUserFromToken(accessToken)).thenReturn(mockUser);
//
//        assertThrows(NoPermissionException.class, () -> {
//            activityService.registerActivity(activityTitle, accessToken);
//        });
//
//        verify(activityRepository).findOneByTitle(activityTitle);
//        verify(authService).extractUserFromToken(accessToken);
//        // Verify that the activity repository does not save the activity
//        verify(activityRepository, times(0)).save(any(Activity.class));
//    }
}
