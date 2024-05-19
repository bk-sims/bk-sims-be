package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.entities.course_registration.TemporaryClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TemporaryClassRepository extends JpaRepository<TemporaryClass, UUID> {
    @Query("SELECT new com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse(" +
            "p.id, p.name, p.campus, p.room, p.weeks, p.days, p.startTime, p.endTime, p.type, p.proposedCourse.course.credits , p.capacity, p.currentEnrollment, CONCAT(p.lecturer.user.firstName, ' ', p.lecturer.user.lastName)" +
            ") " +
            "FROM TemporaryClass t " +
            "JOIN t.proposedClass p " +
            "WHERE t.student.id = :studentId")
    List<CourseClassGeneralResponse> findAllByStudentId(UUID studentId);
}
