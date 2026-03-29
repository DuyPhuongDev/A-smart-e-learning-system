package com.hcmut.lms.learning.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.response.ClassProgressResponse;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.service.LearningProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for Learning Progress operations
 * Handles tracking and querying learning progress for students
 */
@RestController
@RequestMapping("${prefix-api:}/progress")
@RequiredArgsConstructor
public class LearningProgressController {
    private final LearningProgressService learningProgressService;

    /**
     * Update or create learning progress for current user
     * 
     * @param request Learning progress request with lectureId, currentPosition, progressPercentage, etc.
     */
    @PostMapping("/track")
    public ResponseEntity<LearningProgressResponse> trackingProgress(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestBody @Valid LearningProgressRequest request) {
        return ResponseEntity.ok(learningProgressService.trackingProgress(currentUser.getId(), request));
    }

    /**
     * Get learning progress for current user in a specific lecture
     */
    @GetMapping("/lectures/{lectureId}")
    public ResponseEntity<LearningProgressResponse> getProgress(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID lectureId) {
        return ResponseEntity.ok(learningProgressService.getProgressForStudent(currentUser.getId(), lectureId));
    }

    /**
     * Get all learning progress for current user in a class
     */
    @GetMapping("/classes/{classId}")
    public ResponseEntity<List<LearningProgressResponse>> getProgressesByClass(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID classId) {
        return ResponseEntity.ok(learningProgressService.getProgressesByClassForStudent(currentUser.getId(), classId));
    }

    /**
     * Get class-level progress summary for current user
     */
    @GetMapping("/classes/{classId}/summary")
    public ResponseEntity<ClassProgressResponse> getClassProgressSummary(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID classId) {
        return ResponseEntity.ok(learningProgressService.getClassProgressSummaryForStudent(currentUser.getId(), classId));
    }

}
