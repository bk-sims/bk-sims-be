package com.dalv.bksims.services.course_registration;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.dalv.bksims.exceptions.CapacityLimitException;
import com.dalv.bksims.exceptions.ClassOverlapException;
import com.dalv.bksims.exceptions.EntityAlreadyExistsException;
import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.dtos.course_registration.CourseClassGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseGeneralResponse;
import com.dalv.bksims.models.dtos.course_registration.CourseProposalDto;
import com.dalv.bksims.models.dtos.course_registration.RegisteredClassRequest;
import com.dalv.bksims.models.entities.course_registration.AbstractCourseClass;
import com.dalv.bksims.models.entities.course_registration.AssignedClass;
import com.dalv.bksims.models.entities.course_registration.Course;
import com.dalv.bksims.models.entities.course_registration.CourseProposal;
import com.dalv.bksims.models.entities.course_registration.ProposedClass;
import com.dalv.bksims.models.entities.course_registration.ProposedCourse;
import com.dalv.bksims.models.entities.course_registration.ProposedCourseId;
import com.dalv.bksims.models.entities.course_registration.RegisteredClass;
import com.dalv.bksims.models.entities.course_registration.RegisteredClassId;
import com.dalv.bksims.models.entities.course_registration.RegistrationPeriod;
import com.dalv.bksims.models.entities.user.Lecturer;
import com.dalv.bksims.models.entities.user.Student;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.enums.CourseProposalStatus;
import com.dalv.bksims.models.repositories.course_registration.AssignedClassRepository;
import com.dalv.bksims.models.repositories.course_registration.CourseProposalRepository;
import com.dalv.bksims.models.repositories.course_registration.CourseRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedClassRepository;
import com.dalv.bksims.models.repositories.course_registration.ProposedCourseRepository;
import com.dalv.bksims.models.repositories.course_registration.RegisteredClassRepository;
import com.dalv.bksims.models.repositories.course_registration.RegistrationPeriodRepository;
import com.dalv.bksims.models.repositories.user.LecturerRepository;
import com.dalv.bksims.models.repositories.user.StudentRepository;
import com.dalv.bksims.models.repositories.user.UserRepository;
import com.dalv.bksims.services.common.ExcelService;
import com.dalv.bksims.services.common.S3Service;
import com.dalv.bksims.validations.CourseProposalValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepo;

    private final ProposedCourseRepository proposedCourseRepo;

    private final ProposedClassRepository proposedClassRepo;

    private final RegisteredClassRepository registeredClassRepo;

    private final UserRepository userRepo;

    private final StudentRepository studentRepo;

    private final AssignedClassRepository assignedClassRepo;

    private final LecturerRepository lecturerRepo;

    private final RegistrationPeriodRepository registrationPeriodRepo;

    private final ExcelService excelService;

    private final S3Service s3Service;

    private final CourseProposalRepository courseProposalRepo;

    private static CourseClassGeneralResponse getCourseClassGeneralResponse(AbstractCourseClass courseClass) {
        String lecturerName = courseClass.getLecturer()
                .getUser()
                .getFirstName() + " " + courseClass.getLecturer().getUser().getLastName();

        int credits = 0;
        String courseCode = "";
        if (courseClass instanceof ProposedClass) {
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

    public Page<CourseGeneralResponse> findProposedCoursesWithClassesPagination(int offset, int pageSize) {
        Pageable pageRequest = PageRequest.of(offset - 1, pageSize);
        Page<ProposedCourse> proposedCourses = proposedCourseRepo.findAll(pageRequest);

        List<CourseGeneralResponse> result = new ArrayList<>();
        for (ProposedCourse proposedCourse : proposedCourses) {
            Map<String, List<CourseClassGeneralResponse>> courseClassesMap = new TreeMap<>();
            List<ProposedClass> proposedClasses = proposedCourse.getProposedClasses();

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

        return new PageImpl<>(result, pageRequest, proposedCourses.getTotalElements());
    }

    @Transactional
    public List<CourseProposal> findAllCourseProposal() {
        return courseProposalRepo.findAll();
    }


    @Transactional
    public CourseProposal uploadCourseProposal(MultipartFile excelFile) {
        // Validate excel file
        CourseProposalValidator.validateExcelFile(excelFile);

        // Upload the excel file to S3 and retrieve the url
        String organizationName = "Computer Science";
        String excelFileName = s3Service.uploadFileForActivity(excelFile, organizationName);
        String excelFileUrl = s3Service.getFileUrl(excelFileName, organizationName + "/");

        // Create new course proposal
        CourseProposal courseProposal = CourseProposal.builder()
                .excelFileName(excelFileName)
                .excelFileUrl(excelFileUrl)
                .status(CourseProposalStatus.PENDING.toString())
                .build();

        return courseProposalRepo.save(courseProposal);
    }

    @Transactional
    public Map<String, String> approveCourseProposal(String excelFileName) {
        // Get excel file by name from S3
        S3ObjectInputStream excelFile = s3Service.findFileByName("Computer Science", excelFileName);

        // Read the excel file
        List<CourseProposalDto> courseProposalList = excelService.readExcel(excelFile);

        Set<String> courseCodes = courseProposalList.stream().map(CourseProposalDto::courseCode).collect(Collectors.toSet());
        Set<String> lecturerCodes = courseProposalList.stream().map(CourseProposalDto::lecturerCode).collect(Collectors.toSet());

        List<Course> courses = courseRepo.findCoursesByCourseCodeIn(courseCodes);
        List<Lecturer> lecturers = lecturerRepo.findLecturersByLecturerCodeIn(lecturerCodes);

        Map<String, Course> courseMap = new HashMap<>();
        for (Course course : courses) {
            courseMap.put(course.getCourseCode(), course);
        }

        Map<String, Lecturer> lecturerMap = new HashMap<>();
        for (Lecturer lecturer : lecturers) {
            lecturerMap.put(lecturer.getUser().getCode(), lecturer);
        }

        // Get current registration period
        RegistrationPeriod registrationPeriod = registrationPeriodRepo.findRegistrationPeriodBySemesterName("HK241");

        List<ProposedClass> proposedClasses = new ArrayList<>();
        for (CourseProposalDto courseProposalDto : courseProposalList) {
            Course course = courseMap.get(courseProposalDto.courseCode());
            Lecturer lecturer = lecturerMap.get(courseProposalDto.lecturerCode());

            ProposedCourseId proposedCourseId = ProposedCourseId.builder()
                    .courseId(course.getId())
                    .registrationPeriodId(registrationPeriod.getId())
                    .build();
            ProposedCourse proposedCourse = proposedCourseRepo.findProposedCourseByProposedCourseId(proposedCourseId);
            if (proposedCourse == null) {
                proposedCourse = ProposedCourse.builder()
                        .proposedCourseId(proposedCourseId)
                        .build();
                proposedCourse = proposedCourseRepo.save(proposedCourse);
            }

            ProposedClass proposedClass = ProposedClass.builder()
                    .name(courseProposalDto.className())
                    .campus(courseProposalDto.campus())
                    .room(courseProposalDto.room())
                    .weeks(courseProposalDto.weeks())
                    .days(courseProposalDto.days())
                    .startTime(courseProposalDto.startTime())
                    .endTime(courseProposalDto.endTime())
                    .type(courseProposalDto.type())
                    .capacity(courseProposalDto.capacity())
                    .currentEnrollment(0)
                    .proposedCourse(proposedCourse)
                    .lecturer(lecturer)
                    .build();

            proposedClasses.add(proposedClass);
        }
        proposedClassRepo.saveAll(proposedClasses);

        // Update course proposal status
        CourseProposal courseProposal = courseProposalRepo.findByExcelFileName(excelFileName);
        courseProposal.setStatus(CourseProposalStatus.APPROVED.toString());

        Map<String, String> result = new HashMap<>();
        result.put("coursesNumber", String.valueOf(courseCodes.size()));
        result.put("classesNumber", String.valueOf(proposedClasses.size()));
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
