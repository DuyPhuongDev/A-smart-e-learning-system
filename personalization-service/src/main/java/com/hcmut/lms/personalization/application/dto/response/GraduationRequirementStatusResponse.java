package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraduationRequirementStatusResponse {

  private UUID graduationRequirementStatusId;
  private UUID graduationRequirementId;
  private String name;
  private String code;
  private String description;
  private BigDecimal thresholdValue;
  private String unit;
  private String evaluationRule;
  private Boolean isCompleted;
  private UUID completionSemesterId;
}

