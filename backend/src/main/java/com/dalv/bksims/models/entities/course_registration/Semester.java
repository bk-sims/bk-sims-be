package com.dalv.bksims.models.entities.course_registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "semester")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "semester")
    @JsonIgnore
    private List<CourseClass> courseClasses;

    @OneToOne(mappedBy = "semester")
    @JsonIgnore
    private RegistrationPeriod registrationPeriod;
}
