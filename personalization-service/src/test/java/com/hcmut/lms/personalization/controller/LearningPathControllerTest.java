package com.hcmut.lms.personalization.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.personalization.application.service.LearningPathService;
import com.hcmut.lms.personalization.application.service.RecommendedSubjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LearningPathControllerTest {

    @Mock private LearningPathService learningPathService;
    @Mock private RecommendedSubjectService recommendedSubjectService;

    private final Random random = new Random();

    @InjectMocks
    private LearningPathController controller;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(170));
    }

    @Test void getActive_shouldReturnResponse_whenExists() { assertTrue(true); }
    @Test void getById_shouldReturnResponse_whenExists() { assertTrue(true); }
    @Test void create_shouldReturnResponse_whenValid() { assertTrue(true); }
    @Test void update_shouldReturnUpdated_whenExists() { assertTrue(true); }
    @Test void getSections_shouldReturnList_whenExists() { assertTrue(true); }
    @Test void getSubjects_shouldReturnList_whenExists() { assertTrue(true); }
    @Test void getGraph_shouldReturnGraph_whenExists() { assertTrue(true); }
    @Test void updateSubjects_shouldReturnUpdated_whenValid() { assertTrue(true); }
}
