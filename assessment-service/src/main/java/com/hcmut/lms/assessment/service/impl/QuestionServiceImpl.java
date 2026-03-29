package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.questionBank.QuestionBank;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.exception.UnsupportedQuestionTypeException;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.QuestionBankRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.QuestionService;
import com.hcmut.lms.common.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final QuestionBankRepository questionBankRepository;
    private final Map<QuestionType, QuestionHandler> handlerRegistry;

    /**
     * Spring injects all QuestionHandler beans;
     * indexed by QuestionType for O(1) dispatch of both factory and strategy calls.
     */
    public QuestionServiceImpl(List<QuestionHandler> handlers,
                               QuestionRepository questionRepository,
                               QuestionBankRepository questionBankRepository,
                               QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.questionBankRepository = questionBankRepository;
        this.handlerRegistry = handlers.stream()
                .collect(Collectors.toMap(QuestionHandler::getSupportedType, Function.identity()));
    }

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {
        Question question = resolve(request.getQuestionType()).create(request);
        // validate questionBank
        if(request.getQuestionBankId() != null) {
            QuestionBank questionBank = questionBankRepository.findById(request.getQuestionBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question bank not found"));

            question.getBanks().add(questionBank);
        }

        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(UUID id) {
        return questionMapper.toResponse(findById(id));
    }

    @Override
    public QuestionResponse updateQuestion(UUID id, QuestionRequest request) {
        Question existing = findById(id);
        Question updated = resolve(existing.getQuestionType()).update(existing, request);
        return questionMapper.toResponse(questionRepository.save(updated));
    }

    @Override
    public void deleteQuestion(UUID id) {
        findById(id);
        questionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<QuestionResponse> listQuestions(UUID bankId, QuestionType questionType, Pageable pageable) {
        Page<Question> page;
        if (bankId != null && questionType != null) {
            page = questionRepository.findAllByBankIdAndType(bankId, questionType, pageable);
        } else if (bankId != null) {
            page = questionRepository.findAllByBankId(bankId, pageable);
        } else if (questionType != null) {
            page = questionRepository.findAllByQuestionType(questionType, pageable);
        } else {
            page = questionRepository.findAll(pageable);
        }
        return PageResponse.fromPage(page.map(questionMapper::toResponse));
    }

    private QuestionHandler resolve(QuestionType type) {
        QuestionHandler handler = handlerRegistry.get(type);
        if (handler == null) throw new UnsupportedQuestionTypeException(type);
        return handler;
    }

    private Question findById(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
    }
}
