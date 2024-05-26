package com.dalv.bksims.models.dtos.social_points_management;

public record ActivityHistoryResponse(
        String userFirstName,
        String userLastName,
        String activityTitle,
        Integer pointsApproved
) {
}
