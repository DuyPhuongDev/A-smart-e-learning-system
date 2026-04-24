package com.hcmut.lms.assessment.client;

import com.hcmut.lms.assessment.client.dto.ClassGradingWeightDto;
import com.hcmut.lms.assessment.client.dto.ClassSectionReportMetadataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "course-management-service", path = "/api/courses/internal")
public interface CourseManagementInternalClient {

    @GetMapping("/class-sections/{id}/report-metadata")
    ClassSectionReportMetadataResponse getClassSectionReportMetadata(@PathVariable("id") UUID id);

    @GetMapping("/class-sections/{classId}/grading-weights")
    List<ClassGradingWeightDto> getGradingWeights(@PathVariable("classId") UUID classId);
}

