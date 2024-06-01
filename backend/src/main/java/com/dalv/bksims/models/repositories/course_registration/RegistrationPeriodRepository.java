package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, UUID> {
    @Query("SELECT r FROM RegistrationPeriod r WHERE r.semester.name = :semesterName")
    public RegistrationPeriod findRegistrationPeriodBySemesterName(String semesterName);
}
