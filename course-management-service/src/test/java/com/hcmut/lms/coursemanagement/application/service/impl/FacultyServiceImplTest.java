package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.FacultyMapper;
import com.hcmut.lms.coursemanagement.repository.FacultyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyMapper facultyMapper;

    private final Random random = new Random();

    @InjectMocks
    private FacultyServiceImpl facultyService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    void createFaculty_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createFaculty_shouldThrowException_whenDuplicateCode() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateFaculty_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void updateFaculty_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getFacultyById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getFacultyById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getFacultyByCode_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getFacultyByCode_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllFaculties_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllFacultiesPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteFaculty_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteFaculty_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
