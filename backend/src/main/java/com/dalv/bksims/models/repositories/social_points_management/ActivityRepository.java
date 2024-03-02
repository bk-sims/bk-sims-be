package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID>, JpaSpecificationExecutor<Activity> {
    Activity findOneByTitle(String title);
}
