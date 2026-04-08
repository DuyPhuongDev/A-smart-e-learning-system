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
public class ProbabilityAnalysisDetailsResponse {

  private Double probabilityScore;

  private String note;

  private Integer sampleSize;

  private Double predictedFinalGpa;

  private Double standardDeviation;

  private Integer predictionsCount;
}

