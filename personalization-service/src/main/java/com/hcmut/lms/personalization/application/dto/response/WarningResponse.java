package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.personalization.application.dto.response.enums.WarningSeverity;
import com.hcmut.lms.personalization.application.dto.response.enums.WarningType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarningResponse {

  private WarningType type;

  private WarningSeverity severity;

  private String message;

  private List<FeasibilityMissingRequirementResponse> missingRequirements;

  private List<String> missingRequirementIds;
}
