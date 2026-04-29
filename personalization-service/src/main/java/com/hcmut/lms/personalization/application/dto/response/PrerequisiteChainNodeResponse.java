package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrerequisiteChainNodeResponse {

  private UUID id;
  private String subjectCode;
  private String subjectName;
  private Integer credits;
  private Integer semesterOrder;
  private String semesterLabel;
  private Boolean isCompleted;
  private Boolean isCurrentSemester;
}