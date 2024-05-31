package com.dalv.bksims.services.course_registration;

import com.dalv.bksims.models.entities.course_registration.Department;
import com.dalv.bksims.models.repositories.course_registration.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }
}
