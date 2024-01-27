package com.dalv.bksims.controllers.social_points_management;

import com.dalv.bksims.models.dtos.social_points_management.ActivityRequest;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.services.social_points_management.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Activity")
@RestController
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
            @PathVariable int pageSize
    ) {
        Page<Activity> activitiesWithPagination = activityService.findActivityWithPagination(offset, pageSize);
        return new ResponseEntity<>(activitiesWithPagination, HttpStatus.OK);
    }

    @PatchMapping("/{title}")
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Activity> updateActivityById(
            @PathVariable String title,
            @ModelAttribute @Valid ActivityRequest activityUpdateRequest
    ) {
        Activity activity = activityService.updateActivityInfo(title, activityUpdateRequest);
        return new ResponseEntity<>(activity, HttpStatus.OK);
    }
}
