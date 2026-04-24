package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedSubjectResponse {

    private UUID subjectId;
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private String recommendationReason;
    private BigDecimal importanceScore;
    private String preferenceCategory;
    private Double predictedGrade;
}