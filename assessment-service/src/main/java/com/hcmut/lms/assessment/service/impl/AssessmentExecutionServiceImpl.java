package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.exception.UnsupportedQuestionTypeException;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.handler.dto.FeedbackDto;
import com.hcmut.lms.assessment.handler.dto.GradingResult;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.AssessmentExecutionService;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AssessmentExecutionServiceImpl implements AssessmentExecutionService {

    private final QuestionRepository questionRepository;
    private final Map<QuestionType, QuestionHandler> handlerRegistry;

    /**
     * Same QuestionHandler beans used by QuestionService — no duplication.
     * Factory role (create/update) is in QuestionService;
     * Strategy role (validate/grade/feedback) is used here.
     */
    public AssessmentExecutionServiceImpl(List<QuestionHandler> handlers,
                                          QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.handlerRegistry = handlers.stream()
                .collect(Collectors.toMap(QuestionHandler::getSupportedType, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = RuntimeException.class)
    public GradingResponse submitAnswer(UUID questionId, SubmissionDto submission, BigDecimal maxScore) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        question = (Question) Hibernate.unproxy(question);

        QuestionHandler handler = resolve(question.getQuestionType());
        handler.validate(question, submission);
        GradingResult result = handler.grade(question, submission, maxScore);
        FeedbackDto feedback = handler.generateFeedback(question, result);

        return GradingResponse.from(result, feedback);
    }

    private QuestionHandler resolve(QuestionType type) {
        QuestionHandler handler = handlerRegistry.get(type);
        if (handler == null) throw new UnsupportedQuestionTypeException(type);
        return handler;
    }
}
