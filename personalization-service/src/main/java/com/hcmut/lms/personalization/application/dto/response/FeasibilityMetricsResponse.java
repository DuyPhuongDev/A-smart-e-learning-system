package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeasibilityMetricsResponse {

  private Integer remainingCredits;

  private Integer availableMainSemesters;

  private Integer availableSummerSemesters;

  private Double requiredAverageGpa;

  private Integer longestPrerequisiteChain;
}

