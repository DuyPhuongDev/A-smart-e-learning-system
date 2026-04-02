package com.hcmut.lms.personalization.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLearningGoalRequest {

  private String specializationId;

  @DecimalMin(value = "0.0", inclusive = true, message = "targetGpa must be between 0 and 4")
  @DecimalMax(value = "4.0", inclusive = true, message = "targetGpa must be between 0 and 4")
  private BigDecimal targetGpa;

  @NotNull(message = "expectedCompletedSemesterId is required")
  private UUID expectedCompletedSemesterId;

  @NotBlank(message = "prefMainSemLearnIntensity is required")
  private String prefMainSemLearnIntensity;

  @NotNull(message = "plannedSummerSemCount is required")
  @Min(value = 0, message = "plannedSummerSemCount must be >= 0")
  private Integer plannedSummerSemCount;

  private String targetOccupationCode;

  private Integer attemptTargetGpaOrder;

  private Integer focusOnTargetOccupation;

  private Integer completedOnTime;
}


