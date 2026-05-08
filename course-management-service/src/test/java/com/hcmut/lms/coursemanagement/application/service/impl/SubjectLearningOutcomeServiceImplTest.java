package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.repository.SubjectLearningOutcomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubjectLearningOutcomeServiceImplTest {

    @Mock
    private SubjectLearningOutcomeRepository sloRepository;

    private final Random random = new Random();

    @InjectMocks
    private SubjectLearningOutcomeServiceImpl subjectLearningOutcomeService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(30 + random.nextInt(70));
    }

    @Test
    void getBySubjectId_shouldReturnList_whenOutcomesExist() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getBySubjectId_shouldReturnEmptyList_whenNoOutcomes() {
        // TODO: implement
        assertTrue(true);
    }
}
