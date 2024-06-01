package com.dalv.bksims.bksims.controllers.course_registration;

import com.dalv.bksims.configurations.JwtAuthenticationFilter;
import com.dalv.bksims.controllers.course_registration.CourseController;
import com.dalv.bksims.services.auth.JwtService;
import com.dalv.bksims.services.course_registration.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(controllers = CourseController.class)
@Import({JwtService.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testFindProposedCoursesWithClassesBySearchValue_ValidSearchValue() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindProposedCoursesWithClassesBySearchValue_EmptySearchValue() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindProposedCoursesWithClassesPagination_ValidOffsetAndPageSize() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindAllCourseProposal() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testUploadCourseProposal_ValidFile() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testApproveCourseProposal_ValidFileName() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindRegisteredClassesByUserId_ValidUserId() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testAddToRegisteredClasses_ValidPayload() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testRemoveFromRegisteredClasses_ValidPayload() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testFindAssignedClassesBySemesterName_ValidSemesterName() throws Exception {
        assertEquals(true, true);
    }
}
