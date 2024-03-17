package com.dalv.bksims.models.dtos.social_points_management;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParticipantRemovalRequest(
        @NotNull (message = "Activity ID cannnot be null") String activityId,
        @NotNull (message = "User ID list cannot be null" ) List<String> userIds
) {
}
