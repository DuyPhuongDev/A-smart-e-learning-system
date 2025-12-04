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
public class CurriculumSectionResponse {
    private UUID id;
    private String name;
    private String notes;
    private Integer requiredCredits;
    private Integer displayOrder;
    private String description;
    private String curriculumCode;
    private UUID curriculumSpecializationId;
    private UUID curriculumIntakeYearId;
    private String createdAt;
    private String updatedAt;
}

