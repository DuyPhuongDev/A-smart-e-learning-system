package com.hcmut.lms.personalization.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLearningPathRequest {

  private Integer totalCredits;
  private Integer estimatedDurationSemesters;
  private BigDecimal predictedGpa;
  private BigDecimal completionRate;
}

