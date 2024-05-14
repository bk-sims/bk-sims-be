package com.dalv.bksims.models.dtos.course_registration;

import java.util.List;
import java.util.UUID;

public record CourseGeneralResponse(
        UUID id,
        String courseCode,
        String name,
        int credits,
        List<CourseClassGeneralResponse> courseClasses
) {
}
