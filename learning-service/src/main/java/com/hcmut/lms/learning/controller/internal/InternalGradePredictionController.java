package com.hcmut.lms.learning.controller.internal;

import com.hcmut.lms.learning.dto.request.BatchGradePredictionRequest;
import com.hcmut.lms.learning.dto.response.BatchGradePredictionResponse;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.service.GradePredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Internal controller for grade prediction operations.
 * Used by other microservices for service-to-service calls.
 */
@RestController
@RequestMapping("/api/learning/internal/prediction")
@RequiredArgsConstructor
@Slf4j
public class InternalGradePredictionController {

  private final GradePredictionService gradePredictionService;

  @GetMapping("/grade")
  public ResponseEntity<GradePredictionResponse> predictGradeInternal(
      @RequestParam UUID studentId, @RequestParam UUID subjectId,
      @RequestParam(required = false) Integer plannedSemesterCredits,
      @RequestParam(required = false) Double threshold) {
    log.info("Internal grade prediction request: studentId={}, subjectId={}", studentId, subjectId);
    return ResponseEntity.ok(
        gradePredictionService.predictGrade(studentId, subjectId, plannedSemesterCredits, threshold));
  }

  @PostMapping("/grade/batch")
  public ResponseEntity<BatchGradePredictionResponse> predictGradeBatch(
      @RequestBody BatchGradePredictionRequest request) {
    log.info("Internal batch grade prediction request with {} items",
        request != null && request.getPredictions() != null ? request.getPredictions().size() : 0);
    return ResponseEntity.ok(gradePredictionService.predictGradeBatch(request));
  }
}

