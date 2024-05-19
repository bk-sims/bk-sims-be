package com.dalv.bksims.bksims.services.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.ActivityType;
import com.dalv.bksims.models.repositories.social_points_management.ActivityTypeRepository;
import com.dalv.bksims.services.social_points_management.ActivityTypeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityTypeServiceTest {

    @Mock
    private ActivityTypeRepository activityTypeRepository;

    @InjectMocks
    private ActivityTypeService activityTypeService;

    @Test
    public void testFindAllActivityTypeService() {
        List<ActivityType> expectedActivityTypes = Arrays.asList(
                new ActivityType(UUID.fromString("8d2ff438-6172-4a6f-a851-61fd0ebda5c2"), "Student Awards and Recognition Titles"),
                new ActivityType(UUID.fromString("3b6bac76-61bd-4437-a0e8-4f46246d7397"), "Robocon Contest"),
                new ActivityType(UUID.fromString("b318357d-7bb2-4a4a-9291-dbad6b11dbee"), "International Cooperation and Exchange Activities"),
                new ActivityType(UUID.fromString("d2555937-7736-42af-bb66-768eb6bae5c4"), "Visiting Factories, Enterprises, Projects")
        );

        when(activityTypeRepository.findAll()).thenReturn(expectedActivityTypes);

        List<ActivityType> actualActivityTypes = activityTypeService.findAll();

        Assertions.assertNotNull(actualActivityTypes);
        Assertions.assertIterableEquals(expectedActivityTypes, actualActivityTypes);
    }

    @Test
    public void testAddActivityTypeService() {
        ActivityType expectedActivityType = ActivityType
                .builder()
                .id(UUID.randomUUID())
                .name("Voluntary Blood Donation").build();

        when(activityTypeRepository.save(Mockito.any(ActivityType.class))).thenReturn(expectedActivityType);

        ActivityType actualActivityType = activityTypeService.save(expectedActivityType);

        Assertions.assertNotNull(actualActivityType);
        Assertions.assertEquals(expectedActivityType, actualActivityType);
    }
}
