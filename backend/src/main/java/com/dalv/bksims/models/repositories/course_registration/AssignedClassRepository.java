package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.AssignedClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignedClassRepository extends JpaRepository<AssignedClass, UUID> {
    List<AssignedClass> findBySemesterName(String semesterName);
}
