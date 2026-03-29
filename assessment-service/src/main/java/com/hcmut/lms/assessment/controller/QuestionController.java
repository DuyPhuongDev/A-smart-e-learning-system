package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.service.QuestionService;
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
@RequestMapping("${prefix-api}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Creates a question. The payload must include a "questionType" discriminator field
     * (MCQ | CODING | ESSAY) and the type-specific properties.
     *
     * Example MCQ payload:
     * {
     *   "questionType": "MCQ",
     *   "difficultLevel": "EASY",
     *   "point": 1.0,
     *   "required": true,
     *   "allowMultiAnswer": false,
     *   "shuffleOption": true,
     *   "answerOptions": [
     *     { "content": "Paris", "correct": true, "orderIndex": 0 },
     *     { "content": "London", "correct": false, "orderIndex": 1 }
     *   ]
     * }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse createQuestion(@Valid @RequestBody QuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @GetMapping("/{id}")
    public QuestionResponse getQuestion(@PathVariable UUID id) {
        return questionService.getQuestion(id);
    }

    @PutMapping("/{id}")
    public QuestionResponse updateQuestion(@PathVariable UUID id,
                                           @Valid @RequestBody QuestionRequest request) {
        return questionService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<QuestionResponse> listQuestions(
            @RequestParam(required = false) UUID bankId,
            @RequestParam(required = false) QuestionType questionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            return questionService.listQuestions(bankId, questionType,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @GetMapping("/by-assessment/{id}")
    public List<QuestionResponse> listQuestionsByAssessmentId(@PathVariable UUID id) {
        return questionService.listQuestionsInAssessment(id);
    }
}
