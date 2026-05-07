package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathSubjectsRequest;
import com.hcmut.lms.personalization.application.dto.request.UpdateLearningPathRequest;
import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.service.LearningPathService;
import com.hcmut.lms.personalization.application.service.RecommendedSubjectService;
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
  private final RecommendedSubjectService recommendedSubjectService;

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
      @Valid @RequestBody UpdateLearningPathRequest request) {
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

  @GetMapping("/{learningPathId}/graph")
  public ResponseEntity<LearningPathGraphResponse> getGraph(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.getGraph(currentUser.getId(), learningPathId));
  }


  @PutMapping("/{learningPathId}/subjects")
  public ResponseEntity<LearningPathResponse> updateSubjects(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId,
      @Valid @RequestBody UpdateLearningPathSubjectsRequest request) {
    return ResponseEntity.ok(learningPathService.updateLearningPathSubjects(
        currentUser.getId(), learningPathId, request.getChanges()));
  }

  @GetMapping("/{learningPathId}/subjects/search")
  public ResponseEntity<List<LearningPathSubjectResponse>> searchSubjects(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId,
      @RequestParam("keyword") String keyword) {
    return ResponseEntity.ok(learningPathService.searchSubjects(
        currentUser.getId(), learningPathId, keyword));
  }

  @GetMapping("/{learningPathId}/subjects/recommended")
  public ResponseEntity<List<RecommendedSubjectResponse>> getRecommendedSubjects(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(recommendedSubjectService.getRecommendedSubjects(
        currentUser.getId(), learningPathId));
  }

  @PostMapping("/{learningPathId}/sync-progress")
  public ResponseEntity<LearningPathSyncProgressResponse> syncProgress(
      @CurrentUser CurrentUserInfo currentUser,
      @PathVariable UUID learningPathId) {
    return ResponseEntity.ok(learningPathService.syncLearningPathProgress(currentUser.getId(), learningPathId));
  }
}

