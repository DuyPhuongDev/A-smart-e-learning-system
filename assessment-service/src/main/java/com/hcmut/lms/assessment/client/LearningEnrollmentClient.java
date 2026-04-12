package com.hcmut.lms.assessment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "learning-service",
        contextId = "assessmentLearningEnrollmentClient",
        path = "/api/learning/v1/enrollment"
)
public interface LearningEnrollmentClient {

    @GetMapping("/check/{classId}")
    Boolean checkEnrollment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("classId") UUID classId
    );
}
