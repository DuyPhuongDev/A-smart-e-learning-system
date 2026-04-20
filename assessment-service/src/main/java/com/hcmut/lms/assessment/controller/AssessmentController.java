package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.GradingBreakdownResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.service.AssessmentService;
import com.hcmut.lms.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentResponse createAssessment(@Valid @RequestBody AssessmentRequest request) {
        return assessmentService.createAssessment(request);
    }

    @GetMapping("/{id}")
    public AssessmentResponse getAssessment(@PathVariable UUID id) {
        return assessmentService.getAssessment(id);
    }

    @PutMapping("/{id}")
    public AssessmentResponse updateAssessment(@PathVariable UUID id,
                                               @Valid @RequestBody AssessmentRequest request) {
        return assessmentService.updateAssessment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAssessment(@PathVariable UUID id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<AssessmentResponse> listByClass(
            @RequestParam UUID classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return assessmentService.listByClass(classId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @PostMapping("/{id}/questions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> addQuestion(@PathVariable UUID id,
                                            @Valid @RequestBody List<AddQuestionRequest> request) {
        assessmentService.addQuestion(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeQuestion(@PathVariable UUID id,
                                               @PathVariable UUID questionId) {
        assessmentService.removeQuestion(id, questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/questions")
    public List<QuestionResponse> getQuestions(@PathVariable UUID id) {
        return assessmentService.getQuestions(id);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Void> publishAssessment(@PathVariable UUID id) {
        assessmentService.changeStatus(id, AssessmentStatus.PUBLISHED);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/classes/{classId}/grading-breakdown")
    public ResponseEntity<List<GradingBreakdownResponse>> getGradingBreakdown(@PathVariable UUID classId) {
        return ResponseEntity.ok(assessmentService.getGradingBreakdownForClass(classId));
    }
}
