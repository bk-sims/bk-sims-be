package com.dalv.bksims.models.entities.course_registration;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProposedCourseId implements Serializable {
    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "registration_period_id")
    private UUID registrationPeriodId;
}
