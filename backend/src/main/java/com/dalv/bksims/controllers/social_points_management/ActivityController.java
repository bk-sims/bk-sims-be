package com.dalv.bksims.controllers.social_points_management;

import com.dalv.bksims.models.dtos.social_points_management.ActivityRequest;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.services.social_points_management.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThan;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    ,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") String order,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @Or({
                    @Spec(path = "location", params = "query", spec = Like.class),
                    @Spec(path = "title", params = "query", spec = Like.class)
            })
            Specification<Activity> activitySpec,
            @Spec(path = "endDate", constVal = "#{T(java.time.LocalDate).now()}", valueInSpEL = true, spec = LessThanOrEqual.class)
            Specification<Activity> AcitivityWithClosedStatus,
            @Spec(path = "endDate", constVal = "#{T(java.time.LocalDate).now()}", valueInSpEL = true, spec = GreaterThan.class)
            Specification<Activity> AcitivityWithOpenStatus

    ) {
        return switch (status) {
            case "OPEN" -> {
                Page<Activity> activitiesWithPaginationOpen = activityService.findActivityWithPagination(activitySpec.and(AcitivityWithOpenStatus), offset, pageSize, order);
                yield new ResponseEntity<>(activitiesWithPaginationOpen, HttpStatus.OK);
            }
            case "CLOSED" -> {
                Page<Activity> activitiesWithPaginationClosed = activityService.findActivityWithPagination(activitySpec.and(AcitivityWithClosedStatus), offset, pageSize, order);
                yield new ResponseEntity<>(activitiesWithPaginationClosed, HttpStatus.OK);
            }
            default -> {
                Page<Activity> activitiesWithPagination = activityService.findActivityWithPagination(activitySpec, offset, pageSize, order);
                yield new ResponseEntity<>(activitiesWithPagination, HttpStatus.OK);
            }
        };
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
