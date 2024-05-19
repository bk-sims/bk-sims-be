package com.dalv.bksims.controllers.course_registration;

import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.TemporaryClassRequest;
import com.dalv.bksims.models.entities.course_registration.TemporaryClass;
import com.dalv.bksims.models.entities.course_registration.TemporaryClassId;
import com.dalv.bksims.services.course_registration.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Course Registration", description = "Course Registration API")
@Controller
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

    // Get all temporary course classes
    @GetMapping("/temporary/{studentId}")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<CourseClassGeneralResponse>> findTemporaryClassesByStudentId(@PathVariable String studentId) {
        List<CourseClassGeneralResponse> result = courseService.findTemporaryClassesByStudentId(studentId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/temporary")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<TemporaryClassId> addToTemporaryClasses(
            @RequestBody
            Map<String, String> payload
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        TemporaryClassRequest request = new TemporaryClassRequest(
                UUID.fromString(payload.get("proposedClassId")),
                userEmail
        );

        TemporaryClass result = courseService.addToTemporaryClasses(request);
        return new ResponseEntity<>(result.getTemporaryClassId(), HttpStatus.OK);
    }


    // Remove from temporary course classes
}
