package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executor;

import com.hcmut.lms.personalization.application.service.impl.validation.model.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalFeasibilityCheckServiceTest {

    @Mock private CreditTimeValidatorService creditTimeValidator;
    @Mock private GpaRequirementValidatorService gpaRequirementValidator;
    @Mock private PrerequisiteChainValidatorService prerequisiteChainValidator;
    @Mock private GraduationRequirementValidatorService graduationRequirementValidator;
    @Mock private SemesterCalculationService semesterCalculationService;
    @Mock private Executor taskExecutor;

    @InjectMocks
    private GoalFeasibilityCheckService feasibilityCheckService;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test void checkFeasibility_shouldReturnOverallPassed_whenAllChecksPass() {
        UUID studentId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .specializationId(UUID.randomUUID().toString())
            .targetGpa(BigDecimal.valueOf(3.5))
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any()))
            .thenReturn(availability);

        when(creditTimeValidator.validate(any(), any(Integer.class), any()))
            .thenReturn(new CreditTimeCheckResult(true, 30, 120, 4, 2, "ok"));
        when(gpaRequirementValidator.validate(any(), any(), any(Integer.class), any(Integer.class)))
            .thenReturn(new GpaCheckResult(true, BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.5), BigDecimal.valueOf(3.2), 30, "ok"));
        when(prerequisiteChainValidator.validate(any(), any(), any(), any(Integer.class)))
            .thenReturn(new PrerequisiteChainResult(true, 3, 4, "ok"));
        when(graduationRequirementValidator.validate(any(), any(), any()))
            .thenReturn(new GraduationRequirementCheckResult(true, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), "ok"));

        FeasibilityCheckResult result = feasibilityCheckService.checkFeasibility(
            goal, BigDecimal.valueOf(3.2), 90, 30, Collections.emptyList(), Collections.emptyList(), null);
        assertNotNull(result);
        assertTrue(result.overallPassed());
    }

    @Test void checkFeasibility_shouldReturnNotPassed_whenOneCheckFails() {
        UUID studentId = UUID.randomUUID();
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(studentId)
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(2, 1, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(any(), any()))
            .thenReturn(availability);

        when(creditTimeValidator.validate(any(), any(Integer.class), any()))
            .thenReturn(new CreditTimeCheckResult(true, 30, 120, 2, 1, "ok"));
        when(gpaRequirementValidator.validate(any(), any(), any(Integer.class), any(Integer.class)))
            .thenReturn(new GpaCheckResult(false, BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.5), BigDecimal.valueOf(2.0), 30, "fail"));
        when(prerequisiteChainValidator.validate(any(), any(), any(), any(Integer.class)))
            .thenReturn(new PrerequisiteChainResult(true, 3, 2, "ok"));
        when(graduationRequirementValidator.validate(any(), any(), any()))
            .thenReturn(new GraduationRequirementCheckResult(true, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), "ok"));

        FeasibilityCheckResult result = feasibilityCheckService.checkFeasibility(
            goal, BigDecimal.valueOf(2.0), 60, 60, Collections.emptyList(), Collections.emptyList(), null);
        assertNotNull(result);
        assertTrue(!result.overallPassed());
    }
}
