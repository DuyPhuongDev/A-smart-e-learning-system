package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse getQuestion(UUID id);

    Question updateQuestion(UUID id, QuestionRequest request);

    void deleteQuestion(UUID id);

    PageResponse<QuestionResponse> listQuestions(UUID bankId, QuestionType questionType, Pageable pageable);

    List<QuestionResponse> listQuestionsInAssessment(UUID assessmentId);

    Question makeQuestion(QuestionRequest request);


}
