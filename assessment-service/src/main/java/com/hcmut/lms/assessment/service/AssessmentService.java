package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.request.question.ReorderRequest;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.GradingBreakdownResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AssessmentService {

    AssessmentResponse createAssessment(AssessmentRequest request);

    AssessmentResponse getAssessment(UUID id);

    AssessmentResponse updateAssessment(UUID id, AssessmentRequest request);

    void deleteAssessment(UUID id);

    PageResponse<AssessmentResponse> listByClass(UUID classId, Pageable pageable);

    void addQuestion(UUID assessmentId, List<AddQuestionRequest> request);

    void removeQuestion(UUID assessmentId, UUID questionId);

    List<QuestionResponse> getQuestions(UUID assessmentId);

    void changeStatus(UUID id, AssessmentStatus status);

    List<GradingBreakdownResponse> getGradingBreakdownForClass(UUID classId);

     QuestionResponse createQuestionsForAssessment(UUID assessmentId, AssessmentQuestionRequest request);

    QuestionResponse updateQuestionsForAssessment(UUID id, UUID questionId, AssessmentQuestionRequest request);

    void reorderQuestionsInAssessment(UUID id, UUID questionId, @Valid @RequestBody ReorderRequest request);

}
