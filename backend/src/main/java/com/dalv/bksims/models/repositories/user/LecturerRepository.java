package com.dalv.bksims.models.repositories.user;

import com.dalv.bksims.models.entities.user.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface LecturerRepository extends JpaRepository<Lecturer, UUID> {
    @Query("SELECT l FROM Lecturer l WHERE l.user.code IN :lecturerCodes")
    List<Lecturer> findLecturersByLecturerCodeIn(Set<String> lecturerCodes);
}
