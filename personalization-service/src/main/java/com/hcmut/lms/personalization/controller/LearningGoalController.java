package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.personalization.application.dto.request.CreatePreferredSummerSemesterRequest;
import com.hcmut.lms.personalization.application.dto.request.CreateValidatedLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningGoalRequest;
import com.hcmut.lms.personalization.application.dto.request.ValidateLearningGoalFeasibilityRequest;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningGoalResponse;
import com.hcmut.lms.personalization.application.dto.response.PreferredSummerSemesterResponse;
import com.hcmut.lms.personalization.application.service.LearningGoalService;
import com.hcmut.lms.personalization.application.service.LearningGoalValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/learning-goals")
@RequiredArgsConstructor
@Slf4j
public class LearningGoalController {

  private final LearningGoalService learningGoalService;
  private final LearningGoalValidationService validationService;

  @GetMapping("/me/current")
  public ResponseEntity<LearningGoalResponse> getCurrent(@CurrentUser CurrentUserInfo currentUser) {
    return ResponseEntity.ok(learningGoalService.getCurrentLearningGoal(currentUser.getId()));
  }

  @GetMapping("/{learningGoalId}")
  public ResponseEntity<LearningGoalResponse> getById(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningGoalId) {
    return ResponseEntity.ok(learningGoalService.getLearningGoalById(currentUser.getId(), learningGoalId));
  }

  @PostMapping
  public ResponseEntity<LearningGoalResponse> create(
      @CurrentUser CurrentUserInfo currentUser,
      @Valid @RequestBody CreateValidatedLearningGoalRequest request) {
    return ResponseEntity.ok(validationService.confirmValidatedLearningGoal(currentUser.getId(), request));
  }

  @PutMapping("/{learningGoalId}")
  public ResponseEntity<LearningGoalResponse> update(
      @CurrentUser CurrentUserInfo currentUser, @PathVariable UUID learningGoalId,
      @Valid @RequestBody UpdateLearningGoalRequest request) {
    return ResponseEntity.ok(learningGoalService.updateLearningGoal(currentUser.getId(), learningGoalId, request));
  }

  @DeleteMapping("/{learningGoalId}")
  public ResponseEntity<Void> delete(@CurrentUser CurrentUserInfo currentUser, @PathVariable UUID learningGoalId) {
    learningGoalService.deleteLearningGoal(currentUser.getId(), learningGoalId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{learningGoalId}/summer-semesters")
  public ResponseEntity<List<PreferredSummerSemesterResponse>> listPreferredSummerSemesters(
      @CurrentUser CurrentUserInfo currentUser, @PathVariable UUID learningGoalId) {
    return ResponseEntity.ok(learningGoalService.getPreferredSummerSemesters(currentUser.getId(), learningGoalId));
  }

  @PostMapping("/{learningGoalId}/summer-semesters")
  public ResponseEntity<PreferredSummerSemesterResponse> createPreferredSummerSemester(
      @CurrentUser CurrentUserInfo currentUser, @PathVariable UUID learningGoalId,
      @Valid @RequestBody CreatePreferredSummerSemesterRequest request) {
    return ResponseEntity.ok(
        learningGoalService.createPreferredSummerSemester(currentUser.getId(), learningGoalId, request));
  }

  @PostMapping("/validate")
  public ResponseEntity<LearningGoalFeasibilityResponse> validateLearningGoalFeasibility(
      @CurrentUser CurrentUserInfo currentUser, @Valid @RequestBody ValidateLearningGoalFeasibilityRequest request) {

    log.info("Validating learning goal feasibility for studentId={}", currentUser.getId());

    LearningGoalFeasibilityResponse response = validationService.validateLearningGoalFeasibility(
        currentUser.getId(), request);

    log.info(
        "Learning goal feasibility validation completed: level={}, probability={}", response.getFeasibilityLevel(),
        response.getProbabilityScore());

    return ResponseEntity.ok(response);
  }
}


