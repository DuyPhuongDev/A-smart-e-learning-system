package com.hcmut.lms.learning.client.fallback;

import com.hcmut.lms.learning.client.AssessmentClient;
import com.hcmut.lms.learning.client.dto.AssessmentResponse;
import com.hcmut.lms.learning.client.dto.PendingCountRequest;
import com.hcmut.lms.learning.client.dto.PendingCountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class AssessmentClientFallback implements AssessmentClient {

    @Override
    public List<AssessmentResponse> getAssessmentsByClassIds(List<UUID> classIds) {
        log.warn("Assessment service unavailable. Returning empty list.");
        return List.of();
    }

    @Override
    public List<PendingCountResponse> getPendingAssessmentCounts(PendingCountRequest request) {
        log.warn("Assessment service unavailable. Returning empty pending counts.");
        return List.of();
    }
}
