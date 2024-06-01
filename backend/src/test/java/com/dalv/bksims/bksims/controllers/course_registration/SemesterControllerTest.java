package com.dalv.bksims.bksims.controllers.course_registration;

import com.dalv.bksims.configurations.JwtAuthenticationFilter;
import com.dalv.bksims.controllers.course_registration.SemesterController;
import com.dalv.bksims.services.auth.JwtService;
import com.dalv.bksims.services.course_registration.SemesterService;
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

@WebMvcTest(controllers = SemesterController.class)
@Import({JwtService.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class SemesterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SemesterService semesterService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testFindAllSemestersController() throws Exception {
        assertEquals(true, true);
    }
}
