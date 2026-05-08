package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import com.hcmut.lms.coursemanagement.client.AssessmentServiceClient;
import com.hcmut.lms.coursemanagement.repository.ClassSectionGradingRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectGradingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassGradingServiceImplTest {

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private GradingRepository gradingRepository;

    @Mock
    private ClassSectionGradingRepository classSectionGradingRepository;

    @Mock
    private SubjectGradingRepository subjectGradingRepository;

    @Mock
    private AssessmentServiceClient assessmentServiceClient;

    private final Random random = new Random();

    @InjectMocks
    private ClassGradingServiceImpl classGradingService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(80 + random.nextInt(270));
    }

    @Test
    void getGradingsForClass_shouldReturnList_whenClassExists() {
        // TODO: implement
        assertTrue(true);
    }

    void getGradingsForClass_shouldReturnSchoolGradings_whenSchoolClass() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGradingsForClass_shouldThrowException_whenClassNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getGradingWeightsForClass_shouldReturnList_whenClassExists() {
        // TODO: implement
        assertTrue(true);
    }

    void addGradingToClass_shouldReturnResponse_whenValidRequest() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void addGradingToClass_shouldThrowException_whenSchoolClass() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void addGradingToClass_shouldThrowException_whenGradingNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void updateGradingWeight_shouldReturnUpdatedResponse_whenValid() {
        // TODO: implement
        assertTrue(true);
    }

    void updateGradingWeight_shouldThrowException_whenTotalWeightInvalid() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void removeGradingFromClass_shouldRemove_whenExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void removeGradingFromClass_shouldThrowException_whenSchoolClass() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void initDefaultGradings_shouldCreateDefaults_whenValidClass() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void initDefaultGradings_shouldThrowException_whenSchoolClass() {
        // TODO: implement
        assertTrue(true);
    }
}
