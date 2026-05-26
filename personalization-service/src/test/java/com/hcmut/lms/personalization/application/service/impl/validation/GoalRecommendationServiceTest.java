package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.hcmut.lms.personalization.application.dto.response.FeasibilityMissingRequirementResponse;
import com.hcmut.lms.personalization.application.dto.response.RecommendationResponse;
import com.hcmut.lms.personalization.application.dto.response.WarningResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.application.service.impl.validation.model.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import org.junit.jupiter.api.Test;

class GoalRecommendationServiceTest {

    private final GoalRecommendationService service = new GoalRecommendationService();

    private FeasibilityCheckResult passedResult() {
        return new FeasibilityCheckResult(
            new CreditTimeCheckResult(true, 30, 120, 4, 2, "ok"),
            new GpaCheckResult(true, BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.5), BigDecimal.valueOf(3.2), 30, "ok"),
            new PrerequisiteChainResult(true, 3, 4, "ok"),
            new GraduationRequirementCheckResult(true, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), "ok"),
            true);
    }

    private FeasibilityCheckResult failedResult() {
        return new FeasibilityCheckResult(
            new CreditTimeCheckResult(false, 80, 60, 4, 2, "too many credits"),
            new GpaCheckResult(false, BigDecimal.valueOf(4.5), BigDecimal.valueOf(3.5), BigDecimal.valueOf(2.0), 80, "gpa too high"),
            new PrerequisiteChainResult(false, 6, 4, "chain too long"),
            new GraduationRequirementCheckResult(false, List.of("REQ1"), List.of("REQ1"),
                List.of(FeasibilityMissingRequirementResponse.builder().requirementId("REQ1").name("Req1").build()),
                List.of(FeasibilityMissingRequirementResponse.builder().requirementId("REQ1").name("Req1").build()),
                "at risk"),
            false);
    }

    @Test void classifyFeasibility_shouldReturnWeak_whenNotOverallPassed() {
        FeasibilityLevel level = service.classifyFeasibility(failedResult(), 0.8);
        assertTrue(level == FeasibilityLevel.WEAK);
    }

    @Test void classifyFeasibility_shouldReturnGood_whenHighProbability() {
        FeasibilityLevel level = service.classifyFeasibility(passedResult(), 0.75);
        assertTrue(level == FeasibilityLevel.GOOD);
    }

    @Test void classifyFeasibility_shouldReturnMedium_whenLowProbability() {
        FeasibilityLevel level = service.classifyFeasibility(passedResult(), 0.3);
        assertTrue(level == FeasibilityLevel.MEDIUM);
    }

    @Test void buildRecommendations_shouldReturnGoodRecs_whenGoodLevel() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .studentId(java.util.UUID.randomUUID())
            .build();
        List<RecommendationResponse> recs = service.buildRecommendations(FeasibilityLevel.GOOD, goal, passedResult());
        assertNotNull(recs);
        assertTrue(!recs.isEmpty());
    }

    @Test void buildRecommendations_shouldReturnMediumRecs_whenMediumLevel() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .studentId(java.util.UUID.randomUUID())
            .build();
        List<RecommendationResponse> recs = service.buildRecommendations(FeasibilityLevel.MEDIUM, goal, passedResult());
        assertNotNull(recs);
        assertTrue(!recs.isEmpty());
    }

    @Test void buildRecommendations_shouldReturnWeakRecs_whenWeakLevel() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .studentId(java.util.UUID.randomUUID())
            .build();
        List<RecommendationResponse> recs = service.buildRecommendations(FeasibilityLevel.WEAK, goal, failedResult());
        assertNotNull(recs);
        assertTrue(!recs.isEmpty());
    }

    @Test void buildRecommendations_shouldUsePriorityOrder_whenSet() {
        LearningGoal goal = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .studentId(java.util.UUID.randomUUID())
            .attemptTargetGpaOrder(2)
            .completedOnTime(1)
            .focusOnTargetOccupation(3)
            .build();
        List<RecommendationResponse> recs = service.buildRecommendations(FeasibilityLevel.GOOD, goal, passedResult());
        assertNotNull(recs);
        assertTrue(!recs.isEmpty());
    }

    @Test void buildWarnings_shouldReturnWarnings_whenAtRiskRequirements() {
        List<WarningResponse> warnings = service.buildWarnings(failedResult());
        assertNotNull(warnings);
        assertTrue(!warnings.isEmpty());
    }

    @Test void buildWarnings_shouldReturnEmpty_whenAllPassed() {
        List<WarningResponse> warnings = service.buildWarnings(passedResult());
        assertNotNull(warnings);
        assertTrue(warnings.isEmpty());
    }

    @Test void buildWarnings_shouldReturnMediumWarning_whenMissingButNoAtRisk() {
        FeasibilityMissingRequirementResponse detail = FeasibilityMissingRequirementResponse.builder()
            .requirementId("REQ2").name("Req2").build();
        var gradResult = new GraduationRequirementCheckResult(true, List.of("REQ2"), Collections.emptyList(),
            List.of(detail), Collections.emptyList(), "ok");
        var result = new FeasibilityCheckResult(
            new CreditTimeCheckResult(true, 30, 120, 4, 2, "ok"),
            new GpaCheckResult(true, BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.5), BigDecimal.valueOf(3.2), 30, "ok"),
            new PrerequisiteChainResult(true, 3, 4, "ok"),
            gradResult, true);
        List<WarningResponse> warnings = service.buildWarnings(result);
        assertNotNull(warnings);
        assertTrue(!warnings.isEmpty());
    }

    @Test void buildRecommendations_good_shouldHandleKnowledgeAndTimePriorities() {
        LearningGoal goal1 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .focusOnTargetOccupation(1).build();
        List<RecommendationResponse> recs1 = service.buildRecommendations(FeasibilityLevel.GOOD, goal1, passedResult());
        assertTrue(!recs1.isEmpty());

        LearningGoal goal2 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .completedOnTime(1).build();
        List<RecommendationResponse> recs2 = service.buildRecommendations(FeasibilityLevel.GOOD, goal2, passedResult());
        assertTrue(!recs2.isEmpty());
    }

    @Test void buildRecommendations_medium_shouldHandleAllPriorities() {
        var result = passedResult();
        LearningGoal goal1 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .attemptTargetGpaOrder(1).build();
        var recs1 = service.buildRecommendations(FeasibilityLevel.MEDIUM, goal1, result);
        assertTrue(!recs1.isEmpty());

        LearningGoal goal2 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .focusOnTargetOccupation(1).build();
        var recs2 = service.buildRecommendations(FeasibilityLevel.MEDIUM, goal2, result);
        assertTrue(!recs2.isEmpty());

        LearningGoal goal3 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .completedOnTime(1).build();
        var recs3 = service.buildRecommendations(FeasibilityLevel.MEDIUM, goal3, result);
        assertTrue(!recs3.isEmpty());
    }

    @Test void buildRecommendations_weak_shouldHandleAllPriorities() {
        var result = failedResult();
        LearningGoal goal1 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .attemptTargetGpaOrder(1).build();
        var recs1 = service.buildRecommendations(FeasibilityLevel.WEAK, goal1, result);
        assertTrue(!recs1.isEmpty());

        LearningGoal goal2 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .focusOnTargetOccupation(1).build();
        var recs2 = service.buildRecommendations(FeasibilityLevel.WEAK, goal2, result);
        assertTrue(!recs2.isEmpty());

        LearningGoal goal3 = LearningGoal.builder()
            .learningGoalId(java.util.UUID.randomUUID())
            .completedOnTime(1).build();
        var recs3 = service.buildRecommendations(FeasibilityLevel.WEAK, goal3, result);
        assertTrue(!recs3.isEmpty());
    }
}
