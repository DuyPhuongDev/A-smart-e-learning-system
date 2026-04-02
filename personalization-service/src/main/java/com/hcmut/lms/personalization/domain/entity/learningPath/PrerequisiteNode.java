package com.hcmut.lms.personalization.domain.entity.learningPath;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrerequisiteNode {
  private UUID subjectId;
  private String subjectCode;
  private String subjectName;
  private Boolean required;
}

