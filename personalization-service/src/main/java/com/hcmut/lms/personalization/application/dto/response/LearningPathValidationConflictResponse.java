package com.hcmut.lms.personalization.application.dto.response;

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
public class LearningPathValidationConflictResponse {

  private String conflictType;
  private Integer semesterOrder;
  private String description;
  private String suggestedResolution;
  private List<UUID> relatedSubjectIds;
}

