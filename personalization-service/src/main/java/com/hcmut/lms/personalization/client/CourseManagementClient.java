package com.hcmut.lms.personalization.client;

import com.hcmut.lms.personalization.client.dto.SubjectLearningOutcomeResponse;
import com.hcmut.lms.personalization.client.dto.SubjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "course-management-service", path = "/api/courses/internal")
public interface CourseManagementClient {

    @GetMapping("/subjects/{subjectId}")
    SubjectResponse getSubjectById(@PathVariable UUID subjectId);

    @GetMapping("/subjects/{subjectId}/learning-outcomes")
    List<SubjectLearningOutcomeResponse> getLearningOutcomes(@PathVariable UUID subjectId);
}
