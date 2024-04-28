package com.dalv.bksims.models.entities.social_points_management;

import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.enums.InvitationStatus;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activity_invitation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"activity_id", "user_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ActivityInvitation {
    @EmbeddedId
    private ActivityInvitationId activityInvitationId;

    @ManyToOne
    @MapsId("activity_id")
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    private String status = InvitationStatus.PENDING.toString();

    private String invitationLink;

    private boolean expired;
}
