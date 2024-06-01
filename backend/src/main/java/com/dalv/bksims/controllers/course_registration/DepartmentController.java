package com.dalv.bksims.controllers.course_registration;

import com.dalv.bksims.models.entities.course_registration.Department;
import com.dalv.bksims.services.course_registration.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Department", description = "Department API")
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public ResponseEntity<List<Department>> findAllDepartments() {
        List<Department> result = departmentService.findAllDepartments();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
