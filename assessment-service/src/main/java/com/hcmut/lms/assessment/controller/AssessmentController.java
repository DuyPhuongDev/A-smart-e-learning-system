package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.request.assessment.UpdateWeightRequest;
import com.hcmut.lms.assessment.dto.request.question.ReorderRequest;
import com.hcmut.lms.assessment.dto.response.*;
import com.hcmut.lms.assessment.service.AssessmentService;
import com.hcmut.lms.assessment.service.QuestionImportService;
import com.hcmut.lms.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final QuestionImportService questionImportService;

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

    @PostMapping("/{id}/create-question")
    public ResponseEntity<QuestionResponse> createQuestionForAssessment(@PathVariable UUID id, @Valid @RequestBody AssessmentQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assessmentService.createQuestionsForAssessment(id, request));

    }

    @PutMapping("/{id}/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestionInAssessment(@PathVariable UUID id,
                                                                       @PathVariable UUID questionId,
                                                                       @Valid @RequestBody AssessmentQuestionRequest request) {
        return ResponseEntity.ok(assessmentService.updateQuestionsForAssessment(id, questionId, request));
    }

    @PutMapping("/{id}/questions/{questionId}/re-order")
    public ResponseEntity<Void> reorderQuestionsInAssessment(@PathVariable UUID id, @PathVariable UUID questionId, @Valid @RequestBody ReorderRequest request) {
        assessmentService.reorderQuestionsInAssessment(id, questionId, request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/{id}/questions/import/mcq", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionImportResultResponse> importMcqQuestions(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        QuestionImportResultResponse result = questionImportService.importMcqQuestions(file, id , false);
        return ResponseEntity.ok(result);
    }


    @PostMapping(value = "/{id}/questions/import/essay", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionImportResultResponse> importEssayQuestions(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        QuestionImportResultResponse result = questionImportService.importEssayQuestions(file, id , false);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/update-weight")
    public AssessmentGrade updateWeight(@PathVariable UUID id, @Valid @RequestBody UpdateWeightRequest request) {
        return assessmentService.updateGrade(id, request);
    }
}
