package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.dtos.social_points_management.ActivityHistoryResponse;
import com.dalv.bksims.models.dtos.social_points_management.ParticipantResponse;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ActivityParticipationRepository extends JpaRepository<ActivityParticipation, ActivityParticipationId> {
    @Query("SELECT ap.activity.id FROM ActivityParticipation ap WHERE ap.user.id = :userId")
    List<UUID> findActivityIdByUserId(@Param("userId") UUID userId);

    @Query("Select new com.dalv.bksims.models.dtos.social_points_management.ActivityHistoryResponse(ap.user.firstName, ap.user.lastName, ap.activity.title, ap.pointsApproved) " + "FROM ActivityParticipation ap WHERE ap.user.id = :userId")
    List<ActivityHistoryResponse> findActivityHistoryByUserId(@Param("userId") UUID userId);

    @Query("SELECT new com.dalv.bksims.models.dtos.social_points_management.ParticipantResponse(ap.user, ap.pointsApproved, ap.evidenceUrl) " + "FROM ActivityParticipation ap WHERE ap.activity.title = :activityTitle")
    List<ParticipantResponse> findParticipantsByActivityTitle(@Param("activityTitle") String activityTitle);

    @Query("SELECT new com.dalv.bksims.models.dtos.social_points_management.ParticipantResponse(ap.user, ap.pointsApproved, ap.evidenceUrl) " + "FROM ActivityParticipation ap WHERE ap.activity.id = :activityId")
    List<ParticipantResponse> findParticipantsByActivityId(@Param("activityId") UUID activityId);

    @Query("SELECT new com.dalv.bksims.models.dtos.social_points_management.ParticipantResponse(ap.user, ap.pointsApproved, ap.evidenceUrl) " + "FROM ActivityParticipation ap WHERE ap.activity.id = :activityId AND ap.user.id IN :ids")
    List<ParticipantResponse> findParticipantsByActivityIdByIdIn(@Param("activityId") UUID activityId, @Param("ids") List<UUID> ids);

    @Query("SELECT COUNT(ap) > 0 FROM ActivityParticipation ap WHERE ap.user.id = :userId AND ap.activity.id = :activityId")
    boolean existsByUserIdAndActivityId(@Param("userId") UUID userId, @Param("activityId") UUID activityId);

    @Transactional
    void deleteByUserIdIn(List<UUID> userIds);
}
