package com.hcmut.lms.personalization.application.dto.request;

import com.hcmut.lms.personalization.application.dto.response.LearningGoalFeasibilityResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLearningGoalRequest {

  private String specializationId;

  @DecimalMin(value = "0.0", inclusive = true, message = "targetGpa must be between 0 and 4")
  @DecimalMax(value = "4.0", inclusive = true, message = "targetGpa must be between 0 and 4")
  private BigDecimal targetGpa;

  private UUID expectedCompletedSemesterId;

  private String prefMainSemLearnIntensity;

  @Min(value = 0, message = "plannedSummerSemCount must be >= 0")
  private Integer plannedSummerSemCount;

  private String targetOccupationCode;

  private Integer attemptTargetGpaOrder;

  private Integer focusOnTargetOccupation;

  private Integer completedOnTime;

  private List<@Valid CreatePreferredSummerSemesterRequest> summerSemesters;

  @Valid
  private LearningGoalFeasibilityResponse validationResult;
}