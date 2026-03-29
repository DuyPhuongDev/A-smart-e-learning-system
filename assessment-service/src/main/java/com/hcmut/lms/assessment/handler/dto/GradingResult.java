package com.hcmut.lms.assessment.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradingResult {
    private UUID questionId;
    private BigDecimal earnedPoints;
    private BigDecimal maxPoints;
    private GradingStatus status;
    private String detail;
}
