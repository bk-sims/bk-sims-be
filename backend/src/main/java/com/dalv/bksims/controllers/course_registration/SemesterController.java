package com.dalv.bksims.controllers.course_registration;

import com.dalv.bksims.models.entities.course_registration.Semester;
import com.dalv.bksims.services.course_registration.SemesterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Semester", description = "Semester API")
@RestController
@RequestMapping("/api/v1/semesters")
@RequiredArgsConstructor
public class SemesterController {
    private final SemesterService semesterService;

    @GetMapping
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<Semester>> findAllSemesters() {
        List<Semester> result = semesterService.findAllSemesters();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
