package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.entities.course_registration.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("SELECT c FROM Course c WHERE LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchValue, '%'))")
    List<Course> findByCourseCodeOrNameContaining(@Param("searchValue") String searchValue);
}
