package com.hcmut.lms.learning.controller.internal;

import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Internal controller for subject metrics operations.
 * Used by other microservices for service-to-service calls.
 */
@RestController
@RequestMapping("/api/learning/internal/subject-metrics")
@RequiredArgsConstructor
@Slf4j
public class InternalSubjectMetricsController {

  private final SubjectSemesterMetricsService metricsService;

  @GetMapping("/batch/difficulty")
  public ResponseEntity<Map<UUID, String>> getBatchDifficulty(
      @RequestParam("subjectIds") List<UUID> subjectIds) {
    log.info("Internal batch difficulty request for {} subjects", subjectIds.size());
    return ResponseEntity.ok(metricsService.getBatchDifficulty(subjectIds));
  }
}