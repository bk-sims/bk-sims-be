package com.dalv.bksims.models.dtos.social_points_management;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActivityRegistrationRequest(
        @NotNull(message = "Acitivity ID cannot be NULL") UUID activityId,
        @NotNull(message = "User email cannot be NULL") String userEmail
) {
}
