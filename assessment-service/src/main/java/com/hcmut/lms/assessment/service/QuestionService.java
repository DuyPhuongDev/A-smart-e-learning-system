package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse getQuestion(UUID id);

    QuestionResponse updateQuestion(UUID id, QuestionRequest request);

    void deleteQuestion(UUID id);

    PageResponse<QuestionResponse> listQuestions(UUID bankId, QuestionType questionType, Pageable pageable);
}
