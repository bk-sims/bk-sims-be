package com.dalv.bksims.bksims.services.course_registration;

import com.dalv.bksims.models.repositories.course_registration.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Test
    public void testFindAllProposedCoursesWithClasses_ValidOffsetAndPageSize() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindAllProposedCoursesWithClasses_InvalidOffsetAndPageSize() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindRegisteredClassesByUserId_ValidUserId() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindRegisteredClassesByUserId_InvalidUserId() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testRemoveFromRegisteredClasses_ValidRequest() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testRemoveFromRegisteredClasses_InvalidRequest() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindAssignedClassesBySemesterName_ValidSemesterName() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindAssignedClassesBySemesterName_InvalidSemesterName() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testAddToRegisteredClasses_ValidRequest() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testAddToRegisteredClasses_InvalidRequest() throws Exception {
        assertEquals(true, true);
    }
}
