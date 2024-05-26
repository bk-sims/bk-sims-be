package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.ProposedClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProposedClassRepository extends JpaRepository<ProposedClass, UUID> {
    List<ProposedClass> findAllById(Iterable<UUID> ids);
}
