package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.personalization.application.dto.request.CompareLearningPathsRequest;
import com.hcmut.lms.personalization.application.dto.request.OptimizeLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.service.LearningPathService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

  private final LearningPathService learningPathService;

  @GetMapping("/me/active")
  public ResponseEntity<LearningPathResponse> getActive(@CurrentUser CurrentUserInfo currentUser) {
    return ResponseEntity.ok(learningPathService.getActiveLearningPath(currentUser.getId()));
  }

  @GetMapping("/{learningPathId}")
  public ResponseEntity<LearningPathResponse> getById(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.getLearningPathById(currentUser.getId(), learningPathId));
  }

  @PostMapping
  public ResponseEntity<LearningPathResponse> create(@CurrentUser CurrentUserInfo currentUser) {
    return ResponseEntity.ok(learningPathService.createLearningPath(currentUser.getId()));
  }

  @PutMapping("/{learningPathId}")
  public ResponseEntity<LearningPathResponse> update(
      @CurrentUser CurrentUserInfo currentUser, @PathVariable UUID learningPathId,
      @RequestBody UpdateLearningPathRequest request) {
    return ResponseEntity.ok(learningPathService.updateLearningPath(currentUser.getId(), learningPathId, request));
  }

  @GetMapping("/{learningPathId}/sections")
  public ResponseEntity<List<LearningPathSectionResponse>> getSections(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.getSections(currentUser.getId(), learningPathId));
  }

  @GetMapping("/{learningPathId}/subjects")
  public ResponseEntity<List<LearningPathSubjectResponse>> getSubjects(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.getSubjects(currentUser.getId(), learningPathId));
  }

  @PostMapping("/{learningPathId}/optimize")
  public ResponseEntity<List<LearningPathOptimizationCandidateResponse>> optimize(
      @CurrentUser CurrentUserInfo currentUser, @PathVariable UUID learningPathId,
      @Valid @RequestBody OptimizeLearningPathRequest request) {
    return ResponseEntity.ok(learningPathService.optimize(currentUser.getId(), learningPathId, request));
  }

  @GetMapping("/{learningPathId}/validate")
  public ResponseEntity<List<LearningPathValidationConflictResponse>> validate(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.validate(currentUser.getId(), learningPathId));
  }

  @GetMapping("/{learningPathId}/changes")
  public ResponseEntity<List<LearningPathChangeRecordResponse>> getChanges(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.getChanges(currentUser.getId(), learningPathId));
  }

  @GetMapping("/{learningPathId}/graph")
  public ResponseEntity<LearningPathGraphResponse> getGraph(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.getGraph(currentUser.getId(), learningPathId));
  }

  @PostMapping("/compare")
  public ResponseEntity<LearningPathComparisonResultResponse> compare(
      @CurrentUser CurrentUserInfo currentUser,
      @Valid @RequestBody CompareLearningPathsRequest request) {
    return ResponseEntity.ok(learningPathService.compare(currentUser.getId(), request));
  }
}

