package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.CreateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest;
import com.hcmut.lms.personalization.application.dto.request.CreateValidatedLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.ValidateLearningGoalFeasibilityRequest;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.ProbabilityMethod;
import com.hcmut.lms.personalization.application.dto.response.enums.ValidationCheckType;
import com.hcmut.lms.personalization.application.service.LearningGoalService;
import com.hcmut.lms.personalization.application.service.LearningGoalValidationService;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityParsingSupport;
import com.hcmut.lms.personalization.application.service.impl.validation.GoalFeasibilityCheckService;
import com.hcmut.lms.personalization.application.service.impl.validation.GoalProbabilityAnalysisService;
import com.hcmut.lms.personalization.application.service.impl.validation.GoalRecommendationService;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.application.service.impl.validation.model.FeasibilityCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningGoalValidationServiceImpl implements LearningGoalValidationService {

  private final GoalFeasibilityCheckService feasibilityCheckService;
  private final GoalProbabilityAnalysisService probabilityAnalysisService;
  private final GoalRecommendationService recommendationService;
  private final StudentProgressDataService studentProgressDataService;
  private final LearningGoalService learningGoalService;
  private final Executor taskExecutor;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional
  public LearningGoalResponse confirmValidatedLearningGoal(UUID studentId, CreateValidatedLearningGoalRequest request) {
    LearningGoalResponse createdGoal = learningGoalService.createLearningGoal(studentId, request.getGoal());

    List<CreatePreferredSummerSemesterRequest> summerSemesters = request.getSummerSemesters() != null ?
        request.getSummerSemesters() : List.of();
    for (CreatePreferredSummerSemesterRequest summerSemester : summerSemesters) {
      learningGoalService.createPreferredSummerSemester(studentId, createdGoal.getLearningGoalId(), summerSemester);
    }

    LearningGoalFeasibilityResponse validationResult = request.getValidationResult();
    GoalValidationResult entity = GoalValidationResult.builder()
        .learningGoalId(createdGoal.getLearningGoalId())
        .studentId(studentId)
        .feasibilityLevel(validationResult.getFeasibilityLevel())
        .probabilityScore(validationResult.getProbabilityScore() != null ? BigDecimal.valueOf(
            validationResult.getProbabilityScore()) : null)
        .metrics(validationResult.getMetrics())
        .preliminaryChecks(validationResult.getPreliminaryChecks())
        .probabilityAnalysis(validationResult.getProbabilityAnalysis())
        .recommendations(validationResult.getRecommendations())
        .warnings(validationResult.getWarnings())
        .validationTimestamp(Instant.now())
        .build();

    entityManager.persist(entity);
    entityManager.flush();
    return learningGoalService.getLearningGoalById(studentId, createdGoal.getLearningGoalId());
  }


  @Override
  @Transactional(readOnly = true)
  public LearningGoalFeasibilityResponse validateLearningGoalFeasibility(
      UUID studentId,
      ValidateLearningGoalFeasibilityRequest request) {

    log.info("Starting learning goal feasibility validation for studentId={}", studentId);

    CreateLearningGoalRequest goalRequest = request.getGoal();

    LearningGoal tempGoal = LearningGoal.builder()
        .learningGoalId(UUID.randomUUID())
        .studentId(studentId)
        .specializationId(goalRequest.getSpecializationId())
        .targetGpa(goalRequest.getTargetGpa())
        .expectedCompletedSemester(goalRequest.getExpectedCompletedSemesterId())
        .prefMainSemLearnIntensity(parseIntensity(goalRequest.getPrefMainSemLearnIntensity()))
        .plannedSummerSemCount(goalRequest.getPlannedSummerSemCount())
        .targetOccupationCode(goalRequest.getTargetOccupationCode())
        .attemptTargetGpaOrder(goalRequest.getAttemptTargetGpaOrder())
        .focusOnTargetOccupation(goalRequest.getFocusOnTargetOccupation())
        .completedOnTime(goalRequest.getCompletedOnTime())
        .build();

    StudentProgressDataService.StudentProgressData progressData = studentProgressDataService.getStudentProgressData(
        studentId);

    BigDecimal currentGpa = progressData.currentGpa4();
    int earnedCredits = progressData.earnedCredits();
    int remainingCredits = progressData.remainingCredits();
    List<UUID> completedSubjectIds = progressData.completedSubjectIds();
    List<UUID> remainingSubjectIds = progressData.remainingSubjectIds();
    Map<UUID, Integer> remainingSubjectCredits = progressData.remainingSubjectCredits();

    CompletableFuture<FeasibilityCheckResult> feasibilityFuture = CompletableFuture.supplyAsync(
        () -> feasibilityCheckService.checkFeasibility(
            tempGoal, currentGpa, earnedCredits, remainingCredits,
            completedSubjectIds, remainingSubjectIds, request.getGraduationRequirementUpdates()), taskExecutor);

    FeasibilityCheckResult feasibility = feasibilityFuture.join();

    Map<String, Object> probabilityAnalysis;
    double probabilityScore;

    if (!feasibility.overallPassed()) {
      probabilityScore = 0.0;
      probabilityAnalysis = Map.of(
          "method", "skipped", "probabilityScore", probabilityScore, "note",
          "Skipped due to failed preliminary checks");
    } else {
      int totalSemesters = feasibility.creditTimeResult().mainSemesters() + feasibility.creditTimeResult()
          .summerSemesters();

      probabilityAnalysis = probabilityAnalysisService.analyzeProbability(
          studentId, tempGoal.getSpecializationId(),
          remainingSubjectIds, remainingSubjectCredits,
          currentGpa, earnedCredits, remainingCredits, tempGoal.getTargetGpa(),
          totalSemesters, IntensityCreditCapSupport.mainSemesterCap(tempGoal.getPrefMainSemLearnIntensity()));

      probabilityScore = ((Number) probabilityAnalysis.getOrDefault("probabilityScore", 0.5)).doubleValue();
    }

    log.info("Probability analysis result for studentId={}: {}", studentId, probabilityAnalysis);

    FeasibilityLevel feasibilityLevel = recommendationService.classifyFeasibility(feasibility, probabilityScore);
    List<RecommendationResponse> recommendations = recommendationService.buildRecommendations(
        feasibilityLevel, tempGoal, feasibility);
    List<WarningResponse> warnings = recommendationService.buildWarnings(feasibility);

    log.info(
        "Learning goal feasibility validation completed: level={}, probability={}", feasibilityLevel, probabilityScore);

    return buildFeasibilityResponse(
        feasibility, probabilityAnalysis, feasibilityLevel, probabilityScore, recommendations, warnings);
  }

  private LearningIntensity parseIntensity(String value) {
    return IntensityParsingSupport.parseNullableUntrimmedTitleCase(LearningIntensity.class, value);
  }

  private LearningGoalFeasibilityResponse buildFeasibilityResponse(
      FeasibilityCheckResult feasibility, Map<String, Object> probabilityAnalysis, FeasibilityLevel feasibilityLevel,
      double probabilityScore, List<RecommendationResponse> recommendations, List<WarningResponse> warnings) {

    List<ValidationCheckResponse> checks = List.of(
        ValidationCheckResponse.builder()
            .checkType(ValidationCheckType.CREDIT_TIME)
            .passed(feasibility.creditTimeResult().passed())
            .reason(feasibility.creditTimeResult().reason())
            .build(), ValidationCheckResponse.builder()
            .checkType(ValidationCheckType.GPA_REQUIREMENT)
            .passed(feasibility.gpaCheckResult().passed())
            .reason(feasibility.gpaCheckResult().reason())
            .build(), ValidationCheckResponse.builder()
            .checkType(ValidationCheckType.PREREQUISITE_CHAIN)
            .passed(feasibility.prerequisiteChainResult().passed())
            .reason(feasibility.prerequisiteChainResult().reason())
            .build(), ValidationCheckResponse.builder()
            .checkType(ValidationCheckType.GRADUATION_REQUIREMENT)
            .passed(feasibility.graduationReqResult().passed())
            .reason(feasibility.graduationReqResult().reason())
            .build());

    FeasibilityMetricsResponse metrics = FeasibilityMetricsResponse.builder()
        .remainingCredits(feasibility.creditTimeResult().remainingCredits())
        .availableMainSemesters(feasibility.creditTimeResult().mainSemesters())
        .availableSummerSemesters(feasibility.creditTimeResult().summerSemesters())
        .requiredAverageGpa(feasibility.gpaCheckResult().requiredGpa() != null ? feasibility.gpaCheckResult()
            .requiredGpa()
            .doubleValue() : null)
        .longestPrerequisiteChain(feasibility.prerequisiteChainResult().longestChain())
        .build();

    ProbabilityAnalysisDetailsResponse details = ProbabilityAnalysisDetailsResponse.builder()
        .probabilityScore(getDouble(probabilityAnalysis.get("probabilityScore")))
        .note(getString(probabilityAnalysis.get("note")))
        .sampleSize(getInteger(probabilityAnalysis.get("sampleSize")))
        .predictedFinalGpa(getDouble(probabilityAnalysis.get("predictedFinalGpa")))
        .standardDeviation(getDouble(probabilityAnalysis.get("standardDeviation")))
        .predictionsCount(getInteger(probabilityAnalysis.get("predictionsCount")))
        .build();

    ProbabilityAnalysisResponse probAnalysis = ProbabilityAnalysisResponse.builder()
        .method(resolveProbabilityMethod(probabilityAnalysis.get("method")))
        .details(details)
        .build();

    return LearningGoalFeasibilityResponse.builder()
        .feasibilityLevel(feasibilityLevel)
        .probabilityScore(probabilityScore)
        .metrics(metrics)
        .preliminaryChecks(checks)
        .probabilityAnalysis(probAnalysis)
        .recommendations(recommendations)
        .warnings(warnings)
        .build();
  }

  private Double getDouble(Object value) {
    return value instanceof Number number ? number.doubleValue() : null;
  }

  private Integer getInteger(Object value) {
    return value instanceof Number number ? number.intValue() : null;
  }

  private String getString(Object value) {
    return value != null ? value.toString() : null;
  }

  private ProbabilityMethod resolveProbabilityMethod(Object rawMethod) {
    if (rawMethod instanceof ProbabilityMethod method) {
      return method;
    }

    if (rawMethod == null) {
      return ProbabilityMethod.UNKNOWN;
    }

    String normalized = rawMethod.toString().trim();
    if (normalized.isEmpty()) {
      return ProbabilityMethod.UNKNOWN;
    }

    normalized = normalized.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    return switch (normalized) {
      case "SKIPPED" -> ProbabilityMethod.SKIPPED;
      case "HISTORICAL" -> ProbabilityMethod.HISTORICAL;
      case "PREDICTIVE" -> ProbabilityMethod.PREDICTIVE;
      case "PREDICTIVE_FALLBACK" -> ProbabilityMethod.PREDICTIVE_FALLBACK;
      default -> ProbabilityMethod.UNKNOWN;
    };
  }
}
