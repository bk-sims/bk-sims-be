package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.ActivityParticipation;
import com.dalv.bksims.models.entities.social_points_management.ActivityParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityParticipationRepository extends JpaRepository<ActivityParticipation, ActivityParticipationId> {
}
