package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.AcademicYearMapper;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcademicYearServiceImplTest {

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private AcademicYearMapper academicYearMapper;

    private final Random random = new Random();

    @InjectMocks
    private AcademicYearServiceImpl academicYearService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    void createAcademicYear_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createAcademicYear_shouldThrowException_whenDuplicateYear() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateAcademicYear_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    void updateAcademicYear_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAcademicYearById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAcademicYearById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllAcademicYears_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllAcademicYears_shouldReturnEmptyList_whenNone() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllAcademicYearsPaginated_shouldReturnPageResponse() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllAcademicYearsPaginated_shouldReturnEmptyPage_whenOutOfRange() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteAcademicYear_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteAcademicYear_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
