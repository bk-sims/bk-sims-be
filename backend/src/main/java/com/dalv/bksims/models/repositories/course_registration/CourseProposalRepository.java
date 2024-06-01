package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.CourseProposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseProposalRepository extends JpaRepository<CourseProposal, UUID> {
    CourseProposal findByExcelFileName(String excelFileName);
}
