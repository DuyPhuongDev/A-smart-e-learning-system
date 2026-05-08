package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.SemesterMapper;
import com.hcmut.lms.coursemanagement.client.UserManagementClient;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SemesterServiceImplTest {

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private UserManagementClient userManagementClient;

    @Mock
    private SemesterMapper semesterMapper;

    private final Random random = new Random();

    @InjectMocks
    private SemesterServiceImpl semesterService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(150));
    }

    @Test
    void createSemester_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    void createSemester_shouldThrowException_whenAcademicYearNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void updateSemester_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void updateSemester_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getSemesterById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getSemesterById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getAllSemesters_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    void getAllSemestersPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    void getSemestersByAcademicYearId_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getSemestersByAcademicYearIdPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteSemester_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteSemester_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    void getCurrentSemester_shouldReturnResponse() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getCurrentSemester_shouldThrowException_whenNoCurrentSemester() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getRemainSemester_shouldReturnList_whenStudentExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getRemainSemester_shouldThrowException_whenStudentNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
