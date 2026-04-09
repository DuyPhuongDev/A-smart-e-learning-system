package com.hcmut.lms.assessment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "learning-service", path = "/api/learning/v1/enrollment/internal")
public interface LearningInternalEnrollmentClient {

    @GetMapping("/class/{classId}/students")
    List<UUID> getStudentIdsByClassId(@PathVariable("classId") UUID classId);
}

