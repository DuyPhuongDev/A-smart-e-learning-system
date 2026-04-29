package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.answer.EssayAcceptedFileType;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.exception.UnsupportedQuestionTypeException;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.AnswerOptionRepository;
import com.hcmut.lms.assessment.repository.AssessmentQuestionRepository;
import com.hcmut.lms.assessment.repository.EssayAcceptFileTypeRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.QuestionService;
import com.hcmut.lms.common.dto.PageResponse;
import org.hibernate.Hibernate;
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
    private final Map<QuestionType, QuestionHandler> handlerRegistry;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final EssayAcceptFileTypeRepository essayAcceptFileTypeRepository;

    /**
     * Spring injects all QuestionHandler beans;
     * indexed by QuestionType for O(1) dispatch of both factory and strategy calls.
     */
    public QuestionServiceImpl(List<QuestionHandler> handlers,
                               QuestionRepository questionRepository,
                               AssessmentQuestionRepository assessmentQuestionRepository,
                               AnswerOptionRepository answerOptionRepository,
                               EssayAcceptFileTypeRepository essayAcceptFileTypeRepository,
                               QuestionMapper questionMapper
    ) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.answerOptionRepository = answerOptionRepository;
        this.assessmentQuestionRepository = assessmentQuestionRepository;
        this.essayAcceptFileTypeRepository = essayAcceptFileTypeRepository;
        this.handlerRegistry = handlers.stream()
                .collect(Collectors.toMap(QuestionHandler::getSupportedType, Function.identity()));
    }

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {
        Question question = resolve(request.getQuestionType()).create(request);

        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(UUID id) {
        return questionMapper.toResponse(findById(id));
    }

    @Override
    public Question updateQuestion(UUID id, QuestionRequest request) {
        Question existing = findById(id);
        if (existing instanceof McqQuestion mcq) {
            answerOptionRepository.deleteByQuestionId(mcq.getId());
            answerOptionRepository.flush();
            mcq.getAnswerOptions().clear();
        }else if(existing instanceof EssayQuestion essay) {
            essayAcceptFileTypeRepository.deleteAllByEssayQuestion_Id(essay.getId());
            essayAcceptFileTypeRepository.flush();
            essay.getAcceptedFileTypes().clear();
        }
        Question updated = resolve(existing.getQuestionType()).update(existing, request);
        return questionRepository.save(updated);
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

    @Override
    public List<QuestionResponse> listQuestionsInAssessment(UUID assessmentId) {
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId);
        return  assessmentQuestions.stream().map( i ->{
            QuestionResponse questionResponse = questionMapper.toResponse((Question) Hibernate.unproxy(i.getQuestion()));
            questionResponse.setOrderIndex( i.getOrderIndex());
            questionResponse.setPoint(i.getPoint());
            return questionResponse;
        }).toList();
    }

    @Override
    public Question makeQuestion(QuestionRequest request) {
        return resolve(request.getQuestionType()).create(request);
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
