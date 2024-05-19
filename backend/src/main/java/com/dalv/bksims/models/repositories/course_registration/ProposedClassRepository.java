package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.ProposedClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProposedClassRepository extends JpaRepository<ProposedClass, UUID> {
    ProposedClass findOneById(UUID id);
}
