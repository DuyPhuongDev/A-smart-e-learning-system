package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.dto.request.WizardGradRequirementUpdate;
import com.hcmut.lms.personalization.application.service.impl.validation.model.*;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalFeasibilityCheckService {

  private final CreditTimeValidatorService creditTimeValidator;
  private final GpaRequirementValidatorService gpaRequirementValidator;
  private final PrerequisiteChainValidatorService prerequisiteChainValidator;
  private final GraduationRequirementValidatorService graduationRequirementValidator;
  private final SemesterCalculationService semesterCalculationService;
  private final Executor taskExecutor;

  public FeasibilityCheckResult checkFeasibility(
      LearningGoal goal, BigDecimal currentGpa, int earnedCredits, int remainingCredits, List<UUID> completedSubjectIds,
      List<UUID> remainingSubjectIds, List<WizardGradRequirementUpdate> graduationRequirementUpdates) {
    log.info("Starting feasibility checks for goal {}", goal.getLearningGoalId());

    SemesterCalculationService.SemesterAvailability availability =
        semesterCalculationService.calculateAvailableSemesters(
        goal.getStudentId(), goal.getExpectedCompletedSemester());
    // Use main semesters only for prerequisite chain validation.
    // Summer semesters have much lower credit caps and may not offer the required subjects,
    // so counting them as equivalent to main semesters for chain depth would overestimate feasibility.
    int mainSemestersOnly = availability.availableMainSemestersOnly();

    CompletableFuture<CreditTimeCheckResult> creditTimeFuture = CompletableFuture.supplyAsync(
        () -> creditTimeValidator.validate(goal, remainingCredits, earnedCredits), taskExecutor);

    CompletableFuture<GpaCheckResult> gpaFuture = CompletableFuture.supplyAsync(
        () -> gpaRequirementValidator.validate(goal, currentGpa, earnedCredits, remainingCredits), taskExecutor);

    CompletableFuture<PrerequisiteChainResult> prerequisiteFuture = CompletableFuture.supplyAsync(
        () -> prerequisiteChainValidator.validate(goal, completedSubjectIds, remainingSubjectIds, mainSemestersOnly),
        taskExecutor);

    CompletableFuture<GraduationRequirementCheckResult> graduationReqFuture = CompletableFuture.supplyAsync(
        () -> graduationRequirementValidator.validate(goal.getStudentId(), goal, graduationRequirementUpdates),
        taskExecutor);

    CompletableFuture.allOf(creditTimeFuture, gpaFuture, prerequisiteFuture, graduationReqFuture).join();

    CreditTimeCheckResult creditTimeResult = creditTimeFuture.join();
    GpaCheckResult gpaResult = gpaFuture.join();
    PrerequisiteChainResult prerequisiteResult = prerequisiteFuture.join();
    GraduationRequirementCheckResult graduationReqResult = graduationReqFuture.join();

    boolean overallPassed =
        creditTimeResult.passed() && gpaResult.passed() && prerequisiteResult.passed() && graduationReqResult.passed();

    log.info(
        "Feasibility checks completed: overall={}, credit={}, gpa={}, prereq={}, gradReq={}", overallPassed,
        creditTimeResult.passed(), gpaResult.passed(), prerequisiteResult.passed(), graduationReqResult.passed());

    return new FeasibilityCheckResult(
        creditTimeResult, gpaResult, prerequisiteResult, graduationReqResult,
        overallPassed);
  }
}
