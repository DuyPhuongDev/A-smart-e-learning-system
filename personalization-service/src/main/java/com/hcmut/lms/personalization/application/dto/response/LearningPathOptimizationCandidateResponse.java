package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathOptimizationCandidateResponse {

  private String strategy;
  private String title;
  private String description;
  private String projectedRiskLevel;
  private Double projectedPredictedGpa;
  private Integer projectedTotalSemesters;
  private List<LearningPathChangeRecordResponse> changes;
}

