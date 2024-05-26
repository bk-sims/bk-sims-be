package com.dalv.bksims.models.entities.course_registration;

import jakarta.persistence.Column;
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
@Table(name = "assigned_class")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AssignedClass {
    @Column(unique = true)
    protected String courseCode;
    protected String courseName;
    protected String className;
    protected String campus;
    protected String room;
    protected String weeks;
    protected String days;
    protected String startTime;
    protected String endTime;
    protected String type;
    protected int credits;
    protected int capacity;
    protected String lecturerName;
    protected String semesterName;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
