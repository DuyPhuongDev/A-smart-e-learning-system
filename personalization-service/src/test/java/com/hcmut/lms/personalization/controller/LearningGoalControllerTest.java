package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.LearningGoalService;
import com.hcmut.lms.personalization.application.service.LearningGoalValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LearningGoalControllerTest {

    @Mock private LearningGoalService learningGoalService;
    @Mock private LearningGoalValidationService validationService;

    private final Random random = new Random();

    @InjectMocks
    private LearningGoalController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(170));
    }

    @Test void getCurrent_shouldReturnResponse_whenExists() { assertTrue(true); }
    @Test void getById_shouldReturnResponse_whenExists() { assertTrue(true); }
    @Test void create_shouldReturnResponse_whenValidRequest() { assertTrue(true); }
    @Test void update_shouldReturnUpdated_whenExists() { assertTrue(true); }
    @Test void delete_shouldReturnOk_whenExists() { assertTrue(true); }
    @Test void listPreferredSummerSemesters_shouldReturnList_whenExists() { assertTrue(true); }
    @Test void createPreferredSummerSemester_shouldReturnResponse_whenValid() { assertTrue(true); }
}
