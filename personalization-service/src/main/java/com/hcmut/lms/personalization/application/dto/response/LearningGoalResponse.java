package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningGoalResponse {

  private UUID learningGoalId;
  private UUID studentId;
  private String specializationId;
  private BigDecimal targetGpa;
  private UUID expectedCompletedSemesterId;
  private String prefMainSemLearnIntensity;
  private Integer plannedSummerSemCount;
  private String targetOccupationCode;
  private Integer attemptTargetGpaOrder;
  private Integer focusOnTargetOccupation;
  private Integer completedOnTime;
  private Boolean isActive;
  private GoalValidationResultResponse goalValidationResult;
  private Instant createdAt;
  private Instant updatedAt;
}

