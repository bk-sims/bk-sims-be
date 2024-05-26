package com.dalv.bksims.models.entities.course_registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "proposed_course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProposedCourse {
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("course_id")
    @JoinColumn(name = "course_id")
    @JsonIgnore
    protected Course course;

    @EmbeddedId
    ProposedCourseId proposedCourseId;

    @OneToOne
    @MapsId("registration_period_id")
    @JoinColumn(name = "registration_period_id")
    @JsonIgnore
    private RegistrationPeriod registrationPeriod;
    
    @OneToMany(mappedBy = "proposedCourse", fetch = FetchType.LAZY)
    private List<ProposedClass> proposedClasses;
}
