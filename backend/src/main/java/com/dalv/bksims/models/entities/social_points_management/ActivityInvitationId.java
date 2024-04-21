package com.dalv.bksims.models.entities.social_points_management;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ActivityInvitationId implements Serializable {
    @Column(name = "activity_id")
    private UUID activityId;

    @Column(name = "user_id")
    private UUID userId;
}
