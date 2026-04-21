package com.hcmut.lms.assessment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "learning-service",
        contextId = "assessmentLearningInternalEnrollmentClient",
        path = "/api/learning/internal/enrollments"
)
public interface LearningInternalEnrollmentClient {

    @GetMapping("/class/{classId}/students")
    List<UUID> getStudentIdsByClassId(@PathVariable("classId") UUID classId);

    @GetMapping("/check/{classId}")
    Boolean checkEnrollment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("classId") UUID classId
    );
}
