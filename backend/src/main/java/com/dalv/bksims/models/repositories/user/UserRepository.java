package com.dalv.bksims.models.repositories.user;

import com.dalv.bksims.models.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    Optional<User> findByCode(String code);
}
