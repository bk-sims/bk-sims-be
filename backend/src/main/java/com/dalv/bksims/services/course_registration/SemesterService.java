package com.dalv.bksims.services.course_registration;

import com.dalv.bksims.models.entities.course_registration.Semester;
import com.dalv.bksims.models.repositories.social_points_management.SemesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemesterService {
    private final SemesterRepository semesterRepository;

    public List<Semester> findAllSemesters() {
        return semesterRepository.findAll();
    }
}
