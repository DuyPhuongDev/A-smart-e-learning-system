package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalValidationResultResponse {

  private UUID validationId;

  private UUID learningGoalId;

  private UUID studentId;

  private FeasibilityLevel feasibilityLevel;

  private BigDecimal probabilityScore;

  private FeasibilityMetricsResponse metrics;

  private List<ValidationCheckResponse> preliminaryChecks;

  private ProbabilityAnalysisResponse probabilityAnalysis;

  private List<RecommendationResponse> recommendations;

  private List<WarningResponse> warnings;

  private Instant validationTimestamp;

  private Instant createdAt;

  private Instant updatedAt;
}

