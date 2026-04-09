package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning/subject-metrics")
@RequiredArgsConstructor
@Slf4j
public class SubjectSemesterMetricsController {

  private final SubjectSemesterMetricsService metricsService;

  /**
   * Trigger computation of all subject semester metrics.
   * This should be called before computing the grade prediction dataset.
   */
  @PostMapping("/compute-all")
  public ResponseEntity<Map<String, Object>> computeAllMetrics() {
    log.info("Received request to compute all subject semester metrics");

    int count = metricsService.computeAndSaveAllMetrics();

    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("metricsComputed", count);
    response.put("message", "Subject semester metrics computed and saved successfully");

    return ResponseEntity.ok(response);
  }

  /**
   * Get metrics for a specific subject and semester.
   */
  @GetMapping("/subject/{subjectId}/semester/{semesterId}")
  public ResponseEntity<? extends Object> getMetrics(@PathVariable UUID subjectId, @PathVariable UUID semesterId) {

    return metricsService.findBySubjectIdAndSemesterId(subjectId, semesterId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Get metrics history for a subject.
   */
  @GetMapping("/subject/{subjectId}/history")
  public ResponseEntity<List<SubjectSemesterMetrics>> getSubjectHistory(
      @PathVariable UUID subjectId) {

    List<SubjectSemesterMetrics> history = metricsService.getMetricsHistoryForSubject(subjectId);
    return ResponseEntity.ok(history);
  }

  /**
   * Compute metrics for a specific subject and semester on-demand.
   */
  @PostMapping("/subject/{subjectId}/semester/{semesterId}/compute")
  public ResponseEntity<SubjectSemesterMetrics> computeMetrics(
      @PathVariable UUID subjectId,
      @PathVariable UUID semesterId) {

    SubjectSemesterMetrics metrics = metricsService.getOrComputeMetrics(subjectId, semesterId);
    return ResponseEntity.ok(metrics);
  }
}
