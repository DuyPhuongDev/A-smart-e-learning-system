package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import com.hcmut.lms.personalization.application.dto.request.CreateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.CreateValidatedLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.ValidateLearningGoalFeasibilityRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.application.service.LearningGoalService;
import com.hcmut.lms.personalization.application.service.impl.validation.GoalFeasibilityCheckService;
import com.hcmut.lms.personalization.application.service.impl.validation.GoalProbabilityAnalysisService;
import com.hcmut.lms.personalization.application.service.impl.validation.GoalRecommendationService;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.FeasibilityCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.GpaCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.GraduationRequirementCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.PrerequisiteChainResult;
import com.hcmut.lms.personalization.application.mapper.LearningGoalMapper;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import com.hcmut.lms.personalization.repository.GoalValidationResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearningGoalValidationServiceImplTest {

    @Mock private GoalFeasibilityCheckService feasibilityCheckService;
    @Mock private GoalProbabilityAnalysisService probabilityAnalysisService;
    @Mock private GoalRecommendationService recommendationService;
    @Mock private StudentProgressDataService studentProgressDataService;
    @Mock private LearningGoalService learningGoalService;
    @Mock private GoalValidationResultRepository goalValidationResultRepository;
    @Mock private LearningGoalMapper learningGoalMapper;

    @InjectMocks
    private LearningGoalValidationServiceImpl service;

    private static FeasibilityCheckResult makePassedResult() {
        return new FeasibilityCheckResult(
            new CreditTimeCheckResult(true, 30, 120, 4, 2, "ok"),
            new GpaCheckResult(true, BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.5), BigDecimal.valueOf(3.2), 30, "ok"),
            new PrerequisiteChainResult(true, 3, 4, "ok"),
            new GraduationRequirementCheckResult(true, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), "ok"),
            true);
    }

    private CreateLearningGoalRequest makeGoalRequest() {
        CreateLearningGoalRequest req = new CreateLearningGoalRequest();
        req.setSpecializationId(UUID.randomUUID().toString());
        req.setTargetGpa(BigDecimal.valueOf(3.5));
        req.setPrefMainSemLearnIntensity("Standard");
        req.setPlannedSummerSemCount(0);
        return req;
    }

    @Test void validateLearningGoalFeasibility_shouldReturnFullResponse_whenAllPassed() {
        UUID studentId = UUID.randomUUID();
        CreateLearningGoalRequest goalReq = makeGoalRequest();
        ValidateLearningGoalFeasibilityRequest request = new ValidateLearningGoalFeasibilityRequest();
        request.setGoal(goalReq);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.2), 90, 30,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any())).thenReturn(progressData);

        when(feasibilityCheckService.checkFeasibility(any(), any(), anyInt(), anyInt(), anyList(), anyList(), any()))
            .thenReturn(makePassedResult());

        Map<String, Object> probResult = new HashMap<>();
        probResult.put("method", "predictive");
        probResult.put("probabilityScore", 0.75);
        when(probabilityAnalysisService.analyzeProbability(any(), anyList(), any(), any(), anyInt(), anyInt(), any(), any()))
            .thenReturn(probResult);

        when(recommendationService.classifyFeasibility(any(), anyDouble()))
            .thenReturn(FeasibilityLevel.GOOD);
        when(recommendationService.buildRecommendations(any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(recommendationService.buildWarnings(any()))
            .thenReturn(Collections.emptyList());

        LearningGoalFeasibilityResponse result = service.validateLearningGoalFeasibility(studentId, request);
        assertNotNull(result);
        assertTrue(result.getFeasibilityLevel() == FeasibilityLevel.GOOD);
    }

    @Test void validateLearningGoalFeasibility_shouldReturnZeroProbability_whenNotOverallPassed() {
        UUID studentId = UUID.randomUUID();
        CreateLearningGoalRequest goalReq = makeGoalRequest();
        ValidateLearningGoalFeasibilityRequest request = new ValidateLearningGoalFeasibilityRequest();
        request.setGoal(goalReq);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(2.0), 60, 60,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any())).thenReturn(progressData);

        FeasibilityCheckResult failedResult = new FeasibilityCheckResult(
            new CreditTimeCheckResult(false, 60, 40, 2, 1, "not enough"),
            new GpaCheckResult(true, BigDecimal.valueOf(3.0), BigDecimal.valueOf(3.5), BigDecimal.valueOf(2.0), 60, "ok"),
            new PrerequisiteChainResult(true, 3, 2, "ok"),
            new GraduationRequirementCheckResult(true, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), "ok"),
            false);
        when(feasibilityCheckService.checkFeasibility(any(), any(), anyInt(), anyInt(), anyList(), anyList(), any()))
            .thenReturn(failedResult);

        when(recommendationService.classifyFeasibility(any(), anyDouble()))
            .thenReturn(FeasibilityLevel.WEAK);
        when(recommendationService.buildRecommendations(any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(recommendationService.buildWarnings(any()))
            .thenReturn(Collections.emptyList());

        LearningGoalFeasibilityResponse result = service.validateLearningGoalFeasibility(studentId, request);
        assertNotNull(result);
        assertTrue(result.getProbabilityScore().compareTo(BigDecimal.ZERO) == 0);
    }

    @Test void confirmValidatedLearningGoal_shouldReturnResponse_whenValid() {
        UUID studentId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();

        CreateLearningGoalRequest goalReq = makeGoalRequest();

        LearningGoalFeasibilityResponse validation = new LearningGoalFeasibilityResponse();
        validation.setFeasibilityLevel(FeasibilityLevel.MEDIUM);
        validation.setProbabilityScore(BigDecimal.valueOf(0.75));

        CreateValidatedLearningGoalRequest request = new CreateValidatedLearningGoalRequest();
        request.setGoal(goalReq);
        request.setSummerSemesters(Collections.emptyList());
        request.setValidationResult(validation);

        LearningGoalResponse createdGoal = new LearningGoalResponse();
        createdGoal.setLearningGoalId(goalId);
        when(learningGoalService.createLearningGoal(any(), any())).thenReturn(createdGoal);

        GoalValidationResult savedEntity = GoalValidationResult.builder()
            .learningGoalId(goalId)
            .studentId(studentId)
            .build();
        when(goalValidationResultRepository.save(any())).thenReturn(savedEntity);

        com.hcmut.lms.personalization.application.dto.response.GoalValidationResultResponse mockValidationResp =
            new com.hcmut.lms.personalization.application.dto.response.GoalValidationResultResponse();
        when(learningGoalMapper.toGoalValidationResultResponse(any())).thenReturn(mockValidationResp);

        LearningGoalResponse result = service.confirmValidatedLearningGoal(studentId, request);
        assertNotNull(result);
    }

    @Test void validateLearningGoalFeasibility_shouldHandleSummerSemesters_whenProvided() {
        UUID studentId = UUID.randomUUID();
        CreateLearningGoalRequest goalReq = makeGoalRequest();
        ValidateLearningGoalFeasibilityRequest request = new ValidateLearningGoalFeasibilityRequest();
        request.setGoal(goalReq);

        com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest summerReq =
            new com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest();
        summerReq.setSemesterId(UUID.randomUUID());
        summerReq.setLearnIntensity("Standard");
        request.setSummerSemesters(List.of(summerReq));

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.2), 90, 30,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any())).thenReturn(progressData);

        when(feasibilityCheckService.checkFeasibility(any(), any(), anyInt(), anyInt(), anyList(), anyList(), any()))
            .thenReturn(makePassedResult());

        Map<String, Object> probResult = new HashMap<>();
        probResult.put("method", "predictive");
        probResult.put("probabilityScore", 0.75);
        when(probabilityAnalysisService.analyzeProbability(any(), anyList(), any(), any(), anyInt(), anyInt(), any(), any()))
            .thenReturn(probResult);

        when(recommendationService.classifyFeasibility(any(), anyDouble()))
            .thenReturn(FeasibilityLevel.GOOD);
        when(recommendationService.buildRecommendations(any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(recommendationService.buildWarnings(any()))
            .thenReturn(Collections.emptyList());

        LearningGoalFeasibilityResponse result = service.validateLearningGoalFeasibility(studentId, request);
        assertNotNull(result);
        assertTrue(result.getFeasibilityLevel() == FeasibilityLevel.GOOD);
    }

    @Test void validateLearningGoalFeasibility_shouldResolveMethod_whenStringProvided() {
        UUID studentId = UUID.randomUUID();
        CreateLearningGoalRequest goalReq = makeGoalRequest();
        ValidateLearningGoalFeasibilityRequest request = new ValidateLearningGoalFeasibilityRequest();
        request.setGoal(goalReq);

        StudentProgressDataService.StudentProgressData progressData =
            new StudentProgressDataService.StudentProgressData(
                BigDecimal.valueOf(3.2), 90, 30,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(), null);
        when(studentProgressDataService.getStudentProgressData(any(), any())).thenReturn(progressData);

        when(feasibilityCheckService.checkFeasibility(any(), any(), anyInt(), anyInt(), anyList(), anyList(), any()))
            .thenReturn(makePassedResult());

        Map<String, Object> probResult = new HashMap<>();
        probResult.put("method", "historical");
        probResult.put("probabilityScore", BigDecimal.valueOf(0.65));
        probResult.put("sampleSize", 100);
        probResult.put("predictedFinalGpa", 3.2);
        probResult.put("standardDeviation", 0.5);
        probResult.put("predictionsCount", 5);
        when(probabilityAnalysisService.analyzeProbability(any(), anyList(), any(), any(), anyInt(), anyInt(), any(), any()))
            .thenReturn(probResult);

        when(recommendationService.classifyFeasibility(any(), anyDouble()))
            .thenReturn(FeasibilityLevel.GOOD);
        when(recommendationService.buildRecommendations(any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(recommendationService.buildWarnings(any()))
            .thenReturn(Collections.emptyList());

        LearningGoalFeasibilityResponse result = service.validateLearningGoalFeasibility(studentId, request);
        assertNotNull(result);
        assertTrue(result.getFeasibilityLevel() == FeasibilityLevel.GOOD);
    }
}
