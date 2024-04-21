package com.dalv.bksims.models.dtos.social_points_management;

import com.dalv.bksims.models.entities.user.User;

public record ParticipantsResponse(
        User user,
        Integer pointsApproved
) {
}
