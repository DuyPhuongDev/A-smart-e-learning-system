package com.hcmut.lms.coursemanagement.client.dto;

import com.hcmut.lms.coursemanagement.domain.entity.subject.GradingType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
public class AssessmentGrade {
    private UUID assessmentId;
    private String title;
    private BigDecimal weight;
    private GradingType assessmentType;
}