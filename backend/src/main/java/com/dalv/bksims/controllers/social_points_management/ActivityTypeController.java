package com.dalv.bksims.controllers.social_points_management;

import com.dalv.bksims.models.dtos.social_points_management.ActivityTypeRequest;
import com.dalv.bksims.models.entities.social_points_management.ActivityType;
import com.dalv.bksims.services.social_points_management.ActivityTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Activity Type")
@RestController
@RequestMapping("/api/v1/activity-types")
@RequiredArgsConstructor
@Slf4j
public class ActivityTypeController {
    private final ActivityTypeService activityTypeService;

    @GetMapping
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public List<ActivityType> findAll() {
        return activityTypeService.findAll();
    }

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ActivityType addActivityType(@Valid @RequestBody ActivityTypeRequest activityTypeRequest) {
        ActivityType newActivityType = new ActivityType();
        newActivityType.setName(activityTypeRequest.name());

        ActivityType returnedActivityType = activityTypeService.save(newActivityType);
        log.info("Created activity type: {}", returnedActivityType.getId());
        return returnedActivityType;
    }

}
