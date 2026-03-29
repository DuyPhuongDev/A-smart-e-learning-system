package com.hcmut.lms.learning.dto.internal;

import lombok.Builder;
import lombok.Getter;

/**
 * Internal DTO holding all extracted features before conversion to Map for ONNX inference.
 */
@Getter
@Builder
public class ExtractedFeatures {
    // Semester features
    private int semCredits;
    private int retakeNo;

    // Student history features
    private int numSemestersPrior;
    private double cumulativeGradeAvg;
    private double previousSemGradeAvg;

    // Subject baseline features
    private double subjectHistMedianSmooth;

    // Relative course features
    private double relativeAvgCourseGrade;
}
