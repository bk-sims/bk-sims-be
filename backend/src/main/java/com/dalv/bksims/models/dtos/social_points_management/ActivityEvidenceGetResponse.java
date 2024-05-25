package com.dalv.bksims.models.dtos.social_points_management;

import java.util.UUID;

public record ActivityEvidenceGetResponse(
        UUID userId,

        UUID activityId,

        String evidenceUrl
) {
}
