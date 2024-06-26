package com.dalv.bksims.models.entities.course_registration;

import com.dalv.bksims.models.entities.user.Lecturer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "proposed_class")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProposedClass extends AbstractCourseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @Column(unique = true)
    protected String name;

    protected String campus;

    protected String room;

    protected String weeks;

    protected String days;

    protected String startTime;

    protected String endTime;

    protected String type;

    protected int capacity;

    protected int currentEnrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    @JsonIgnore
    protected Lecturer lecturer;

    @OneToMany(mappedBy = "proposedClass", fetch = FetchType.LAZY)
    @JsonIgnore
    protected Set<RegisteredClass> registeredClasses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "course_id", referencedColumnName = "course_id"),
            @JoinColumn(name = "registration_period_id", referencedColumnName = "registration_period_id")
    })
    @JsonIgnore
    protected ProposedCourse proposedCourse;
}
