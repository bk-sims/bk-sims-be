package com.dalv.bksims.controllers.course_registration;

import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.RegisteredClassRequest;
import com.dalv.bksims.models.entities.course_registration.CourseProposal;
import com.dalv.bksims.models.entities.course_registration.RegisteredClass;
import com.dalv.bksims.models.entities.course_registration.RegisteredClassId;
import com.dalv.bksims.services.course_registration.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/proposed/{offset}/{pageSize}")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Page<CourseGeneralResponse>> findProposedCoursesWithClassesPagination(
            @PathVariable int offset,
            @PathVariable int pageSize) {
        Page<CourseGeneralResponse> result = courseService.findProposedCoursesWithClassesPagination(offset, pageSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/course-proposal")
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<CourseProposal>> findAllCourseProposal() {
        List<CourseProposal> result = courseService.findAllCourseProposal();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/course-proposal/upload")
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<CourseProposal> uploadCourseProposal(@RequestParam("excelFile") MultipartFile excelFile) {
        CourseProposal result = courseService.uploadCourseProposal(excelFile);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/course-proposal/approve")
    @Secured({"ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, String>> approveCourseProposal(@RequestBody Map<String, String> payload) {
        String excelFileName = payload.get("excelFileName");
        Map<String, String> result = courseService.approveCourseProposal(excelFileName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

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

    @DeleteMapping("/registered")
    @Secured({"ROLE_STUDENT"})
    public ResponseEntity<List<RegisteredClassId>> removeFromRegisteredClasses(
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

        List<RegisteredClass> result = courseService.removeFromRegisteredClasses(request);
        return new ResponseEntity<>(result.stream().map(RegisteredClass::getRegisteredClassId).toList(), HttpStatus.OK);
    }

    @GetMapping("/assigned/{semesterName}")
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<Map<String, List<CourseClassGeneralResponse>>> findAssignedClassesBySemesterName(@PathVariable String semesterName) {
        Map<String, List<CourseClassGeneralResponse>> result = courseService.findAssignedClassesBySemesterName(semesterName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
