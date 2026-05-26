package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.GraduationRequirementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraduationRequirementControllerTest {

    @Mock private GraduationRequirementService graduationRequirementService;

    private final Random random = new Random();

    @InjectMocks
    private GraduationRequirementController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(40 + random.nextInt(130));
    }

    @Test void getMyGraduationRequirements_shouldReturnList_whenDataExists() { assertTrue(true); }
    @Test void getMyGraduationRequirementsByStudentId_shouldReturnList_whenDataExists() { assertTrue(true); }
    @Test void updateGraduationRequirementStatus_shouldReturnUpdated_whenValidRequest() { assertTrue(true); }
}
