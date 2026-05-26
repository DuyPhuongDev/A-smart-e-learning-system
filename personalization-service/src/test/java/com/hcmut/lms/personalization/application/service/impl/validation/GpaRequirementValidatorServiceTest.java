package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import com.hcmut.lms.personalization.application.service.impl.validation.model.GpaCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import org.junit.jupiter.api.Test;

class GpaRequirementValidatorServiceTest {

    private final GpaRequirementValidatorService gpaValidator = new GpaRequirementValidatorService();

    @Test void validate_shouldReturnPassed_whenCurrentGpaMeetsTarget() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(3.5))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, BigDecimal.valueOf(3.6), 90, 30);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldReturnFailed_whenRequiredGpaTooHigh() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(3.9))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, BigDecimal.valueOf(2.0), 60, 30);
        assertNotNull(result);
        assertTrue(!result.passed());
    }

    @Test void validate_shouldHandleNullCurrentGpa() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(3.0))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, null, 0, 30);
        assertNotNull(result);
    }

    @Test void validate_shouldHandleZeroRemainingCredits() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(3.0))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, BigDecimal.valueOf(3.2), 120, 0);
        assertNotNull(result);
    }

    @Test void validate_shouldThrow_whenTargetGpaIsNull() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(null)
            .build();
        try {
            gpaValidator.validate(goal, BigDecimal.valueOf(3.0), 90, 30);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Target GPA"));
        }
        assertTrue(true);
    }

    @Test void validate_shouldHandleNullCurrentGpaAndZeroRemainingCredits() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(3.0))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, null, 120, 0);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldFail_whenNullCurrentGpaAndHighTarget() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(5.0))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, null, 0, 30);
        assertNotNull(result);
        assertTrue(!result.passed());
    }

    @Test void validate_shouldFail_whenNullCurrentGpaAndZeroRemainingAndHighTarget() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(5.0))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, null, 0, 0);
        assertNotNull(result);
        assertTrue(!result.passed());
    }

    @Test void validate_shouldFail_whenCurrentGpaBelowTargetAndNoRemainingCredits() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .targetGpa(BigDecimal.valueOf(3.5))
            .build();
        GpaCheckResult result = gpaValidator.validate(goal, BigDecimal.valueOf(2.0), 120, 0);
        assertNotNull(result);
        assertTrue(!result.passed());
    }
}
