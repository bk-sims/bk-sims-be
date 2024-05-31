package com.dalv.bksims.models.dtos.course_registration;

import lombok.Builder;

@Builder
public record CourseProposalDto(
        String courseCode,
        String className,
        String campus,
        String room,
        String weeks,
        String days,
        String startTime,
        String endTime,
        String type,
        int capacity,
        String lecturerCode
) {
}
