package com.hcmut.lms.assessment.controller.internal;

import com.hcmut.lms.assessment.dto.response.AssessmentGrade;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.PendingAssessmentCountResponse;
import com.hcmut.lms.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/assessments")
public class AssessmentInternalController {

    private final AssessmentService assessmentService;

    @GetMapping("/class/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<AssessmentGrade> getGradesByClass(@PathVariable UUID id) {
        return assessmentService.getGradesByClass(id);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.OK)
    public List<AssessmentResponse> getAssessmentsByClassIds(@RequestBody List<UUID> classIds) {
        return assessmentService.getAssessmentsByClassIds(classIds);
    }

    @PostMapping("/batch/pending-counts")
    @ResponseStatus(HttpStatus.OK)
    public List<PendingAssessmentCountResponse> getPendingAssessmentCounts(
            @RequestBody PendingAssessmentCountRequest request) {
        return assessmentService.getPendingAssessmentCounts(request.getClassIds(), request.getStudentId());
    }
}
