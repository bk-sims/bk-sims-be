package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.ActivityInvitation;
import com.dalv.bksims.models.entities.social_points_management.ActivityInvitationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityInvitationRepository extends JpaRepository<ActivityInvitation, ActivityInvitationId> {
    List<ActivityInvitation> findInvitationsByActivityTitle(@Param("activityTitle") String activityTitle);
}
