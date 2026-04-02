package com.hcmut.lms.coursemanagement.application.dto.response;

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
public class InternalGraduationRequirementResponse {

    private UUID graduationRequirementId;
    private String name;
    private String code;
    private String description;
    private BigDecimal thresholdValue;
    private String unit;
    private String evaluationRule;
}

