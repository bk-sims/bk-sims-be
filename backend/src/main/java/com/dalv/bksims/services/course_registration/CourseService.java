package com.dalv.bksims.services.course_registration;

import com.dalv.bksims.exceptions.CapacityLimitException;
import com.dalv.bksims.exceptions.ClassOverlapException;
import com.dalv.bksims.exceptions.EntityAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.RegisteredClassRequest;
import com.dalv.bksims.models.entities.course_registration.AbstractCourseClass;
import com.dalv.bksims.models.entities.course_registration.AssignedClass;
import com.dalv.bksims.models.entities.course_registration.Course;
import com.dalv.bksims.models.entities.course_registration.CourseClass;
import com.dalv.bksims.models.entities.course_registration.ProposedClass;
import com.dalv.bksims.models.entities.course_registration.ProposedCourse;
import com.dalv.bksims.models.entities.course_registration.RegisteredClass;
import com.dalv.bksims.models.entities.course_registration.RegisteredClassId;
import com.dalv.bksims.models.entities.user.Student;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.repositories.course_registration.AssignedClassRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedClassRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedCourseRepository;
import com.dalv.bksims.models.repositories.course_registration.RegisteredClassRepository;
import com.dalv.bksims.models.repositories.user.StudentRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final ProposedCourseRepository proposedCourseRepo;

    private final ProposedClassRepository proposedClassRepo;

    private final RegisteredClassRepository registeredClassRepo;

    private final UserRepository userRepo;

    private final StudentRepository studentRepo;

    private final AssignedClassRepository assignedClassRepo;

    private static CourseClassGeneralResponse getCourseClassGeneralResponse(AbstractCourseClass courseClass) {
        String lecturerName = courseClass.getLecturer()
                .getUser()
                .getFirstName() + " " + courseClass.getLecturer().getUser().getLastName();

        int credits = 0;
        String courseCode = "";
        if (courseClass instanceof CourseClass) {
            Course course = ((CourseClass) courseClass).getCourse();
            credits = course.getCredits();
            courseCode = course.getCourseCode();
        } else if (courseClass instanceof ProposedClass) {
            Course course = ((ProposedClass) courseClass).getProposedCourse().getCourse();
            credits = course.getCredits();
            courseCode = course.getCourseCode();
        }

        return new CourseClassGeneralResponse(
                courseClass.getId(),
                courseCode,
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

    private static boolean isOverlap(ProposedClass proposedClass, List<String> classTimeline, DateTimeFormatter formatter) {
        String startTime = classTimeline.get(0);
        String endTime = classTimeline.get(1);
        String proposedClassStartTime = proposedClass.getStartTime();
        String proposedClassEndTime = proposedClass.getEndTime();

        LocalTime startTimeLocal = LocalTime.parse(startTime, formatter);
        LocalTime endTimeLocal = LocalTime.parse(endTime, formatter);
        LocalTime proposedClassStartTimeLocal = LocalTime.parse(proposedClassStartTime, formatter);
        LocalTime proposedClassEndTimeLocal = LocalTime.parse(proposedClassEndTime, formatter);

        boolean overlap = proposedClassStartTimeLocal.isBefore(endTimeLocal) && proposedClassEndTimeLocal.isAfter(startTimeLocal);
        return overlap;
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
    public List<RegisteredClass> addToRegisteredClasses(RegisteredClassRequest registeredClassRequest) {
        List<UUID> proposedClassIds = registeredClassRequest.proposedClassIds();
        List<ProposedClass> proposedClasses = proposedClassRepo.findAllById(proposedClassIds);

        User user = userRepo.findByEmail(registeredClassRequest.userEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with email " + registeredClassRequest.userEmail() + " not found"));

        if (proposedClasses.isEmpty() || proposedClasses.size() != proposedClassIds.size()) {
            throw new EntityNotFoundException(
                    "Course classes not found");
        }

        Student student = studentRepo.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Student with email " + registeredClassRequest.userEmail() + " not found"));

        List<CourseClassGeneralResponse> existingRegisteredClasses = registeredClassRepo.findAllByStudentId(student.getId());

        // Check if the proposed classes are already registered
        for (ProposedClass proposedClass : proposedClasses) {
            String courseCode = proposedClass.getProposedCourse().getCourse().getCourseCode();
            List<RegisteredClass> existingRegisteredClassesByCourseCode = registeredClassRepo.findOneByCourseCode(courseCode);

            if (!existingRegisteredClassesByCourseCode.isEmpty()) {
                throw new EntityAlreadyExistsException(
                        "Course with code " + courseCode + " is already registered");
            }
        }

        // Check if currentEnrollment is greater than or equal to capacity
        for (ProposedClass proposedClass : proposedClasses) {
            if (proposedClass.getCurrentEnrollment() >= proposedClass.getCapacity()) {
                throw new CapacityLimitException(
                        "Course class with name " + proposedClass.getName() + " is full");
            }

            // Build a map of existing registered class days like
            /* {
                    "2": [
                            [
                                "7:00",
                                "9:00"
                            ],
                            [
                                "13:00",
                                "15:00"
                            ]
                    ],
                    "3": [
                            [
                                "7:00",
                                "9:00"
                            ],
                            [
                                "13:00",
                                "15:00"
                            ]
                    ],
                ...
                }
            */
            Map<String, List<List<String>>> schedule = new HashMap<>();
            for (CourseClassGeneralResponse erc : existingRegisteredClasses) {
                List<String> classTimeline = new ArrayList<>();
                classTimeline.add(erc.startTime());
                classTimeline.add(erc.endTime());

                String[] days = erc.days().split(",");
                for (String d : days) {
                    if (!schedule.containsKey(d)) {
                        List<List<String>> daySchedule = new ArrayList<>();
                        daySchedule.add(classTimeline);
                        schedule.put(d, daySchedule);
                    } else {
                        schedule.get(d).add(classTimeline);
                    }
                }
            }

            // Check if proposed classes start time end time overlap with existing
            String[] days = proposedClass.getDays().split(",");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            for (String d : days) {
                if (schedule.containsKey(d)) {
                    List<List<String>> daySchedule = schedule.get(d);
                    for (List<String> classTimeline : daySchedule) {
                        boolean overlap = isOverlap(proposedClass, classTimeline, formatter);
                        if (overlap) {
                            throw new ClassOverlapException(
                                    "Course class with name " + proposedClass.getName() + " overlaps with existing class");
                        }
                    }
                }
            }
        }

        // Add to registered classes
        List<RegisteredClass> registeredClasses = new ArrayList<>();
        for (ProposedClass proposedClass : proposedClasses) {
            RegisteredClassId registeredClassId = RegisteredClassId.builder()
                    .proposedClassId(proposedClass.getId())
                    .studentId(student.getId())
                    .build();

            RegisteredClass registeredClass = RegisteredClass.builder()
                    .registeredClassId(registeredClassId)
                    .proposedClass(proposedClass)
                    .student(student)
                    .build();

            proposedClass.setCurrentEnrollment(proposedClass.getCurrentEnrollment() + 1);

            proposedClassRepo.save(proposedClass);
            registeredClassRepo.save(registeredClass);
            registeredClasses.add(registeredClass);
        }

        return registeredClasses;
    }

    @Transactional
    public Map<String, List<CourseClassGeneralResponse>> findRegisteredClassesByUserId(String userId) {
        UUID userUUID = UUID.fromString(userId);
        Student student = studentRepo.findByUserId(userUUID)
                .orElseThrow(() -> new EntityNotFoundException("Student with id user_id " + userId + " not found"));

        List<CourseClassGeneralResponse> registeredClasses = registeredClassRepo.findAllByStudentId(student.getId());

        Map<String, List<CourseClassGeneralResponse>> result = new TreeMap<>();
        for (CourseClassGeneralResponse registeredClass : registeredClasses) {
            RegisteredClassId registeredClassId = RegisteredClassId.builder()
                    .proposedClassId(registeredClass.id())
                    .studentId(student.getId())
                    .build();

            RegisteredClass curr = registeredClassRepo.findOneByRegisteredClassId(registeredClassId);
            String courseCode = curr.getProposedClass().getProposedCourse().getCourse().getCourseCode();
            String courseName = curr.getProposedClass().getProposedCourse().getCourse().getName();
            String key = courseCode + " - " + courseName;

            if (!result.containsKey(key)) {
                List<CourseClassGeneralResponse> courseClasses = new ArrayList<>();
                courseClasses.add(registeredClass);
                result.put(key, courseClasses);
            } else {
                result.get(key).add(registeredClass);
            }
        }

        return result;
    }

    @Transactional
    public List<RegisteredClass> removeFromRegisteredClasses(RegisteredClassRequest registeredClassRequest) {
        List<UUID> proposedClassIds = registeredClassRequest.proposedClassIds();
        List<ProposedClass> proposedClasses = proposedClassRepo.findAllById(proposedClassIds);

        User user = userRepo.findByEmail(registeredClassRequest.userEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with email " + registeredClassRequest.userEmail() + " not found"));

        if (proposedClasses.isEmpty() || proposedClasses.size() != proposedClassIds.size()) {
            throw new EntityNotFoundException(
                    "Course classes not found");
        }

        Student student = studentRepo.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Student with email " + registeredClassRequest.userEmail() + " not found"));

        // Check if the proposed classes are already registered
        for (ProposedClass proposedClass : proposedClasses) {
            String courseCode = proposedClass.getProposedCourse().getCourse().getCourseCode();
            List<RegisteredClass> existingRegisteredClassesByCourseCode = registeredClassRepo.findOneByCourseCode(courseCode);

            if (existingRegisteredClassesByCourseCode.isEmpty()) {
                throw new EntityNotFoundException(
                        "Course with code " + courseCode + " is not registered");
            }
        }

        // Remove from registered classes
        List<RegisteredClass> registeredClasses = new ArrayList<>();
        for (ProposedClass proposedClass : proposedClasses) {
            RegisteredClassId registeredClassId = RegisteredClassId.builder()
                    .proposedClassId(proposedClass.getId())
                    .studentId(student.getId())
                    .build();

            RegisteredClass registeredClass = registeredClassRepo.findOneByRegisteredClassId(registeredClassId);
            registeredClassRepo.delete(registeredClass);

            proposedClass.setCurrentEnrollment(proposedClass.getCurrentEnrollment() - 1);
            proposedClassRepo.save(proposedClass);

            registeredClasses.add(registeredClass);
        }

        return registeredClasses;
    }

    public Map<String, List<CourseClassGeneralResponse>> findAssignedClassesBySemesterName(String semesterName) {
        List<AssignedClass> assignedClasses = assignedClassRepo.findBySemesterName(semesterName);
        Map<String, List<CourseClassGeneralResponse>> result = new TreeMap<>();

        for (AssignedClass assignedClass : assignedClasses) {
            CourseClassGeneralResponse courseClassResult = new CourseClassGeneralResponse(
                    assignedClass.getId(),
                    assignedClass.getCourseCode(),
                    assignedClass.getClassName(),
                    assignedClass.getCampus(),
                    assignedClass.getRoom(),
                    assignedClass.getWeeks(),
                    assignedClass.getDays(),
                    assignedClass.getStartTime(),
                    assignedClass.getEndTime(),
                    assignedClass.getType(),
                    assignedClass.getCredits(),
                    assignedClass.getCapacity(),
                    assignedClass.getCapacity(),
                    assignedClass.getLecturerName()
            );
            String key = assignedClass.getCourseCode() + " - " + assignedClass.getCourseName();
            if (!result.containsKey(key)) {
                List<CourseClassGeneralResponse> courseClasses = new ArrayList<>();
                courseClasses.add(courseClassResult);
                result.put(key, courseClasses);
            } else {
                result.get(key).add(courseClassResult);
            }
        }

        return result;
    }

}
