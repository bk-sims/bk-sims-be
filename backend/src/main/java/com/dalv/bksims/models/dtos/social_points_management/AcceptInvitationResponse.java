package com.dalv.bksims.models.dtos.social_points_management;

public record AcceptInvitationResponse(
        int status,
        String message,
        String redirectLink
) {
}
