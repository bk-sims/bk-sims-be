package com.dalv.bksims.models.entities.course_registration;

import com.dalv.bksims.models.entities.user.Lecturer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class AbstractCourseClass {
    protected UUID id;

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

    protected Course course;

    protected Lecturer lecturer;
}
