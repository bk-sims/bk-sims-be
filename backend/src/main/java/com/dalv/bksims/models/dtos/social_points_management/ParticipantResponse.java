package com.dalv.bksims.models.dtos.social_points_management;

import com.dalv.bksims.models.entities.user.User;

public record ParticipantResponse(
        User user,
        Integer pointsApproved,

        String evidenceUrl
) {
}
