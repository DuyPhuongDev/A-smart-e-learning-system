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
public class FeasibilityMissingRequirementResponse {

  private String requirementId;

  private String requirementStatusId;

  private String code;

  private String name;

  private String description;
}

