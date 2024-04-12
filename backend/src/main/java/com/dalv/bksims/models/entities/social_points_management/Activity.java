package com.dalv.bksims.models.entities.social_points_management;

import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "activity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String title;

    @Column(unique = true)
    private String bannerFileName;

    @Column(unique = true)
    private String bannerFileUrl;

    private String description;

    private String location;

    private String startDate;

    private String endDate;

    private int numberOfParticipants;

    private boolean canParticipantsInvite;

    private int points;

    @Column(unique = true)
    private String regulationsFileName;

    @Column(unique = true)
    private String regulationsFileUrl;

    private String registrationStartDate;

    private String registrationEndDate;

    private String activityType;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String status = Status.PENDING.toString();

    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "activity")
    @JsonIgnore
    private Set<ActivityParticipation> participation;
}
