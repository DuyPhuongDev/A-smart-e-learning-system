package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackfillRequirementEmbeddingsResponse {
  private long missingCount;
  private long embeddedCount;
  private boolean migrationGenerated;
  private String migrationFile;
}

