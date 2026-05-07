package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemesterGpaResponse {
    private String semesterCode;
    private Double semesterGpa10;
    private Double semesterGpa4;
    private Double cumulativeGpa10;
    private Double cumulativeGpa4;
    private Integer totalCredits;
    private Integer gradedCredits;
}
