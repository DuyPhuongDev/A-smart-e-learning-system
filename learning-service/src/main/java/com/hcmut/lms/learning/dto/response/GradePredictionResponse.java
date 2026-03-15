package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradePredictionResponse {
    private UUID studentId;
    private UUID subjectId;
    private UUID semesterId;

    // Prediction results (4-point scale)
    private Double rawPredictedGrade;         // g_hat4
    private Double correctedPredictedGrade;   // g_tilde = g_hat4 + mu_R
    private Double residualMean;              // mu_R
    private Double residualStd;               // sigma_R
    private Double threshold;                 // t
    private Double probabilityAboveThreshold; // P(g4 >= t)

    // Optional compatibility interval
    private Double lowerBound95;
    private Double upperBound95;
    private Double confidenceIntervalWidth;

    // Model metadata
    private UUID modelVersionId;
    private String modelVersionName;
    private Instant predictionTimestamp;

    // Actual grade (if subject already completed in a prior semester)
    private Boolean isActualGrade;
    private Double actualGrade;
}
