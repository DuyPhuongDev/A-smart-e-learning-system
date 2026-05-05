package com.hcmut.lms.learning.client;

import com.hcmut.lms.learning.client.dto.AssessmentResponse;
import com.hcmut.lms.learning.client.dto.PendingCountRequest;
import com.hcmut.lms.learning.client.dto.PendingCountResponse;
import com.hcmut.lms.learning.client.fallback.AssessmentClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "assessment-service",
        path = "/api/internal/assessments",
        fallback = AssessmentClientFallback.class
)
public interface AssessmentClient {

    @PostMapping("/batch")
    List<AssessmentResponse> getAssessmentsByClassIds(@RequestBody List<UUID> classIds);

    @PostMapping("/batch/pending-counts")
    List<PendingCountResponse> getPendingAssessmentCounts(@RequestBody PendingCountRequest request);
}
