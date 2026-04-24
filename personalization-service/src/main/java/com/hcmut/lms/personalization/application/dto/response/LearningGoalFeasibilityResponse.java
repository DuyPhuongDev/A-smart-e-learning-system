package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningGoalFeasibilityResponse {

  private FeasibilityLevel feasibilityLevel;

  private BigDecimal probabilityScore;

  private FeasibilityMetricsResponse metrics;

  private List<ValidationCheckResponse> preliminaryChecks;

  private ProbabilityAnalysisResponse probabilityAnalysis;

  private List<RecommendationResponse> recommendations;

  private List<WarningResponse> warnings;
}
