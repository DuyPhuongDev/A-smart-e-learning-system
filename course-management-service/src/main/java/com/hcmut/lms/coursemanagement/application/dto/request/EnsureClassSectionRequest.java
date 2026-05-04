package com.hcmut.lms.coursemanagement.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnsureClassSectionRequest {
  private UUID subjectId;
  private UUID semesterId;
  private UUID createdBy;
}
