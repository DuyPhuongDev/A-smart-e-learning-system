package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;

import com.hcmut.lms.personalization.application.service.impl.validation.model.PrerequisiteChainResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.PrerequisiteChainResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrerequisiteChainValidatorServiceTest {

    @Mock private CourseManagementClient courseManagementClient;

    @InjectMocks
    private PrerequisiteChainValidatorService prerequisiteChainValidator;

    @Test void validate_shouldReturnPassed_whenChainWithinSemesters() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString())
            .build();

        PrerequisiteChainResponse response = new PrerequisiteChainResponse();
        response.setLongestChainLength(3);
        when(courseManagementClient.getPrerequisiteChain(any())).thenReturn(response);

        PrerequisiteChainResult result = prerequisiteChainValidator.validate(
            goal, Collections.emptyList(), Collections.emptyList(), 4);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldReturnFailed_whenChainExceedsSemesters() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString())
            .build();

        PrerequisiteChainResponse response = new PrerequisiteChainResponse();
        response.setLongestChainLength(6);
        when(courseManagementClient.getPrerequisiteChain(any())).thenReturn(response);

        PrerequisiteChainResult result = prerequisiteChainValidator.validate(
            goal, Collections.emptyList(), Collections.emptyList(), 4);
        assertNotNull(result);
        assertTrue(!result.passed());
    }

    @Test void validate_shouldReturnFailed_whenClientThrowsException() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .specializationId(UUID.randomUUID().toString())
            .build();

        when(courseManagementClient.getPrerequisiteChain(any()))
            .thenThrow(new RuntimeException("Service down"));

        PrerequisiteChainResult result = prerequisiteChainValidator.validate(
            goal, Collections.emptyList(), Collections.emptyList(), 4);
        assertNotNull(result);
        assertTrue(!result.passed());
    }
}
