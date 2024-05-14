package com.dalv.bksims.services.course_registration;

import com.dalv.bksims.exceptions.CapacityLimitException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.TemporaryCourseClassRequest;
import com.dalv.bksims.models.entities.course_registration.AbstractCourseClass;
import com.dalv.bksims.models.entities.course_registration.Course;
import com.dalv.bksims.models.entities.course_registration.ProposedCourseClass;
import com.dalv.bksims.models.entities.course_registration.TemporaryCourseClass;
import com.dalv.bksims.models.entities.course_registration.TemporaryCourseClassId;
import com.dalv.bksims.models.entities.user.Student;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.repositories.course_registration.CourseRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedCourseClassRepository;
import com.dalv.bksims.models.repositories.course_registration.TemporaryCourseClassRepository;
import com.dalv.bksims.models.repositories.user.StudentRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepo;

    private final ProposedCourseClassRepository proposedCourseClassRepo;

    private final TemporaryCourseClassRepository temporaryCourseClassRepo;

    private final UserRepository userRepo;

    private final StudentRepository studentRepo;

    private static CourseClassGeneralResponse getCourseClassGeneralResponse(AbstractCourseClass courseClass) {
        String lecturerName = courseClass.getLecturer()
                .getUser()
                .getFirstName() + " " + courseClass.getLecturer().getUser().getLastName();

        return new CourseClassGeneralResponse(
                courseClass.getId(),
                courseClass.getName(),
                courseClass.getCampus(),
                courseClass.getRoom(),
                courseClass.getWeeks(),
                courseClass.getDays(),
                courseClass.getStartTime(),
                courseClass.getEndTime(),
                courseClass.getType(),
                courseClass.getCourse().getCredits(),
                courseClass.getCapacity(),
                courseClass.getCurrentEnrollment(),
                lecturerName
        );
    }

    public List<CourseGeneralResponse> findProposedCoursesWithClassesBySearchValue(String searchValue) {
        List<Course> courses = courseRepo.findByCourseCodeOrNameContaining(searchValue);
        List<CourseGeneralResponse> result = new ArrayList<>();
        List<CourseClassGeneralResponse> courseClassesResult = new ArrayList<>();

        for (Course course : courses) {
            List<ProposedCourseClass> courseClasses = course.getProposedCourseClasses();
            for (ProposedCourseClass courseClass : courseClasses) {
                CourseClassGeneralResponse courseClassResult = getCourseClassGeneralResponse(
                        courseClass);

                courseClassesResult.add(courseClassResult);
            }

            CourseGeneralResponse courseResult = new CourseGeneralResponse(
                    course.getId(),
                    course.getCourseCode(),
                    course.getName(),
                    course.getCredits(),
                    courseClassesResult
            );

            result.add(courseResult);
        }

        return result;
    }

    @Transactional
    public TemporaryCourseClass addToTemporaryCourseClasses(TemporaryCourseClassRequest temporaryCourseClassRequest) {
        ProposedCourseClass proposedCourseClass = proposedCourseClassRepo.findOneById(
                temporaryCourseClassRequest.proposedCourseClassId());
        User user = userRepo.findByEmail(temporaryCourseClassRequest.userEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with email " + temporaryCourseClassRequest.userEmail() + " not found"));

        if (proposedCourseClass == null) {
            throw new EntityNotFoundException(
                    "Proposed course class with ID " + temporaryCourseClassRequest.proposedCourseClassId() + " not found");
        }

        Student student = studentRepo.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Student with email " + temporaryCourseClassRequest.userEmail() + " not found"));

        // Check if currentEnrollment is greater than or equal to capacity
        if (proposedCourseClass.getCurrentEnrollment() >= proposedCourseClass.getCapacity()) {
            throw new CapacityLimitException(
                    "Proposed course class with name " + proposedCourseClass.getName() + " is full");
        }

        TemporaryCourseClassId temporaryCourseClassId = TemporaryCourseClassId.builder()
                .proposedCourseClassId(proposedCourseClass.getId())
                .studentId(student.getId())
                .build();

        TemporaryCourseClass temporaryCourseClass = TemporaryCourseClass.builder()
                .temporaryCourseClassId(temporaryCourseClassId)
                .proposedCourseClass(proposedCourseClass)
                .student(student)
                .build();

        proposedCourseClass.setCurrentEnrollment(proposedCourseClass.getCurrentEnrollment() + 1);

        proposedCourseClassRepo.save(proposedCourseClass);
        temporaryCourseClassRepo.save(temporaryCourseClass);

        return temporaryCourseClass;
    }

    @Transactional
    public List<CourseClassGeneralResponse> findTemporaryCoursesByStudentId(String studentId) {
        UUID studentUUID = UUID.fromString(studentId);
        studentRepo.findById(studentUUID)
                .orElseThrow(() -> new EntityNotFoundException("Student with id " + studentId + " not found"));

        return temporaryCourseClassRepo.findAllByStudentId(UUID.fromString(studentId));
//        return null;
    }

}
