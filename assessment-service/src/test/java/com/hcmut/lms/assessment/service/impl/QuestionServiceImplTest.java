package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.question.*;
import com.hcmut.lms.assessment.dto.request.question.EssayQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.McqQuestionRequest;
import com.hcmut.lms.assessment.dto.response.CodingQuestionResponse;
import com.hcmut.lms.assessment.dto.response.McqQuestionResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.exception.UnsupportedQuestionTypeException;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.AnswerOptionRepository;
import com.hcmut.lms.assessment.repository.AssessmentQuestionRepository;
import com.hcmut.lms.assessment.repository.EssayAcceptFileTypeRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.common.dto.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private AssessmentQuestionRepository assessmentQuestionRepository;

    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @Mock
    private EssayAcceptFileTypeRepository essayAcceptFileTypeRepository;

    @Mock
    private QuestionHandler questionHandler;

    private QuestionServiceImpl questionService;

    private QuestionServiceImpl createService() {
        // getSupportedType must already be stubbed
        return new QuestionServiceImpl(
                List.of(questionHandler),
                questionRepository,
                assessmentQuestionRepository,
                answerOptionRepository,
                essayAcceptFileTypeRepository,
                questionMapper);
    }

    // --- createQuestion ---

    @Test
    void createQuestion_shouldReturnResponse_whenValidRequest() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        McqQuestionRequest request = createMcqRequest();
        McqQuestion entity = createMcqQuestion();
        McqQuestion saved = createMcqQuestion();
        saved.setId(UUID.randomUUID());
        McqQuestionResponse expected = createMcqResponse(saved.getId());

        when(questionHandler.create(request)).thenReturn(entity);
        when(questionRepository.save(entity)).thenReturn(saved);
        when(questionMapper.toResponse(any(Question.class))).thenReturn(expected);

        QuestionResponse result = questionService.createQuestion(request);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void createQuestion_shouldThrowException_whenUnsupportedType() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        EssayQuestionRequest request = new EssayQuestionRequest();
        request.setQuestionType(QuestionType.ESSAY);

        assertThatThrownBy(() -> questionService.createQuestion(request))
                .isInstanceOf(UnsupportedQuestionTypeException.class);
    }

    // --- getQuestion ---

    @Test
    void getQuestion_shouldReturnResponse_whenExists() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID id = UUID.randomUUID();
        McqQuestion entity = createMcqQuestion();
        entity.setId(id);
        McqQuestionResponse expected = createMcqResponse(id);

        when(questionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(questionMapper.toResponse(any(Question.class))).thenReturn(expected);

        QuestionResponse result = questionService.getQuestion(id);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getQuestion_shouldThrowException_whenNotFound() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID id = UUID.randomUUID();
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getQuestion(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Question");
    }

    // --- updateQuestion ---

    @Test
    void updateQuestion_shouldUpdateMcqQuestion() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID id = UUID.randomUUID();
        McqQuestionRequest request = createMcqRequest();
        McqQuestion existing = createMcqQuestion();
        existing.setId(id);
        Question updated = createMcqQuestion();
        updated.setId(id);

        when(questionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(questionHandler.update(existing, request)).thenReturn(updated);
        when(questionRepository.save(updated)).thenReturn(updated);

        Question result = questionService.updateQuestion(id, request);

        assertThat(result).isEqualTo(updated);
        verify(answerOptionRepository).deleteByQuestionId(id);
    }

    @Test
    void updateQuestion_shouldUpdateEssayQuestion() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.ESSAY);
        questionService = createService();
        UUID id = UUID.randomUUID();
        EssayQuestion existing = createEssayQuestion();
        existing.setId(id);
        EssayQuestionRequest request = createEssayRequest();
        Question updated = createEssayQuestion();
        updated.setId(id);

        when(questionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(questionHandler.update(existing, request)).thenReturn(updated);
        when(questionRepository.save(updated)).thenReturn(updated);

        Question result = questionService.updateQuestion(id, request);

        assertThat(result).isEqualTo(updated);
        verify(essayAcceptFileTypeRepository).deleteAllByEssayQuestion_Id(id);
    }

    // --- deleteQuestion ---

    @Test
    void deleteQuestion_shouldDelete_whenExists() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID id = UUID.randomUUID();
        McqQuestion entity = createMcqQuestion();
        entity.setId(id);

        when(questionRepository.findById(id)).thenReturn(Optional.of(entity));

        questionService.deleteQuestion(id);

        verify(questionRepository).deleteById(id);
    }

    @Test
    void deleteQuestion_shouldThrowException_whenNotFound() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID id = UUID.randomUUID();
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.deleteQuestion(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Question");
    }

    // --- listQuestions ---

    @Test
    void listQuestions_shouldReturnByBankId() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID bankId = UUID.randomUUID();
        McqQuestion q1 = createMcqQuestion();
        q1.setId(UUID.randomUUID());
        Page<Question> page = new PageImpl<>(List.of(q1));
        McqQuestionResponse resp = createMcqResponse(q1.getId());
        when(questionRepository.findAllByBankId(eq(bankId), any(Pageable.class))).thenReturn(page);
        when(questionMapper.toResponse(any(Question.class))).thenReturn(resp);

        PageResponse<QuestionResponse> result = questionService.listQuestions(bankId, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listQuestions_shouldReturnByBankIdAndType() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID bankId = UUID.randomUUID();
        McqQuestion q1 = createMcqQuestion();
        q1.setId(UUID.randomUUID());
        Page<Question> page = new PageImpl<>(List.of(q1));
        McqQuestionResponse resp = createMcqResponse(q1.getId());
        when(questionRepository.findAllByBankIdAndType(eq(bankId), eq(QuestionType.MCQ), any(Pageable.class))).thenReturn(page);
        when(questionMapper.toResponse(any(Question.class))).thenReturn(resp);

        PageResponse<QuestionResponse> result = questionService.listQuestions(bankId, QuestionType.MCQ, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listQuestions_shouldReturnAll_whenNoFilter() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        McqQuestion q1 = createMcqQuestion();
        q1.setId(UUID.randomUUID());
        Page<Question> page = new PageImpl<>(List.of(q1));
        McqQuestionResponse resp = createMcqResponse(q1.getId());
        when(questionRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(questionMapper.toResponse(any(Question.class))).thenReturn(resp);

        PageResponse<QuestionResponse> result = questionService.listQuestions(null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listQuestions_shouldReturnByQuestionType() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        Question question = createMcqQuestion();
        question.setId(UUID.randomUUID());
        Page<Question> page = new PageImpl<>(List.of(question));
        when(questionRepository.findAllByQuestionType(QuestionType.MCQ, Pageable.unpaged()))
                .thenReturn(page);
        when(questionMapper.toResponse(question)).thenReturn(new McqQuestionResponse());

        PageResponse<QuestionResponse> result = questionService.listQuestions(null, QuestionType.MCQ, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    // --- makeQuestion ---

    @Test
    void makeQuestion_shouldReturnQuestion_whenSupportedType() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        McqQuestion mcqQuestion = createMcqQuestion();
        mcqQuestion.setId(UUID.randomUUID());
        when(questionHandler.create(any())).thenReturn(mcqQuestion);

        Question result = questionService.makeQuestion(createMcqRequest());

        assertThat(result).isNotNull();
    }

    // --- listQuestionsInAssessment ---

    @Test
    void listQuestionsInAssessment_shouldReturnList() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        questionService = createService();
        UUID assessmentId = UUID.randomUUID();
        McqQuestion question = createMcqQuestion();
        question.setId(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(Assessment.builder().id(assessmentId).build())
                .question(question)
                .orderIndex(0)
                .point(BigDecimal.TEN)
                .build();
        McqQuestionResponse response = createMcqResponse(question.getId());

        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(aq));
        when(questionMapper.toResponse(any(Question.class))).thenReturn(response);

        List<QuestionResponse> result = questionService.listQuestionsInAssessment(assessmentId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOrderIndex()).isEqualTo(0);
    }

    // --- helper methods ---

    private McqQuestionRequest createMcqRequest() {
        McqQuestionRequest req = new McqQuestionRequest();
        req.setQuestionType(QuestionType.MCQ);
        req.setContent("What is 2+2?");
        req.setDifficultLevel(DifficultLevel.EASY);
        req.setRequired(true);
        return req;
    }

    private EssayQuestionRequest createEssayRequest() {
        EssayQuestionRequest req = new EssayQuestionRequest();
        req.setQuestionType(QuestionType.ESSAY);
        req.setContent("Explain polymorphism");
        req.setDifficultLevel(DifficultLevel.EASY);
        req.setRequired(false);
        return req;
    }

    private McqQuestion createMcqQuestion() {
        McqQuestion q = new McqQuestion();
        q.setQuestionType(QuestionType.MCQ);
        q.setContent("What is 2+2?");
        q.setAllowMultiAnswer(false);
        q.setAnswerOptions(new ArrayList<>());
        return q;
    }

    private EssayQuestion createEssayQuestion() {
        EssayQuestion q = new EssayQuestion();
        q.setQuestionType(QuestionType.ESSAY);
        q.setContent("Explain polymorphism");
        q.setAcceptedFileTypes(new ArrayList<>());
        return q;
    }

    private McqQuestionResponse createMcqResponse(UUID id) {
        McqQuestionResponse response = new McqQuestionResponse();
        response.setId(id);
        response.setQuestionType(QuestionType.MCQ);
        response.setContent("What is 2+2?");
        return response;
    }
}
