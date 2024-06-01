package com.dalv.bksims.models.repositories.social_points_management;

import com.dalv.bksims.models.entities.course_registration.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {
    List<Semester> findAll();
}
