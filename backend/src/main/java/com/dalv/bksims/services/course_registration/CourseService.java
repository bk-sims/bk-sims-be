package com.dalv.bksims.services.course_registration;

import com.dalv.bksims.exceptions.CapacityLimitException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.TemporaryClassRequest;
import com.dalv.bksims.models.entities.course_registration.AbstractCourseClass;
import com.dalv.bksims.models.entities.course_registration.Course;
import com.dalv.bksims.models.entities.course_registration.CourseClass;
import com.dalv.bksims.models.entities.course_registration.ProposedClass;
import com.dalv.bksims.models.entities.course_registration.ProposedCourse;
import com.dalv.bksims.models.entities.course_registration.TemporaryClass;
import com.dalv.bksims.models.entities.course_registration.TemporaryClassId;
import com.dalv.bksims.models.entities.user.Student;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.repositories.course_registration.CourseRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedClassRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedCourseRepository;
import com.dalv.bksims.models.repositories.course_registration.TemporaryClassRepository;
import com.dalv.bksims.models.repositories.user.StudentRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepo;

    private final ProposedCourseRepository proposedCourseRepo;

    private final ProposedClassRepository proposedClassRepo;

    private final TemporaryClassRepository temporaryClassRepo;

    private final UserRepository userRepo;

    private final StudentRepository studentRepo;

    private static CourseClassGeneralResponse getCourseClassGeneralResponse(AbstractCourseClass courseClass) {
        String lecturerName = courseClass.getLecturer()
                .getUser()
                .getFirstName() + " " + courseClass.getLecturer().getUser().getLastName();

        int credits = 0;
        if (courseClass instanceof CourseClass) {
            Course course = ((CourseClass) courseClass).getCourse();
            credits = course.getCredits();
        } else if (courseClass instanceof ProposedClass) {
            Course course = ((ProposedClass) courseClass).getProposedCourse().getCourse();
            credits = course.getCredits();
        }

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
                credits,
                courseClass.getCapacity(),
                courseClass.getCurrentEnrollment(),
                lecturerName
        );
    }

    public List<CourseGeneralResponse> findProposedCoursesWithClassesBySearchValue(String searchValue) {
        // Find proposed courses that contain search value in name
        List<ProposedCourse> proposedCourses = proposedCourseRepo.findByCourseCodeOrNameContaining(searchValue);
        List<CourseGeneralResponse> result = new ArrayList<>();

        // Find classes of each proposed course result
        for (ProposedCourse proposedCourse : proposedCourses) {
            Map<String, List<CourseClassGeneralResponse>> courseClassesMap = new TreeMap<>();
            List<ProposedClass> proposedClasses = proposedCourse.getProposedClasses();

            // The corresponding course of that proposed course
            Course course = proposedCourse.getCourse();

            for (ProposedClass proposedClass : proposedClasses) {
                CourseClassGeneralResponse courseClassResult = getCourseClassGeneralResponse(
                        proposedClass);

                if (!courseClassesMap.containsKey(proposedClass.getName())) {
                    List<CourseClassGeneralResponse> courseClasses = new ArrayList<>();
                    courseClasses.add(courseClassResult);
                    courseClassesMap.put(proposedClass.getName(), courseClasses);
                } else {
                    courseClassesMap.get(proposedClass.getName()).add(courseClassResult);
                }
            }

            CourseGeneralResponse courseResult = new CourseGeneralResponse(
                    course.getId(),
                    course.getCourseCode(),
                    course.getName(),
                    course.getCredits(),
                    courseClassesMap
            );

            result.add(courseResult);
        }

        return result;
    }

    @Transactional
    public TemporaryClass addToTemporaryClasses(TemporaryClassRequest temporaryClassRequest) {
        ProposedClass proposedClass = proposedClassRepo.findOneById(
                temporaryClassRequest.proposedClassId());
        User user = userRepo.findByEmail(temporaryClassRequest.userEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with email " + temporaryClassRequest.userEmail() + " not found"));

        if (proposedClass == null) {
            throw new EntityNotFoundException(
                    "Proposed course class with ID " + temporaryClassRequest.proposedClassId() + " not found");
        }

        Student student = studentRepo.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Student with email " + temporaryClassRequest.userEmail() + " not found"));

        // Check if currentEnrollment is greater than or equal to capacity
        if (proposedClass.getCurrentEnrollment() >= proposedClass.getCapacity()) {
            throw new CapacityLimitException(
                    "Proposed course class with name " + proposedClass.getName() + " is full");
        }

        // Check if class start time end time overlap with others

        TemporaryClassId temporaryClassId = TemporaryClassId.builder()
                .proposedClassId(proposedClass.getId())
                .studentId(student.getId())
                .build();

        TemporaryClass temporaryClass = TemporaryClass.builder()
                .temporaryClassId(temporaryClassId)
                .proposedClass(proposedClass)
                .student(student)
                .build();

        proposedClass.setCurrentEnrollment(proposedClass.getCurrentEnrollment() + 1);

        proposedClassRepo.save(proposedClass);
        temporaryClassRepo.save(temporaryClass);

        return temporaryClass;
    }

    @Transactional
    public List<CourseClassGeneralResponse> findTemporaryClassesByStudentId(String studentId) {
        UUID studentUUID = UUID.fromString(studentId);
        studentRepo.findById(studentUUID)
                .orElseThrow(() -> new EntityNotFoundException("Student with id " + studentId + " not found"));

        return temporaryClassRepo.findAllByStudentId(UUID.fromString(studentId));
    }

}
