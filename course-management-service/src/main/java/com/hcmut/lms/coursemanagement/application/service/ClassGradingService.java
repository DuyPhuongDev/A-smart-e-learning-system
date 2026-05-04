package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.ClassGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.UpdateGradingWeightRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassGradingWeightResponse;

import java.util.List;
import java.util.UUID;

public interface ClassGradingService {

    List<ClassGradingResponse> getGradingsForClass(UUID classId);

    ClassGradingResponse addGradingToClass(UUID classId, ClassGradingRequest request);

    ClassGradingResponse updateGradingWeight(UUID classId, UUID gradingId, UpdateGradingWeightRequest request);

    void removeGradingFromClass(UUID classId, UUID gradingId);

    /**
     * Seed default gradings for teacher-owned classes (isOfficial == true).
     */
    void initDefaultGradings(UUID classId);

    List<ClassGradingWeightResponse> getGradingWeightsForClass(UUID classId);
}
