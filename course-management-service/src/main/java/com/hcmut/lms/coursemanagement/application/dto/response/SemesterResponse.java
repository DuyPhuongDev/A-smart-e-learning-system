package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemesterResponse {
  private UUID id;
  private String semesterCode;
  private String startDate;
  private String endDate;
  private UUID academicYearId;
  private String academicYearCode;
  private Integer semKey;
  private String createdAt;
  private String updatedAt;
}