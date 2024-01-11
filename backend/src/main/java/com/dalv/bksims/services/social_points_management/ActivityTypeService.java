package com.dalv.bksims.services.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.ActivityType;
import com.dalv.bksims.models.repositories.social_points_management.ActivityTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityTypeService {
    private final ActivityTypeRepository activityTypeRepository;

    public List<ActivityType> findAll() {
        return activityTypeRepository.findAll();
    }

    public ActivityType save(ActivityType activityType) {
        return activityTypeRepository.save(activityType);
    }
}
