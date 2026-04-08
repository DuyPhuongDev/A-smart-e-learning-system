package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathComparisonResultResponse {

  private UUID baselinePathId;
  private String candidateStrategy;
  private LearningPathComparisonMetrics baseline;
  private LearningPathComparisonMetrics candidate;
  private LearningPathComparisonDeltas deltas;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LearningPathComparisonMetrics {
    private Integer totalSemesters;
    private Double avgCreditsPerSemester;
    private Double predictedGpa;
    private String riskLevel;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LearningPathComparisonDeltas {
    private Integer totalSemesters;
    private Double avgCreditsPerSemester;
    private Double predictedGpa;
  }
}

