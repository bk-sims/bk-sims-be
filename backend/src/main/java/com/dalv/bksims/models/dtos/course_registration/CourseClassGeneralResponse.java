package com.dalv.bksims.models.dtos.course_registration;

import java.util.UUID;

public record CourseClassGeneralResponse(
        UUID id,
        String courseCode,
        String name,
        String campus,
        String room,
        String weeks,
        String days,
        String startTime,
        String endTime,
        String type,
        int credits,
        int capacity,
        int currentEnrollment,
        String lecturerName
) {

}
