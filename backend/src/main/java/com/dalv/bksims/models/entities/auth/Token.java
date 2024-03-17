package com.dalv.bksims.models.entities.auth;

import com.dalv.bksims.models.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Token {
    @Column(unique = true)
    public String token;
    public boolean revoked;
    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
