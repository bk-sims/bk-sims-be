package com.dalv.bksims.models.entities.course_registration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String courseCode;

    @Column(unique = true)
    private String name;

    private int credits;

    private String introduction;

    private String syllabusFileName;

    private int exercise;

    private int midtermExam;

    private int assignment;

    private int finalExam;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<ProposedCourse> proposedCourses;
}
