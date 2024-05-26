package com.dalv.bksims.models.repositories.course_registration;

import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.entities.course_registration.RegisteredClass;
import com.dalv.bksims.models.entities.course_registration.RegisteredClassId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RegisteredClassRepository extends JpaRepository<RegisteredClass, RegisteredClassId> {
    @Query("SELECT new com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse(" +
            "p.id, p.proposedCourse.course.courseCode,p.name, p.campus, p.room, p.weeks, p.days, p.startTime, p.endTime, p.type, p.proposedCourse.course.credits , p.capacity, p.currentEnrollment, CONCAT(p.lecturer.user.firstName, ' ', p.lecturer.user.lastName)" +
            ") " +
            "FROM RegisteredClass r " +
            "JOIN r.proposedClass p " +
            "WHERE r.student.id = :studentId")
    List<CourseClassGeneralResponse> findAllByStudentId(UUID studentId);

    RegisteredClass findOneByRegisteredClassId(RegisteredClassId id);

    @Query("SELECT r " +
            "FROM RegisteredClass r " +
            "JOIN r.proposedClass p " +
            "JOIN p.proposedCourse pc " +
            "JOIN pc.course c " +
            "WHERE c.courseCode = :courseCode")
    List<RegisteredClass> findOneByCourseCode(String courseCode);
}
