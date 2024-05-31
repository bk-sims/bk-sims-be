package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.ProposedCourse;
import com.dalv.bksims.models.entities.course_registration.ProposedCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProposedCourseRepository extends JpaRepository<ProposedCourse, UUID> {
    @Query("SELECT pc FROM ProposedCourse pc JOIN pc.course c WHERE LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchValue, '%'))")
    List<ProposedCourse> findByCourseCodeOrNameContaining(@Param("searchValue") String searchValue);

    ProposedCourse findProposedCourseByProposedCourseId(ProposedCourseId proposedCourseId);
}
