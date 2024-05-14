package com.dalv.bksims.models.dtos.course_registration;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TemporaryCourseClassRequest(
        @NotNull(message = "Proposed course class ID cannot be NULL") UUID proposedCourseClassId,
        @NotNull(message = "User email cannot be NULL") String userEmail
) {
}
