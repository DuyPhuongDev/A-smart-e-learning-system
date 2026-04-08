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
public class CurriculumResponse {
    private String code;
    private UUID specializationId;
    private String specializationName;
    private UUID intakeYearId;
    private String intakeYearStartDate;
    private String name;
    private Integer totalCredits;
    private String description;
    private String document;
    private String createdAt;
    private String updatedAt;
}

