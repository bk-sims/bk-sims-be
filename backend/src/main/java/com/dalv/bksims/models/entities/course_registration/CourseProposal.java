package com.dalv.bksims.models.entities.course_registration;

import com.dalv.bksims.models.enums.CourseProposalStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "course_proposal")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CourseProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String excelFileName;

    private String excelFileUrl;

    private String status = CourseProposalStatus.PENDING.toString();
}
