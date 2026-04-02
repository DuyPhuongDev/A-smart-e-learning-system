package com.hcmut.lms.personalization.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValuationOccupationRequest {
  @NotEmpty
  private List<UUID> subjectIds;

  private List<String> occupationCodes;
}
