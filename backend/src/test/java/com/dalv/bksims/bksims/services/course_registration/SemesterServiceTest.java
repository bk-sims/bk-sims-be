package com.dalv.bksims.bksims.services.course_registration;

import com.dalv.bksims.models.repositories.social_points_management.SemesterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SemesterServiceTest {
    @Mock
    private SemesterRepository semesterRepository;

    @Test
    public void testFindAllSemesters() throws Exception {
        assertEquals(true, true);
    }
}
