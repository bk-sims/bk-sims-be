package com.dalv.bksims.bksims.controllers.social_points_management;


import com.dalv.bksims.configurations.JwtAuthenticationFilter;
import com.dalv.bksims.controllers.social_points_management.ActivityController;
import com.dalv.bksims.models.entities.social_points_management.Activity;
import com.dalv.bksims.models.repositories.user.UserRepository;
import com.dalv.bksims.services.auth.JwtService;
import com.dalv.bksims.services.social_points_management.ActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @InjectMocks
    private ActivityController eventController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testFindOneActivityByTitleController() throws Exception {
        String activityTitle = "Activity 1";
        Activity expectedActivity = new Activity();
        expectedActivity.setTitle(activityTitle);

        when(activityService.findOneActivityByTitle(activityTitle))
                .thenReturn(expectedActivity);

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/activities/{title}", activityTitle)
                        .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(expectedActivity.getTitle()));
    }
}
