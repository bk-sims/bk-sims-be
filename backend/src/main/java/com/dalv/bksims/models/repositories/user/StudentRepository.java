package com.dalv.bksims.models.repositories.user;

import com.dalv.bksims.models.entities.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByUserId(UUID id);
}
