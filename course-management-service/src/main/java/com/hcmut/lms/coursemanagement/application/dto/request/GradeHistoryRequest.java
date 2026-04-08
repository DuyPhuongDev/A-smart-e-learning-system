package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistoryRequest {

  @Valid
  @NotNull
  private StudentInfo studentInfo;

  @Valid
  @NotEmpty
  private List<GradeRecord> gradeRecords;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class StudentInfo {
    @NotBlank
    private String studentCode;

    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    // CS
    private String specializationCode;

    // 22
    private String intakeYearCode;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GradeRecord {
    @NotBlank
    private String subjectCode;

    @NotNull
    private Integer credits;

    private Float gradeNumeric;

    private String gradeLetter;

    // HK221
    @NotBlank
    private String semesterCode;

    // 22
    @NotBlank
    private String academicYearCode;

    private Boolean isPassed;
  }
}
