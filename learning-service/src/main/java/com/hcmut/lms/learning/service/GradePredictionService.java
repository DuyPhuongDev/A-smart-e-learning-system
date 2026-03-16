package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.dto.request.RawFeaturePredictionRequest;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;

import java.util.UUID;

public interface GradePredictionService {
    /**
     * Predict grade for a student in a specific subject.
     * The current semester is automatically resolved based on the current date.
     * Enrollment in a concrete class section is not required.
     *
     * @param studentId Student UUID
     * @param subjectId Subject UUID
     * @param plannedSemesterCredits Optional planned credits for target semester (default: 17)
     * @param threshold Optional threshold t for P(g4 >= t) (default: 2.0)
     * @return GradePredictionResponse with prediction and uncertainty details
     */
    GradePredictionResponse predictGrade(UUID studentId,
                                         UUID subjectId,
                                         Integer plannedSemesterCredits,
                                         Double threshold);

    /**
     * Predict grade using a raw feature map supplied by the client.
     *
     * @param request raw feature map and optional threshold
     * @return GradePredictionResponse with prediction and uncertainty details
     */
    GradePredictionResponse predictGradeFromRawFeatures(RawFeaturePredictionRequest request);
}
