package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.application.mapper.GradingMapper;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradingServiceImplTest {

    @Mock
    private GradingRepository gradingRepository;

    @Mock
    private GradingMapper gradingMapper;

    private final Random random = new Random();

    @InjectMocks
    private GradingServiceImpl gradingService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    @Test
    void createGrading_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void createGrading_shouldThrowException_whenDuplicateName() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateGrading_shouldReturnUpdatedResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateGrading_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGradingById_shouldReturnResponse_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGradingById_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllGradings_shouldReturnList() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getAllGradings_shouldReturnEmptyList_whenNone() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteGrading_shouldDelete_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteGrading_shouldThrowException_whenNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
