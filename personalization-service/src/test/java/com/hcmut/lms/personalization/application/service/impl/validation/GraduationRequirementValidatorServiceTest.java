package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.FeasibilityMissingRequirementResponse;
import com.hcmut.lms.personalization.application.service.impl.validation.model.GraduationRequirementCheckResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.GraduationRequirementResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GraduationRequirementStatus;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.repository.GraduationRequirementStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GraduationRequirementValidatorServiceTest {

    @Mock private GraduationRequirementStatusRepository graduationRequirementStatusRepository;
    @Mock private CourseManagementClient courseManagementClient;

    @InjectMocks
    private GraduationRequirementValidatorService graduationRequirementValidator;

    @Test void validate_shouldReturnPassed_whenAllCompleted() {
        UUID studentId = UUID.randomUUID();
        UUID statusId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(statusId)
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(true)
            .completionSemesterId(UUID.randomUUID())
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .build();

        GraduationRequirementResponse metadata = new GraduationRequirementResponse();
        metadata.setGraduationRequirementId(requirementId);
        metadata.setCode("REQ1");
        metadata.setName("Requirement 1");
        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(List.of(metadata));

        GraduationRequirementCheckResult result = graduationRequirementValidator.validate(studentId, goal);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldReturnNotPassed_whenUncompletedWithoutPlan() {
        UUID studentId = UUID.randomUUID();
        UUID statusId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(statusId)
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(false)
            .completionSemesterId(null)
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .build();

        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(Collections.emptyList());

        GraduationRequirementCheckResult result = graduationRequirementValidator.validate(studentId, goal);
        assertNotNull(result);
        assertTrue(!result.passed());
        assertTrue(!result.atRiskRequirements().isEmpty());
    }

    @Test void validate_shouldReturnPassed_whenUncompletedButHasPlan() {
        UUID studentId = UUID.randomUUID();
        UUID statusId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(statusId)
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(false)
            .completionSemesterId(UUID.randomUUID())
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .build();

        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(Collections.emptyList());

        GraduationRequirementCheckResult result = graduationRequirementValidator.validate(studentId, goal);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldApplyUpdates_whenProvided() {
        UUID studentId = UUID.randomUUID();
        UUID statusId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(statusId)
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(false)
            .completionSemesterId(null)
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(Collections.emptyList());

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .build();

        com.hcmut.lms.personalization.application.dto.request.WizardGradRequirementUpdate update =
            new com.hcmut.lms.personalization.application.dto.request.WizardGradRequirementUpdate();
        update.setGraduationRequirementStatusId(statusId);
        update.setIsCompleted(true);
        update.setCompletionSemesterId(UUID.randomUUID());

        GraduationRequirementCheckResult result = graduationRequirementValidator.validate(
            studentId, goal, List.of(update));
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldHandleFetchMetadataException() {
        UUID studentId = UUID.randomUUID();

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(UUID.randomUUID())
            .studentId(studentId)
            .graduationRequirementId(UUID.randomUUID())
            .isCompleted(false)
            .completionSemesterId(null)
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenThrow(new RuntimeException("Service unavailable"));

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .build();

        GraduationRequirementCheckResult result = graduationRequirementValidator.validate(studentId, goal);
        assertNotNull(result);
        assertTrue(!result.passed());
    }
}
