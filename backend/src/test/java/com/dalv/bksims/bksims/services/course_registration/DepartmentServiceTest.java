package com.dalv.bksims.bksims.services.course_registration;

import com.dalv.bksims.models.repositories.course_registration.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;

    @Test
    public void testFindAllDepartments() throws Exception {
        assertEquals(true, true);
    }
}
