package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathChangeRecordResponse {

  private String type;
  private String subjectCode;
  private String subjectName;
  private String reason;
  private Integer fromSemesterOrder;
  private Integer toSemesterOrder;
  private Instant appliedAt;
}

