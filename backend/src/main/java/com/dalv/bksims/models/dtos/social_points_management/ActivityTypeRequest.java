package com.dalv.bksims.models.dtos.social_points_management;

import jakarta.validation.constraints.NotBlank;

public record ActivityTypeRequest(@NotBlank(message = "Name must not be blank") String name) {
}

