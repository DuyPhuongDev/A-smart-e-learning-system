package com.hcmut.lms.personalization.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class GraduationRequirementResponse {

    private UUID graduationRequirementId;
    private String name;
    private String code;
    private String description;
    private BigDecimal thresholdValue;
    private String unit;
    private String evaluationRule;
}

