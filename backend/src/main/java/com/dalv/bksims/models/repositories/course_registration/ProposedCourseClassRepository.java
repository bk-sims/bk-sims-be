package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.ProposedCourseClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProposedCourseClassRepository extends JpaRepository<ProposedCourseClass, UUID> {
    ProposedCourseClass findOneById(UUID id);
}
