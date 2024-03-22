package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.dtos.social_points_management.ParticipantsResponse;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ActivityParticipationRepository extends JpaRepository<ActivityParticipation, ActivityParticipationId> {
    @Query("SELECT ap.activity.id FROM ActivityParticipation ap WHERE ap.user.id = :userId")
    List<UUID> findActivityIdByUserId(@Param("userId") UUID userId);

    @Query("SELECT new com.dalv.bksims.models.dtos.social_points_management.ParticipantsResponse(ap.user, ap.pointsApproved) FROM ActivityParticipation ap WHERE ap.activity.title = :activityTitle")
    List<ParticipantsResponse> findParticipantsByActivityTitle(@Param("activityTitle") String activityTitle);
}
