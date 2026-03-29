package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.GradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.GradingResponse;

import java.util.List;
import java.util.UUID;

public interface GradingService {
    GradingResponse createGrading(GradingRequest request);
    GradingResponse updateGrading(UUID id, GradingRequest request);
    GradingResponse getGradingById(UUID id);
    List<GradingResponse> getAllGradings();
    void deleteGrading(UUID id);
}


