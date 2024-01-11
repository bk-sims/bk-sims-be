package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, UUID> {
}
