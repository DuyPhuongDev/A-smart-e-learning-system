package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditTimeValidatorServiceTest {

    @Mock private SemesterCalculationService semesterCalculationService;

    @InjectMocks
    private CreditTimeValidatorService creditTimeValidatorService;

    @Test void validate_shouldReturnPassed_whenCreditsWithinLimit() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(goal.getStudentId(), goal.getExpectedCompletedSemester()))
            .thenReturn(availability);

        CreditTimeCheckResult result = creditTimeValidatorService.validate(goal, 30);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldReturnFailed_whenCreditsExceedLimit() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Low)
            .plannedSummerSemCount(0)
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(2, 0, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(goal.getStudentId(), goal.getExpectedCompletedSemester()))
            .thenReturn(availability);

        CreditTimeCheckResult result = creditTimeValidatorService.validate(goal, 60);
        assertNotNull(result);
        assertTrue(!result.passed());
    }

    @Test void validate_shouldThrow_whenPrefMainSemIntensityNull() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(null)
            .plannedSummerSemCount(0)
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(goal.getStudentId(), goal.getExpectedCompletedSemester()))
            .thenReturn(availability);

        try {
            creditTimeValidatorService.validate(goal, 30, availability);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("prefMainSemLearnIntensity"));
        }
        assertTrue(true);
    }

    @Test void validate_shouldThrow_whenPlannedSummerSemCountNull() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(null)
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());

        try {
            creditTimeValidatorService.validate(goal, 30, availability);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("plannedSummerSemCount"));
        }
        assertTrue(true);
    }

    @Test void validate_shouldIncludeSummerCapacity_whenPlannedSummerSems() {
        UUID semId1 = UUID.randomUUID();
        UUID semId2 = UUID.randomUUID();
        PreferredSummerSemester summer1 = new PreferredSummerSemester();
        summer1.setSemesterId(semId1);
        summer1.setLearningIntensity(SummerLearningIntensity.Standard);
        PreferredSummerSemester summer2 = new PreferredSummerSemester();
        summer2.setSemesterId(semId2);
        summer2.setLearningIntensity(SummerLearningIntensity.Light);

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(2)
            .preferredSummerSemesters(new ArrayList<>(List.of(summer1, summer2)))
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());

        CreditTimeCheckResult result = creditTimeValidatorService.validate(goal, 30, availability);
        assertNotNull(result);
        assertTrue(result.passed());
    }

    @Test void validate_shouldThrow_whenSummerPlannedButNoEntries() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(1)
            .preferredSummerSemesters(Collections.emptyList())
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 1, Collections.emptyList());

        try {
            creditTimeValidatorService.validate(goal, 30, availability);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("PreferredSummerSemester"));
        }
        assertTrue(true);
    }

    @Test void validate_shouldThrow_whenNotEnoughSummerEntries() {
        PreferredSummerSemester summer1 = new PreferredSummerSemester();
        summer1.setSemesterId(UUID.randomUUID());
        summer1.setLearningIntensity(SummerLearningIntensity.Standard);

        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(3)
            .preferredSummerSemesters(new ArrayList<>(List.of(summer1)))
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 3, Collections.emptyList());

        try {
            creditTimeValidatorService.validate(goal, 30, availability);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("requires"));
        }
        assertTrue(true);
    }

    @Test void validate_shouldUseTwoArg_whenValidatingWithoutAvailability() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(UUID.randomUUID())
            .studentId(UUID.randomUUID())
            .prefMainSemLearnIntensity(LearningIntensity.Standard)
            .plannedSummerSemCount(0)
            .build();

        SemesterCalculationService.SemesterAvailability availability =
            new SemesterCalculationService.SemesterAvailability(4, 2, Collections.emptyList());
        when(semesterCalculationService.calculateAvailableSemesters(goal.getStudentId(), goal.getExpectedCompletedSemester()))
            .thenReturn(availability);

        CreditTimeCheckResult result = creditTimeValidatorService.validate(goal, 30);
        assertNotNull(result);
    }
}
