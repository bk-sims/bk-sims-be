package com.dalv.bksims.models.dtos.social_points_management;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ActivityEvidenceRequest(
        @NotNull(message = "Acitivity ID cannot be NULL") UUID activityId,
        @NotNull(message = "File cannot be empty") MultipartFile file
) {
}
