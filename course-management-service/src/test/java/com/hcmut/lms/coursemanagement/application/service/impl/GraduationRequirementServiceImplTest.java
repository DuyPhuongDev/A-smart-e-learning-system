package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.repository.GraduationRequirementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraduationRequirementServiceImplTest {

    @Mock
    private GraduationRequirementRepository graduationRequirementRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private final Random random = new Random();

    @InjectMocks
    private GraduationRequirementServiceImpl graduationRequirementService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    @Test
    void getActiveRequirementsByStudentId_shouldReturnList_whenRequirementsExist() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getActiveRequirementsByStudentId_shouldReturnEmptyList_whenNoRequirements() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getActiveRequirementsByStudentId_shouldThrowException_whenStudentNotFound() {
        // TODO: implement
        assertTrue(true);
    }
}
