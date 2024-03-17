package com.dalv.bksims.models.entities.social_points_management;

import com.dalv.bksims.models.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;

import java.util.UUID;

@Entity
@Table(name = "activity_participation", uniqueConstraints={
        @UniqueConstraint(columnNames = {"activity_id", "user_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ActivityParticipation {
    @EmbeddedId
    private ActivityParticipationId activityParticipationId;

    @ManyToOne
    @MapsId("activity_id")
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;
}
