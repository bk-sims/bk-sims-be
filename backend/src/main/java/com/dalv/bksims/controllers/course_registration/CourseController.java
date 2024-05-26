package com.dalv.bksims.controllers.course_registration;

import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.RegisteredClassRequest;
import com.dalv.bksims.models.entities.course_registration.RegisteredClass;
import com.dalv.bksims.models.entities.course_registration.RegisteredClassId;
import com.dalv.bksims.services.course_registration.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Course Registration", description = "Course Registration API")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/proposed")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<CourseGeneralResponse>> findProposedCoursesWithClassesBySearchValue(@RequestParam(value = "value", required = false, defaultValue = "") String searchValue) {
        List<CourseGeneralResponse> result = courseService.findProposedCoursesWithClassesBySearchValue(searchValue);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Get all registered course classes
    @GetMapping("/registered/{userId}")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, List<CourseClassGeneralResponse>>> findRegisteredClassesByUserId(@PathVariable String userId) {
        Map<String, List<CourseClassGeneralResponse>> result = courseService.findRegisteredClassesByUserId(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/registered")
    @Secured({"ROLE_STUDENT"})
    public ResponseEntity<List<RegisteredClassId>> addToRegisteredClasses(
            @RequestBody
            Map<String, List<String>> payload
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<UUID> proposedClassIds = payload.get("proposedClassIds").stream()
                .map(UUID::fromString)
                .toList();

        RegisteredClassRequest request = new RegisteredClassRequest(
                proposedClassIds,
                userEmail
        );

        List<RegisteredClass> result = courseService.addToRegisteredClasses(request);
        return new ResponseEntity<>(result.stream().map(RegisteredClass::getRegisteredClassId).toList(), HttpStatus.OK);
    }


    // Remove from registered course classes
}
