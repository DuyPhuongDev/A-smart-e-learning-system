package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.client.CourseManagementInternalClient;
import com.hcmut.lms.assessment.client.dto.ClassGradingWeightDto;
import com.hcmut.lms.assessment.domain.entity.assessment.*;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.request.assessment.UpdateWeightRequest;
import com.hcmut.lms.assessment.dto.request.question.McqQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.ReorderRequest;
import com.hcmut.lms.assessment.dto.response.*;
import com.hcmut.lms.assessment.event.AssessmentEventPublisher;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.mapper.AssessmentMapper;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.AssessmentQuestionRepository;
import com.hcmut.lms.assessment.repository.AssessmentRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.QuestionService;
import com.hcmut.lms.common.dto.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceImplTest {

    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AssessmentQuestionRepository assessmentQuestionRepository;
    @Mock
    private AssessmentMapper assessmentMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private AssessmentEventPublisher assessmentEventPublisher;
    @Mock
    private CourseManagementInternalClient courseManagementInternalClient;
    @Mock
    private QuestionService questionService;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;

    // --- createAssessment ---

    @Test
    void createAssessment_shouldReturnResponse_whenValidRequest() {
        AssessmentRequest request = createRequest("Test", AssessmentType.ASSIGNMENT, UUID.randomUUID());
        Assessment entity = createEntity("Test", AssessmentType.ASSIGNMENT);
        Assessment saved = createEntityWithId(UUID.randomUUID(), "Test", AssessmentType.ASSIGNMENT);
        AssessmentResponse expected = createResponse(saved.getId(), "Test");

        when(assessmentMapper.toEntity(request)).thenReturn(entity);
        when(assessmentRepository.save(entity)).thenReturn(saved);
        when(assessmentMapper.toResponse(saved)).thenReturn(expected);

        AssessmentResponse result = assessmentService.createAssessment(request);

        assertThat(result).isEqualTo(expected);
        assertThat(entity.getAssessmentStatus()).isEqualTo(AssessmentStatus.DRAFT);
        assertThat(entity.getWeight()).isEqualTo(BigDecimal.ZERO);
    }

    // --- getGradesByClass ---

    @Test
    void getGradesByClass_shouldReturnList_whenAssessmentsExist() {
        UUID classId = UUID.randomUUID();
        Assessment a1 = createEntityWithId(UUID.randomUUID(), "A1", AssessmentType.ASSIGNMENT);
        a1.setWeight(BigDecimal.valueOf(0.3));
        Assessment a2 = createEntityWithId(UUID.randomUUID(), "A2", AssessmentType.MIDTERM);
        a2.setWeight(BigDecimal.valueOf(0.7));

        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId))
                .thenReturn(List.of(a1, a2));

        List<AssessmentGrade> result = assessmentService.getGradesByClass(classId);

        assertThat(result).hasSize(2);
    }

    @Test
    void getGradesByClass_shouldReturnEmptyList_whenNoAssessments() {
        UUID classId = UUID.randomUUID();
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId)).thenReturn(List.of());

        List<AssessmentGrade> result = assessmentService.getGradesByClass(classId);

        assertThat(result).isEmpty();
    }

    // --- getAssessmentsByClassIds ---

    @Test
    void getAssessmentsByClassIds_shouldReturnList_whenIdsProvided() {
        UUID classId = UUID.randomUUID();
        Assessment a1 = createEntityWithId(UUID.randomUUID(), "A1", AssessmentType.ASSIGNMENT);
        AssessmentResponse r1 = createResponse(a1.getId(), "A1");
        when(assessmentRepository.findByClassIdInOrderByCloseTimeAsc(List.of(classId)))
                .thenReturn(List.of(a1));
        when(assessmentMapper.toResponse(a1)).thenReturn(r1);

        List<AssessmentResponse> result = assessmentService.getAssessmentsByClassIds(List.of(classId));

        assertThat(result).hasSize(1);
    }

    @Test
    void getAssessmentsByClassIds_shouldReturnEmptyList_whenNull() {
        List<AssessmentResponse> result = assessmentService.getAssessmentsByClassIds(null);
        assertThat(result).isEmpty();
    }

    @Test
    void getAssessmentsByClassIds_shouldReturnEmptyList_whenEmpty() {
        List<AssessmentResponse> result = assessmentService.getAssessmentsByClassIds(List.of());
        assertThat(result).isEmpty();
    }

    // --- getPendingAssessmentCounts ---

    @Test
    void getPendingAssessmentCounts_shouldReturnList() {
        UUID classId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        List<Object[]> rows = java.util.Collections.singletonList(new Object[]{classId, 2, 5});
        when(assessmentRepository.countSubmittedAndPendingByClassIds(
                List.of(classId), studentId, AssessmentStatus.PUBLISHED, AssessmentSubmissionStatus.SUBMITTED))
                .thenReturn(rows);

        List<PendingAssessmentCountResponse> result = assessmentService.getPendingAssessmentCounts(List.of(classId), studentId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getSubmittedCount()).isEqualTo(2);
        assertThat(result.getFirst().getCount()).isEqualTo(5);
    }

    @Test
    void getPendingAssessmentCounts_shouldReturnEmptyList_whenNull() {
        List<PendingAssessmentCountResponse> result = assessmentService.getPendingAssessmentCounts(null, UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    // --- getAssessment ---

    @Test
    void getAssessment_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Assessment entity = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        AssessmentResponse expected = createResponse(id, "Test");

        when(assessmentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(assessmentMapper.toResponse(entity)).thenReturn(expected);

        AssessmentResponse result = assessmentService.getAssessment(id);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAssessment_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(assessmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.getAssessment(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Assessment");
    }

    // --- updateAssessment ---

    @Test
    void updateAssessment_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        AssessmentRequest request = createRequest("Updated", AssessmentType.ASSIGNMENT, UUID.randomUUID());
        Assessment entity = createEntityWithId(id, "Old", AssessmentType.ASSIGNMENT);
        Assessment saved = createEntityWithId(id, "Updated", AssessmentType.ASSIGNMENT);
        AssessmentResponse expected = createResponse(id, "Updated");

        when(assessmentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(assessmentRepository.save(entity)).thenReturn(saved);
        when(assessmentMapper.toResponse(saved)).thenReturn(expected);

        AssessmentResponse result = assessmentService.updateAssessment(id, request);

        assertThat(result).isEqualTo(expected);
        verify(assessmentMapper).updateEntity(request, entity);
    }

    // --- deleteAssessment ---

    @Test
    void deleteAssessment_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        Assessment entity = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        when(assessmentRepository.findById(id)).thenReturn(Optional.of(entity));

        assessmentService.deleteAssessment(id);

        verify(assessmentRepository).deleteById(id);
    }

    @Test
    void deleteAssessment_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(assessmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.deleteAssessment(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Assessment");
    }

    // --- listByClass ---

    @Test
    void listByClass_shouldReturnPage() {
        UUID classId = UUID.randomUUID();
        Assessment a1 = createEntityWithId(UUID.randomUUID(), "A1", AssessmentType.ASSIGNMENT);
        AssessmentResponse r1 = createResponse(a1.getId(), "A1");
        Page<Assessment> page = new PageImpl<>(List.of(a1));
        when(assessmentRepository.findAllByClassId(eq(classId), any(Pageable.class))).thenReturn(page);
        when(assessmentMapper.toResponse(a1)).thenReturn(r1);

        PageResponse<AssessmentResponse> result = assessmentService.listByClass(classId, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    // --- addQuestion ---

    @Test
    void addQuestion_shouldAddQuestionsToAssessment() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT);
        assessment.setAssessmentQuestions(new HashSet<>());
        AddQuestionRequest addReq = AddQuestionRequest.builder()
                .questionId(questionId).orderIndex(0).build();
        Question question = createMcqQuestion(questionId);

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(questionRepository.findAllById(List.of(questionId))).thenReturn(List.of(question));

        assessmentService.addQuestion(assessmentId, List.of(addReq));

        verify(assessmentRepository).save(assessment);
        assertThat(assessment.getAssessmentQuestions()).hasSize(1);
    }

    @Test
    void addQuestion_shouldThrowException_whenQuestionNotFound() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT);
        AddQuestionRequest addReq = AddQuestionRequest.builder()
                .questionId(questionId).orderIndex(0).build();

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(questionRepository.findAllById(List.of(questionId))).thenReturn(List.of());

        assertThatThrownBy(() -> assessmentService.addQuestion(assessmentId, List.of(addReq)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Question");
    }

    // --- removeQuestion ---

    @Test
    void removeQuestion_shouldRemoveQuestionFromAssessment() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        assessmentService.removeQuestion(assessmentId, questionId);

        verify(assessmentQuestionRepository).deleteByAssessmentIdAndQuestionId(assessmentId, questionId);
    }

    // --- getQuestions ---

    @Test
    void getQuestions_shouldReturnList_whenQuestionsExist() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT);
        McqQuestion question = (McqQuestion) createMcqQuestion(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).point(BigDecimal.TEN).orderIndex(0).build();
        McqQuestionResponse response = new McqQuestionResponse();
        response.setId(question.getId());
        response.setContent("Q1");

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(aq));
        when(questionMapper.toResponse(any(Question.class))).thenReturn(response);

        List<QuestionResponse> result = assessmentService.getQuestions(assessmentId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPoint()).isEqualTo(BigDecimal.TEN);
    }

    // --- changeStatus ---

    @Test
    void changeStatus_shouldPublish_whenPublished() {
        UUID id = UUID.randomUUID();
        Assessment entity = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        Assessment saved = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        saved.setAssessmentStatus(AssessmentStatus.PUBLISHED);

        when(assessmentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(assessmentRepository.save(entity)).thenReturn(saved);

        assessmentService.changeStatus(id, AssessmentStatus.PUBLISHED);

        verify(assessmentEventPublisher).publishAssignmentCreated(saved);
        assertThat(entity.getAssessmentStatus()).isEqualTo(AssessmentStatus.PUBLISHED);
    }

    @Test
    void changeStatus_shouldNotPublish_whenNotPublished() {
        UUID id = UUID.randomUUID();
        Assessment entity = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        Assessment saved = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        saved.setAssessmentStatus(AssessmentStatus.DRAFT);

        when(assessmentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(assessmentRepository.save(entity)).thenReturn(saved);

        assessmentService.changeStatus(id, AssessmentStatus.DRAFT);

        verify(assessmentEventPublisher, never()).publishAssignmentCreated(any());
    }

    // --- updateGrade ---

    @Test
    void updateGrade_shouldReturnAssessmentGrade() {
        UUID id = UUID.randomUUID();
        Assessment entity = createEntityWithId(id, "Test", AssessmentType.ASSIGNMENT);
        UpdateWeightRequest request = new UpdateWeightRequest();
        request.setWeight(BigDecimal.valueOf(0.5));

        when(assessmentRepository.findById(id)).thenReturn(Optional.of(entity));

        AssessmentGrade result = assessmentService.updateGrade(id, request);

        assertThat(entity.getWeight()).isEqualTo(BigDecimal.valueOf(0.5));
        assertThat(result).isNotNull();
    }

    // --- getGradingBreakdownForClass ---

    @Test
    void getGradingBreakdownForClass_shouldReturnBreakdown() {
        UUID classId = UUID.randomUUID();
        Assessment a1 = createEntityWithId(UUID.randomUUID(), "HW1", AssessmentType.ASSIGNMENT);
        a1.setWeight(BigDecimal.valueOf(0.1));

        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId))
                .thenReturn(List.of(a1));
        when(courseManagementInternalClient.getGradingWeights(classId))
                .thenReturn(List.of());

        List<GradingBreakdownResponse> result = assessmentService.getGradingBreakdownForClass(classId);

        assertThat(result).isNotNull();
    }

    @Test
    void getGradingBreakdownForClass_shouldHandleFeignException() {
        UUID classId = UUID.randomUUID();
        Assessment a1 = createEntityWithId(UUID.randomUUID(), "HW1", AssessmentType.ASSIGNMENT);

        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId))
                .thenReturn(List.of(a1));
        when(courseManagementInternalClient.getGradingWeights(classId))
                .thenThrow(new RuntimeException("Service unavailable"));

        List<GradingBreakdownResponse> result = assessmentService.getGradingBreakdownForClass(classId);

        assertThat(result).isNotNull();
    }

    @Test
    void getGradingBreakdownForClass_shouldIncludeWeightedComponents() {
        UUID classId = UUID.randomUUID();
        ClassGradingWeightDto weightDto = new ClassGradingWeightDto();
        weightDto.setGradingType("ASSIGNMENT");
        weightDto.setWeight(0.3f);

        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId))
                .thenReturn(List.of());
        when(courseManagementInternalClient.getGradingWeights(classId))
                .thenReturn(List.of(weightDto));

        List<GradingBreakdownResponse> result = assessmentService.getGradingBreakdownForClass(classId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getGradingType()).isEqualTo("ASSIGNMENT");
    }

    @Test
    void getGradingBreakdownForClass_shouldMergeWeightsAndAssessments() {
        UUID classId = UUID.randomUUID();
        Assessment a1 = createEntityWithId(UUID.randomUUID(), "HW1", AssessmentType.ASSIGNMENT);

        ClassGradingWeightDto weightDto = new ClassGradingWeightDto();
        weightDto.setGradingType("ASSIGNMENT");
        weightDto.setWeight(0.3f);

        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId))
                .thenReturn(List.of(a1));
        when(courseManagementInternalClient.getGradingWeights(classId))
                .thenReturn(List.of(weightDto));

        List<GradingBreakdownResponse> result = assessmentService.getGradingBreakdownForClass(classId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAssessments()).hasSize(1);
    }

    // --- createQuestionsForAssessment ---

    @Test
    void createQuestionsForAssessment_shouldReturnResponse_withProvidedOrderIndex() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT);
        assessment.setAssessmentQuestions(new HashSet<>());
        McqQuestionRequest questionReq = new McqQuestionRequest();
        questionReq.setQuestionType(QuestionType.MCQ);
        AssessmentQuestionRequest request = AssessmentQuestionRequest.builder()
                .question(questionReq).point(BigDecimal.TEN).orderIndex(2).build();
        Question question = createMcqQuestion(UUID.randomUUID());
        Question savedQuestion = createMcqQuestion(question.getId());
        McqQuestionResponse response = new McqQuestionResponse();
        response.setId(question.getId());
        response.setContent("Q");

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(questionService.makeQuestion(questionReq)).thenReturn(question);
        when(questionRepository.save(question)).thenReturn(savedQuestion);
        when(questionMapper.toResponse(savedQuestion)).thenReturn(response);

        QuestionResponse result = assessmentService.createQuestionsForAssessment(assessmentId, request);

        assertThat(result).isNotNull();
        verify(assessmentRepository).save(assessment);
    }

    @Test
    void createQuestionsForAssessment_shouldAutoIndex_whenOrderIndexNegative() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT);
        assessment.setAssessmentQuestions(new HashSet<>());
        McqQuestionRequest questionReq = new McqQuestionRequest();
        questionReq.setQuestionType(QuestionType.MCQ);
        AssessmentQuestionRequest request = AssessmentQuestionRequest.builder()
                .question(questionReq).point(BigDecimal.TEN).orderIndex(-1).build();
        Question question = createMcqQuestion(UUID.randomUUID());
        Question savedQuestion = createMcqQuestion(question.getId());
        McqQuestionResponse response = new McqQuestionResponse();
        response.setId(question.getId());

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(questionService.makeQuestion(questionReq)).thenReturn(question);
        when(questionRepository.save(question)).thenReturn(savedQuestion);
        when(assessmentQuestionRepository.countByAssessment_Id(assessmentId)).thenReturn(3);
        when(questionMapper.toResponse(savedQuestion)).thenReturn(response);

        QuestionResponse result = assessmentService.createQuestionsForAssessment(assessmentId, request);

        assertThat(result).isNotNull();
    }

    // --- updateQuestionsForAssessment ---

    @Test
    void updateQuestionsForAssessment_shouldUpdateQuestionAndConfig() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        McqQuestionRequest questionReq = new McqQuestionRequest();
        questionReq.setQuestionType(QuestionType.MCQ);
        AssessmentQuestionRequest request = AssessmentQuestionRequest.builder()
                .question(questionReq).point(BigDecimal.TEN).orderIndex(1).build();
        Question updatedQuestion = createMcqQuestion(questionId);
        McqQuestionResponse response = new McqQuestionResponse();
        response.setId(questionId);
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(createEntityWithId(assessmentId, "Test", AssessmentType.ASSIGNMENT))
                .question(updatedQuestion).point(BigDecimal.ONE).orderIndex(0).build();

        when(questionService.updateQuestion(questionId, questionReq)).thenReturn(updatedQuestion);
        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(aq));
        when(questionMapper.toResponse(updatedQuestion)).thenReturn(response);

        QuestionResponse result = assessmentService.updateQuestionsForAssessment(assessmentId, questionId, request);

        assertThat(result).isNotNull();
        verify(assessmentQuestionRepository).save(aq);
    }

    @Test
    void updateQuestionsForAssessment_shouldThrowException_whenAssessmentQuestionNotFound() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        McqQuestionRequest questionReq = new McqQuestionRequest();
        questionReq.setQuestionType(QuestionType.MCQ);
        AssessmentQuestionRequest request = AssessmentQuestionRequest.builder()
                .question(questionReq).point(BigDecimal.TEN).orderIndex(1).build();
        Question updatedQuestion = createMcqQuestion(questionId);

        when(questionService.updateQuestion(questionId, questionReq)).thenReturn(updatedQuestion);
        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.updateQuestionsForAssessment(assessmentId, questionId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- reorderQuestionsInAssessment ---

    @Test
    void reorderQuestionsInAssessment_shouldReorder_whenMovingUp() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        ReorderRequest request = new ReorderRequest();
        request.setNewOrderIndex(0);
        AssessmentQuestion current = createAssessmentQuestion(UUID.randomUUID(), questionId, 2);
        AssessmentQuestion q0 = createAssessmentQuestion(UUID.randomUUID(), UUID.randomUUID(), 0);
        AssessmentQuestion q1 = createAssessmentQuestion(UUID.randomUUID(), UUID.randomUUID(), 1);

        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(current));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(new ArrayList<>(List.of(q0, q1, current)));

        assessmentService.reorderQuestionsInAssessment(assessmentId, questionId, request);

        verify(assessmentQuestionRepository).saveAndFlush(current);
        verify(assessmentQuestionRepository).saveAll(anyList());
        verify(assessmentQuestionRepository).save(current);
    }

    @Test
    void reorderQuestionsInAssessment_shouldReorder_whenMovingDown() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        ReorderRequest request = new ReorderRequest();
        request.setNewOrderIndex(2);
        AssessmentQuestion current = createAssessmentQuestion(UUID.randomUUID(), questionId, 0);
        AssessmentQuestion q1 = createAssessmentQuestion(UUID.randomUUID(), UUID.randomUUID(), 1);
        AssessmentQuestion q2 = createAssessmentQuestion(UUID.randomUUID(), UUID.randomUUID(), 2);

        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(current));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(new ArrayList<>(List.of(current, q1, q2)));

        assessmentService.reorderQuestionsInAssessment(assessmentId, questionId, request);

        verify(assessmentQuestionRepository).saveAndFlush(current);
        verify(assessmentQuestionRepository).saveAll(anyList());
        verify(assessmentQuestionRepository).save(current);
    }

    @Test
    void reorderQuestionsInAssessment_shouldDoNothing_whenSameIndex() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        ReorderRequest request = new ReorderRequest();
        request.setNewOrderIndex(1);
        AssessmentQuestion current = createAssessmentQuestion(UUID.randomUUID(), questionId, 1);
        AssessmentQuestion q0 = createAssessmentQuestion(UUID.randomUUID(), UUID.randomUUID(), 0);

        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(current));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(q0, current));

        assessmentService.reorderQuestionsInAssessment(assessmentId, questionId, request);

        verify(assessmentQuestionRepository, never()).saveAndFlush(any());
    }

    @Test
    void reorderQuestionsInAssessment_shouldThrowException_whenIndexOutOfRange() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        ReorderRequest request = new ReorderRequest();
        request.setNewOrderIndex(5);
        AssessmentQuestion current = createAssessmentQuestion(UUID.randomUUID(), questionId, 0);

        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(current));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(current));

        assertThatThrownBy(() -> assessmentService.reorderQuestionsInAssessment(assessmentId, questionId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("out of range");
    }

    @Test
    void reorderQuestionsInAssessment_shouldThrowException_whenNoQuestions() {
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        ReorderRequest request = new ReorderRequest();
        request.setNewOrderIndex(0);

        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.reorderQuestionsInAssessment(assessmentId, questionId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- helper methods ---

    private AssessmentRequest createRequest(String title, AssessmentType type, UUID classId) {
        AssessmentRequest req = new AssessmentRequest();
        req.setTitle(title);
        req.setAssessmentType(type);
        req.setClassId(classId);
        return req;
    }

    private Assessment createEntity(String title, AssessmentType type) {
        Assessment a = new Assessment();
        a.setTitle(title);
        a.setAssessmentType(type);
        a.setAssessmentQuestions(new HashSet<>());
        return a;
    }

    private Assessment createEntityWithId(UUID id, String title, AssessmentType type) {
        Assessment a = createEntity(title, type);
        a.setId(id);
        a.setWeight(BigDecimal.ZERO);
        return a;
    }

    private AssessmentResponse createResponse(UUID id, String title) {
        return AssessmentResponse.builder()
                .id(id)
                .title(title)
                .build();
    }

    private Question createMcqQuestion(UUID id) {
        McqQuestion q = new McqQuestion();
        q.setId(id);
        q.setQuestionType(com.hcmut.lms.assessment.domain.entity.question.QuestionType.MCQ);
        q.setContent("Sample question");
        return q;
    }

    private AssessmentQuestion createAssessmentQuestion(UUID id, UUID questionId, int orderIndex) {
        Question question = createMcqQuestion(questionId);
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .question(question)
                .orderIndex(orderIndex)
                .point(BigDecimal.TEN)
                .build();
        aq.setId(id);
        return aq;
    }
}
