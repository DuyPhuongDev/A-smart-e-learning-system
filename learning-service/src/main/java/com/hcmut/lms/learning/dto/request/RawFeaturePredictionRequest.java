package com.hcmut.lms.learning.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawFeaturePredictionRequest {

    /** Enrollment context */
    @NotNull(message = "semCredits is required")
    @Min(value = 0, message = "semCredits must be >= 0")
    @Max(value = 40, message = "semCredits must be <= 40")
    private Integer semCredits;

    /** Optional; if null will be derived as semCredits^2 */
    @Min(value = 0, message = "semCreditsSquared must be >= 0")
    private Integer semCreditsSquared;

    @NotNull(message = "retakeNo is required")
    @Min(value = 0, message = "retakeNo must be >= 0")
    private Integer retakeNo;

    /** Student history */
    @NotNull(message = "numSemestersPrior is required")
    @Min(value = 0, message = "numSemestersPrior must be >= 0")
    private Integer numSemestersPrior;

    @NotNull(message = "cumulativeGradeAvg is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "cumulativeGradeAvg must be between 0.0 and 4.0")
    @DecimalMax(value = "4.0", inclusive = true, message = "cumulativeGradeAvg must be between 0.0 and 4.0")
    private Double cumulativeGradeAvg;

    @NotNull(message = "previousSemGradeAvg is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "previousSemGradeAvg must be between 0.0 and 4.0")
    @DecimalMax(value = "4.0", inclusive = true, message = "previousSemGradeAvg must be between 0.0 and 4.0")
    private Double previousSemGradeAvg;

    /** Subject baseline */
    @NotNull(message = "subjectHistMedianSmooth is required")
    private Double subjectHistMedianSmooth;

    /** Relative performance */
    @NotNull(message = "relativeAvgCourseGrade is required")
    private Double relativeAvgCourseGrade;

    /** Optional threshold t for P(g4 >= t). Defaults to service-level DEFAULT_THRESHOLD when null. */
    private Double threshold;
}
