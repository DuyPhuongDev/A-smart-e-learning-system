package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AssessmentService {

    AssessmentResponse createAssessment(AssessmentRequest request);

    AssessmentResponse getAssessment(UUID id);

    AssessmentResponse updateAssessment(UUID id, AssessmentRequest request);

    void deleteAssessment(UUID id);

    PageResponse<AssessmentResponse> listByClass(UUID classId, Pageable pageable);

    void addQuestion(UUID assessmentId, AddQuestionRequest request);

    void removeQuestion(UUID assessmentId, UUID questionId);

    List<QuestionResponse> getQuestions(UUID assessmentId);
}
