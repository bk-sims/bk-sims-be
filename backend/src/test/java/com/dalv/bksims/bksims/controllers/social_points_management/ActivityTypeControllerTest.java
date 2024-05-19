package com.dalv.bksims.bksims.controllers.social_points_management;

import com.dalv.bksims.configurations.JwtAuthenticationFilter;
import com.dalv.bksims.controllers.social_points_management.ActivityTypeController;
import com.dalv.bksims.models.dtos.social_points_management.ActivityTypeRequest;
import com.dalv.bksims.models.entities.social_points_management.ActivityType;
import com.dalv.bksims.models.repositories.user.UserRepository;
import com.dalv.bksims.services.auth.JwtService;
import com.dalv.bksims.services.social_points_management.ActivityTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ActivityTypeController.class)
@Import({JwtService.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ActivityTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityTypeService activityTypeService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFindAllActivityTypeController() throws Exception {
        List<ActivityType> expectedActivityTypes = Arrays.asList(
                new ActivityType(UUID.fromString("55bf88cf-f363-4768-802f-04bc62e26b63"), "Student Awards and Recognition Titles"),
                new ActivityType(UUID.fromString("f6ff017c-70c5-4dc7-adb3-676fcc21468e"), "International Cooperation and Exchange Activities"));

        when(activityTypeService.findAll()).thenReturn(expectedActivityTypes);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/activity-types")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()",
                        CoreMatchers.is(expectedActivityTypes.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(expectedActivityTypes.get(0).getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name")
                        .value(expectedActivityTypes.get(0).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(expectedActivityTypes.get(1).getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name")
                        .value(expectedActivityTypes.get(1).getName()));
    }

    @Test
    public void testAddActivityTypeController() throws Exception {
        ActivityTypeRequest activityTypeRequest = new ActivityTypeRequest("Challenge");

        ActivityType expectedActivityType = new ActivityType();
        expectedActivityType.setName(activityTypeRequest.name());

        when(activityTypeService.save(Mockito.any(ActivityType.class)))
                .thenReturn(expectedActivityType);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/activity-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityTypeRequest)));

        resultActions.andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id",
                        CoreMatchers.is(expectedActivityType.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",
                        CoreMatchers.is(expectedActivityType.getName())));
    }

    @Test
    public void testAddActivityTypeControllerWithEmptyName() throws Exception {
        ActivityTypeRequest expectedActivityTypeRequest = new ActivityTypeRequest("   ");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/activity-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedActivityTypeRequest)));

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.hasItem("Name must not be blank")));
    }

    @Test
    public void testAddActivityTypeControllerWithNullName() throws Exception {
        ActivityTypeRequest expectedActivityTypeRequest = new ActivityTypeRequest(null);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/activity-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedActivityTypeRequest)));

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.hasItem("Name must not be blank")));
    }

    @Test
    public void testAddActivityTypeControllerWithNullBody() throws Exception {
        ActivityTypeRequest expectedActivityTypeRequest = null;

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/activity-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedActivityTypeRequest)));

        resultActions.andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("The request body is missing or not in the expected format")));
    }
}