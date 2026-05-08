package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.DepartmentMapper;
import com.hcmut.lms.coursemanagement.repository.DepartmentRepository;
import com.hcmut.lms.coursemanagement.repository.FacultyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    private final Random random = new Random();

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    @Test
    void createDepartment_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createDepartment_shouldThrowException_whenFacultyNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateDepartment_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateDepartment_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getDepartmentById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void getDepartmentById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllDepartments_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    void getAllDepartmentsPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    void getDepartmentsByFacultyId_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getDepartmentsByFacultyIdPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteDepartment_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteDepartment_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getDepartmentBySpecialization_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getDepartmentBySpecialization_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
