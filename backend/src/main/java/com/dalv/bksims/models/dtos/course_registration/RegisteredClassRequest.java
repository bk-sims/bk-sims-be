package com.dalv.bksims.models.dtos.course_registration;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RegisteredClassRequest(
        @NotNull(message = "Proposed course class ID cannot be NULL") List<UUID> proposedClassIds,
        @NotNull(message = "User email cannot be NULL") String userEmail
) {
}
