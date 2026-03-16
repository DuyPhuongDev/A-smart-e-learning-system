package com.hcmut.lms.learning.dto.internal;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Internal DTO holding computed prediction values before mapping to response.
 */
@Getter
@Builder
public class PredictionResult {
    private UUID studentId;
    private UUID subjectId;
    private UUID semesterId;

    private Double rawPredictedGrade;
    private Double correctedPredictedGrade;
    private Double residualMean;
    private Double residualStd;
    private Double threshold;
    private Double probabilityAboveThreshold;

    private Double lowerBound95;
    private Double upperBound95;

    private UUID modelVersionId;
    private String modelVersionName;
}
