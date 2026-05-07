package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDataGenerationResponse {
  private String semesterCode;
  private Integer semesterOrder;
  private int subjectsGenerated;
  private int passedCount;
  private int failedCount;
  private List<TestDataSubjectDetail> details;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TestDataSubjectDetail {
    private String subjectCode;
    private String subjectName;
    private Double grade;
    private Boolean isPassed;
  }
}
