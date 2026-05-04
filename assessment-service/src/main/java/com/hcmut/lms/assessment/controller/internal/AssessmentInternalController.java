package com.hcmut.lms.assessment.controller.internal;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.dto.response.AssessmentGrade;
import com.hcmut.lms.assessment.service.AssessmentService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
}

