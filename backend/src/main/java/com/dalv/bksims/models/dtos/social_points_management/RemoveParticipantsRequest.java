package com.dalv.bksims.models.dtos.social_points_management;

import java.util.List;
import java.util.UUID;

public record RemoveParticipantsRequest(
        String activityId,
        List<UUID> participantsIds
) {
}
