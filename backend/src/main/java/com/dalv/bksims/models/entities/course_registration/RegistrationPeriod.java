package com.dalv.bksims.models.entities.course_registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "registration_period")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegistrationPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String startDate;

    private String endDate;

    private String startTime;

    private String endTime;

    @OneToOne
    @JoinColumn(name = "semester_id")
    @JsonIgnore
    private Semester semester;

    @OneToOne(mappedBy = "registrationPeriod")
    @JsonIgnore
    private ProposedCourse proposedCourse;
}
