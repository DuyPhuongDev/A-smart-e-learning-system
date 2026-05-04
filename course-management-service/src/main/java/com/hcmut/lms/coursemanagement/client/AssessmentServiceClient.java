package com.hcmut.lms.coursemanagement.client;

import com.hcmut.lms.coursemanagement.client.dto.AssessmentGrade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "assessment-service")
public interface AssessmentServiceClient {

    @GetMapping("/api/internal/assessments/class/{id}")
    List<AssessmentGrade> getGradesByClass(@PathVariable UUID id);

}
