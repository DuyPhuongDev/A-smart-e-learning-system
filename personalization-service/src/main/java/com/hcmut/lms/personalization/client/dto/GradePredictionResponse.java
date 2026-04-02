package com.hcmut.lms.personalization.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradePredictionResponse {

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
    private Double confidenceIntervalWidth;

    private UUID modelVersionId;
    private String modelVersionName;
    private String predictionTimestamp;

    private Boolean isActualGrade;
    private Double actualGrade;
}
