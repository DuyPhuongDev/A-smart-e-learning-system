package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.client.LearningInternalEnrollmentClient;
import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.assessment.*;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.submission.*;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.dto.response.student.*;
import com.hcmut.lms.assessment.event.AssessmentEventPublisher;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.CodeJudgeUnavailableException;
import com.hcmut.lms.assessment.exception.ForbiddenException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.handler.dto.GradingStatus;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;
import com.hcmut.lms.assessment.mapper.AssessmentMapper;
import com.hcmut.lms.assessment.repository.*;
import com.hcmut.lms.assessment.dto.request.student.SaveAnswerRequest;
import com.hcmut.lms.assessment.dto.request.student.SaveCodingAnswerRequest;
import com.hcmut.lms.assessment.dto.request.student.SaveEssayAnswerRequest;
import com.hcmut.lms.assessment.dto.request.student.SaveMcqAnswerRequest;
import com.hcmut.lms.assessment.service.AssessmentExecutionService;
import com.hcmut.lms.assessment.service.judge.CppJudgeService;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentAssessmentServiceImplTest {

    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private AssessmentQuestionRepository assessmentQuestionRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AssessmentSubmissionRepository assessmentSubmissionRepository;
    @Mock
    private QuestionSubmissionRepository questionSubmissionRepository;
    @Mock
    private McqSubmissionRepository mcqSubmissionRepository;
    @Mock
    private EssaySubmissionRepository essaySubmissionRepository;
    @Mock
    private CodingSubmissionRepository codingSubmissionRepository;
    @Mock
    private SubmissionTestCaseResultRepository submissionTestCaseResultRepository;
    @Mock
    private LearningInternalEnrollmentClient learningInternalEnrollmentClient;
    @Mock
    private AssessmentExecutionService assessmentExecutionService;
    @Mock
    private CppJudgeService cppJudgeService;
    @Mock
    private AssessmentEventPublisher assessmentEventPublisher;
    @Mock
    private AssessmentMapper assessmentMapper;

    @InjectMocks
    private StudentAssessmentServiceImpl studentAssessmentService;

    private static final UUID STUDENT_ID = UUID.randomUUID();
    private static final UUID CLASS_ID = UUID.randomUUID();

    // --- listCourseAssessments ---

    @Test
    void listCourseAssessments_shouldReturnPage_whenEnrolled() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        Page<Assessment> page = new PageImpl<>(List.of(assessment));
        when(assessmentRepository.findAllByClassId(eq(CLASS_ID), any(PageRequest.class))).thenReturn(page);
        when(assessmentMapper.toResponse(assessment)).thenReturn(AssessmentResponse.builder().id(assessment.getId()).build());
        when(assessmentSubmissionRepository.findAllByStudentIdAndAssessment_IdIn(eq(STUDENT_ID), anyList()))
                .thenReturn(List.of());

        var result = studentAssessmentService.listCourseAssessments(CLASS_ID, STUDENT_ID, "Bearer token", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listCourseAssessments_shouldReturnInProgress_whenHasInProgressSubmission() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        Page<Assessment> page = new PageImpl<>(List.of(assessment));
        when(assessmentRepository.findAllByClassId(eq(CLASS_ID), any(PageRequest.class))).thenReturn(page);
        when(assessmentMapper.toResponse(assessment)).thenReturn(AssessmentResponse.builder().id(assessmentId).build());

        AssessmentSubmission inProgress = createSubmission(UUID.randomUUID(), assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        when(assessmentSubmissionRepository.findAllByStudentIdAndAssessment_IdIn(eq(STUDENT_ID), anyList()))
                .thenReturn(List.of(inProgress));
        when(questionSubmissionRepository.findAttemptIdsByAttemptIdsAndStatus(anyList(), any()))
                .thenReturn(List.of());

        var result = studentAssessmentService.listCourseAssessments(CLASS_ID, STUDENT_ID, "Bearer token", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listCourseAssessments_shouldReturnPendingReview_whenHasUnreviewedSubmission() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        UUID assessmentId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        Page<Assessment> page = new PageImpl<>(List.of(assessment));
        when(assessmentRepository.findAllByClassId(eq(CLASS_ID), any(PageRequest.class))).thenReturn(page);
        when(assessmentMapper.toResponse(assessment)).thenReturn(AssessmentResponse.builder().id(assessmentId).build());

        AssessmentSubmission submitted = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        submitted.setSubmitTime(Instant.now());
        when(assessmentSubmissionRepository.findAllByStudentIdAndAssessment_IdIn(eq(STUDENT_ID), anyList()))
                .thenReturn(List.of(submitted));
        when(questionSubmissionRepository.findAttemptIdsByAttemptIdsAndStatus(anyList(), any()))
                .thenReturn(List.of(attemptId));

        var result = studentAssessmentService.listCourseAssessments(CLASS_ID, STUDENT_ID, "Bearer token", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listCourseAssessments_shouldThrowException_whenNotEnrolled() {
        lenient().when(learningInternalEnrollmentClient.checkEnrollment("Bearer token", CLASS_ID)).thenReturn(false);

        assertThatThrownBy(() ->
                studentAssessmentService.listCourseAssessments(CLASS_ID, STUDENT_ID, "Bearer token", 0, 10))
                .isInstanceOf(ForbiddenException.class);
    }

    // --- startAttempt ---

    @Test
    void startAttempt_shouldReturnResponse_whenValidRequest() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentSubmissionRepository.findFirstByAssessment_IdAndStudentIdAndStatusOrderByCreatedAtDesc(
                assessmentId, STUDENT_ID, AssessmentSubmissionStatus.IN_PROGRESS)).thenReturn(Optional.empty());
        when(assessmentSubmissionRepository.findMaxAttemptNo(assessmentId, STUDENT_ID)).thenReturn(0);
        AssessmentSubmission created = createSubmission(UUID.randomUUID(), assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        created.setCreatedAt(Instant.now());
        when(assessmentSubmissionRepository.saveAndFlush(any(AssessmentSubmission.class))).thenReturn(created);

        StartAttemptResponse result = studentAssessmentService.startAttempt(assessmentId, STUDENT_ID, "Bearer token");

        assertThat(result.getAssessmentId()).isEqualTo(assessmentId);
        assertThat(result.getAttemptNo()).isEqualTo(1);
    }

    @Test
    void startAttempt_shouldThrowException_whenAssessmentNotPublished() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId, AssessmentStatus.DRAFT);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        assertThatThrownBy(() -> studentAssessmentService.startAttempt(assessmentId, STUDENT_ID, "Bearer token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not open");
    }

    @Test
    void startAttempt_shouldThrowException_whenAssessmentClosed() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        assessment.setCloseTime(Instant.now().minusSeconds(3600));
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        assertThatThrownBy(() -> studentAssessmentService.startAttempt(assessmentId, STUDENT_ID, "Bearer token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("closed");
    }

    @Test
    void startAttempt_shouldThrowException_whenMaxAttemptsReached() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        assessment.setMaxAttempts(1);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentSubmissionRepository.findFirstByAssessment_IdAndStudentIdAndStatusOrderByCreatedAtDesc(
                assessmentId, STUDENT_ID, AssessmentSubmissionStatus.IN_PROGRESS)).thenReturn(Optional.empty());
        when(assessmentSubmissionRepository.findMaxAttemptNo(assessmentId, STUDENT_ID)).thenReturn(1);

        assertThatThrownBy(() -> studentAssessmentService.startAttempt(assessmentId, STUDENT_ID, "Bearer token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Maximum attempt limit");
    }

    @Test
    void startAttempt_shouldResumeInProgressAttempt() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        assessment.setTimeLimit(0);
        AssessmentSubmission inProgress = createSubmission(UUID.randomUUID(), assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        inProgress.setCreatedAt(Instant.now());

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentSubmissionRepository.findFirstByAssessment_IdAndStudentIdAndStatusOrderByCreatedAtDesc(
                assessmentId, STUDENT_ID, AssessmentSubmissionStatus.IN_PROGRESS)).thenReturn(Optional.of(inProgress));

        StartAttemptResponse result = studentAssessmentService.startAttempt(assessmentId, STUDENT_ID, "Bearer token");

        assertThat(result.isResumed()).isTrue();
        assertThat(result.getAttemptId()).isEqualTo(inProgress.getId());
    }

    // --- getAttemptDetail ---

    @Test
    void getAttemptDetail_shouldReturnResponse_whenOwnedAttempt() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        attempt.setCreatedAt(Instant.now());
        McqQuestion question = createMcqQuestion(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of());

        AttemptDetailResponse result = studentAssessmentService.getAttemptDetail(attemptId, STUDENT_ID);

        assertThat(result.getAssessmentId()).isEqualTo(assessmentId);
        assertThat(result.getQuestions()).hasSize(1);
    }

    @Test
    void getAttemptDetail_shouldThrowException_whenNotOwner() {
        UUID attemptId = UUID.randomUUID();
        AssessmentSubmission attempt = createSubmission(attemptId, createPublishedAssessment(UUID.randomUUID()),
                UUID.randomUUID(), 1, AssessmentSubmissionStatus.IN_PROGRESS);

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> studentAssessmentService.getAttemptDetail(attemptId, STUDENT_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    // --- getMyAttempts ---

    @Test
    void getMyAttempts_shouldReturnList_whenAttemptsExist() {
        when(learningInternalEnrollmentClient.checkEnrollment(anyString(), any(UUID.class))).thenReturn(true);
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission a1 = createSubmission(UUID.randomUUID(), assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        a1.setSubmitTime(Instant.now());

        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentSubmissionRepository.findAllByAssessment_IdAndStudentIdOrderByAttemptNoDesc(assessmentId, STUDENT_ID))
                .thenReturn(List.of(a1));
        when(questionSubmissionRepository.findAttemptIdsByAttemptIdsAndStatus(anyList(), any()))
                .thenReturn(List.of());

        List<StudentAttemptSummaryResponse> result = studentAssessmentService.getMyAttempts(assessmentId, STUDENT_ID, "Bearer token");

        assertThat(result).hasSize(1);
    }

    // --- getAttemptResult ---

    @Test
    void getAttemptResult_shouldReturnResponse_whenSubmitted() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        attempt.setSubmitTime(Instant.now());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of());
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of());

        SubmitAttemptResponse result = studentAssessmentService.getAttemptResult(attemptId, STUDENT_ID);

        assertThat(result.getAttemptId()).isEqualTo(attemptId);
        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    @Test
    void getAttemptResult_shouldThrowException_whenNotSubmitted() {
        UUID attemptId = UUID.randomUUID();
        AssessmentSubmission attempt = createSubmission(attemptId, createPublishedAssessment(UUID.randomUUID()),
                STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> studentAssessmentService.getAttemptResult(attemptId, STUDENT_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not submitted");
    }

    // --- submitAttempt ---

    @Test
    void submitAttempt_shouldReturnResponse_whenAlreadySubmitted() {
        UUID attemptId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        attempt.setSubmitTime(Instant.now());

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessment.getId())).thenReturn(List.of());
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of());

        SubmitAttemptResponse result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    @Test
    void submitAttempt_shouldThrowException_whenNotOwner() {
        UUID attemptId = UUID.randomUUID();
        AssessmentSubmission attempt = createSubmission(attemptId, createPublishedAssessment(UUID.randomUUID()),
                UUID.randomUUID(), 1, AssessmentSubmissionStatus.IN_PROGRESS);

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> studentAssessmentService.submitAttempt(attemptId, STUDENT_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void submitAttempt_shouldGradeMcqQuestions() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        McqQuestion question = createMcqQuestion(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();
        McqSubmission submission = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).score(BigDecimal.TEN)
                .status(QuestionSubmissionStatus.CORRECT).build();

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(submission));
        GradingResponse grading = GradingResponse.builder()
                .questionId(question.getId()).earnedPoints(BigDecimal.TEN).maxPoints(BigDecimal.TEN)
                .status(GradingStatus.CORRECT).detail("Correct").build();
        when(assessmentExecutionService.submitAnswer(eq(question.getId()), any(), eq(BigDecimal.TEN)))
                .thenReturn(grading);

        SubmitAttemptResponse result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
        verify(assessmentEventPublisher).publishSubmissionGraded(attempt, "GRADED");
    }

    @Test
    void submitAttempt_shouldFallbackZero_whenGradingThrowsRuntimeException() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        McqQuestion question = createMcqQuestion(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();
        McqSubmission submission = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).score(null)
                .status(QuestionSubmissionStatus.PENDING).build();
        submission.setSelectedOptions(new ArrayList<>());
        submission.setSelectedCount(1);

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(submission));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(submission);
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);
        when(assessmentExecutionService.submitAnswer(eq(question.getId()), any(), eq(BigDecimal.TEN)))
                .thenThrow(new RuntimeException("Internal grading error"));

        SubmitAttemptResponse result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    // --- submitAttempt (extended) ---

    @Test
    void submitAttempt_shouldMarkEssayAsPendingReview() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(essayQ).orderIndex(0).point(BigDecimal.TEN).build();

        EssaySubmission essaySub = EssaySubmission.builder()
                .assessmentSubmission(attempt).question(essayQ).status(QuestionSubmissionStatus.PENDING)
                .answerText("This is my essay answer.")
                .build();

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(essaySub));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(essaySub);
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
        assertThat(result.getGradingStatus()).isEqualTo("PENDING_REVIEW");
        verify(assessmentEventPublisher, never()).publishSubmissionGraded(any(), anyString());
    }

    // --- saveAnswer ---

    @Test
    void saveAnswer_shouldThrowException_whenNullQuestionType() {
        UUID attemptId = UUID.randomUUID();
        SaveAnswerRequest request = new SaveMcqAnswerRequest();

        assertThatThrownBy(() -> studentAssessmentService.saveAnswer(attemptId, UUID.randomUUID(), STUDENT_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("questionType is required");
    }

    @Test
    void saveAnswer_shouldThrowException_whenAttemptAlreadySubmitted() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        AssessmentSubmission attempt = createSubmission(attemptId, createPublishedAssessment(UUID.randomUUID()),
                STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        SaveMcqAnswerRequest request = new SaveMcqAnswerRequest();
        request.setQuestionType(QuestionType.MCQ);

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already submitted");
    }

    @Test
    void saveAnswer_shouldThrowException_whenQuestionNotInAssessment() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        SaveMcqAnswerRequest request = new SaveMcqAnswerRequest();
        request.setQuestionType(QuestionType.MCQ);

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId))
                .thenReturn(false);

        assertThatThrownBy(() -> studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not belong to this assessment");
    }

    @Test
    void saveAnswer_shouldSaveMcqAnswer_whenValid() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        McqQuestion question = createMcqQuestion(questionId);
        question.setAnswerOptions(List.of(createAnswerOption(optionId, question, true)));

        SaveMcqAnswerRequest request = new SaveMcqAnswerRequest();
        request.setQuestionType(QuestionType.MCQ);
        request.setSelectedOptionIds(List.of(optionId));

        McqSubmission saved = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).build();
        saved.setSelectedCount(1);
        saved.setUpdatedAt(Instant.now());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId))
                .thenReturn(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(mcqSubmissionRepository.findByAssessmentSubmission_IdAndQuestion_Id(attemptId, questionId))
                .thenReturn(Optional.empty());
        when(mcqSubmissionRepository.save(any(McqSubmission.class))).thenReturn(saved);

        var result = studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request);

        assertThat(result).isNotNull();
        assertThat(result.getSavedAt()).isNotNull();
    }

    @Test
    void saveAnswer_shouldRemoveIncompatibleSubmission_whenExistingEssaySubmission() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        McqQuestion question = createMcqQuestion(questionId);
        question.setAnswerOptions(List.of(createAnswerOption(optionId, question, true)));

        SaveMcqAnswerRequest request = new SaveMcqAnswerRequest();
        request.setQuestionType(QuestionType.MCQ);
        request.setSelectedOptionIds(List.of(optionId));

        McqSubmission saved = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).build();
        saved.setSelectedCount(1);
        saved.setUpdatedAt(Instant.now());

        // Existing essay submission (incompatible type)
        EssaySubmission existing = EssaySubmission.builder()
                .assessmentSubmission(attempt).question(question).build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId))
                .thenReturn(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(questionSubmissionRepository.findByAssessmentSubmission_IdAndQuestion_Id(attemptId, questionId))
                .thenReturn(Optional.of(existing));
        when(mcqSubmissionRepository.findByAssessmentSubmission_IdAndQuestion_Id(attemptId, questionId))
                .thenReturn(Optional.empty());
        when(mcqSubmissionRepository.save(any(McqSubmission.class))).thenReturn(saved);

        var result = studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request);

        assertThat(result).isNotNull();
        verify(questionSubmissionRepository).delete(existing);
    }

    @Test
    void saveAnswer_shouldThrowException_whenLanguageNotSupported() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code");

        SaveCodingAnswerRequest request = new SaveCodingAnswerRequest();
        request.setQuestionType(QuestionType.CODING);
        request.setCode("print('hello')");
        request.setLanguage("brainfuck");

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId))
                .thenReturn(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(codingQ));
        when(cppJudgeService.isSupportedLanguage("brainfuck")).thenReturn(false);

        assertThatThrownBy(() -> studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unsupported language");
    }

    // --- private methods via ReflectionTestUtils ---

    @Test
    void isAssessmentOpenNow_shouldReturnTrue_whenPublishedAndInWindow() {
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());

        boolean result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "isAssessmentOpenNow", assessment);
        assertThat(result).isTrue();
    }

    @Test
    void isAssessmentOpenNow_shouldReturnFalse_whenDraft() {
        Assessment assessment = createAssessment(UUID.randomUUID(), AssessmentStatus.DRAFT);

        boolean result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "isAssessmentOpenNow", assessment);
        assertThat(result).isFalse();
    }

    @Test
    void isAssessmentOpenNow_shouldReturnFalse_whenNotYetStarted() {
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        assessment.setStartTime(Instant.now().plusSeconds(3600));

        boolean result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "isAssessmentOpenNow", assessment);
        assertThat(result).isFalse();
    }

    @Test
    void isAssessmentOpenNow_shouldReturnFalse_whenAlreadyClosed() {
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        assessment.setCloseTime(Instant.now().minusSeconds(3600));

        boolean result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "isAssessmentOpenNow", assessment);
        assertThat(result).isFalse();
    }

    @Test
    void calcBestScore_shouldReturnHighestScore_whenHighScoreRule() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(70));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calcBestScore", List.of(a1, a2), GradingRule.HIGH_SCORE);
        assertThat(result).isEqualTo(BigDecimal.valueOf(90));
    }

    @Test
    void calcBestScore_shouldReturnLastAttemptScore_whenLastAttemptRule() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(70));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calcBestScore", List.of(a1, a2), GradingRule.LAST_ATTEMPT);
        assertThat(result).isEqualTo(BigDecimal.valueOf(90));
    }

    @Test
    void calcBestScore_shouldReturnAvg_whenAvgScoreRule() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(100));

        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calcBestScore", List.of(a1, a2), GradingRule.AVG_SCORE);
        assertThat(result).isEqualTo(new BigDecimal("90.00"));
    }

    @Test
    void calcBestScore_shouldReturnFirstAttemptScore_whenFirstAttemptRule() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(70));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calcBestScore", List.of(a1, a2), GradingRule.FIRST_ATTEMPT);
        assertThat(result).isEqualTo(BigDecimal.valueOf(70));
    }

    @Test
    void calcBestScore_shouldReturnZero_whenNoSubmissions() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calcBestScore", List.of(), GradingRule.HIGH_SCORE);
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void hasEssayContent_shouldReturnTrue_whenTextPresent() {
        EssaySubmission submission = EssaySubmission.builder().answerText("Some text").build();
        boolean result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "hasEssayContent", submission);
        assertThat(result).isTrue();
    }

    // --- getAttemptDetail (extended) ---

    @Test
    void getAttemptDetail_shouldReturnMcqOptions_whenMcqQuestionWithSubmission() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        attempt.setCreatedAt(Instant.now());

        McqQuestion question = createMcqQuestion(questionId);
        AnswerOption option = new AnswerOption();
        option.setId(optionId);
        option.setQuestion(question);
        option.setCorrect(true);
        option.setContent("Answer");
        option.setOrderIndex(0);
        question.setAnswerOptions(List.of(option));
        question.setAllowMultiAnswer(false);

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        McqSubmission mcqSub = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).status(QuestionSubmissionStatus.CORRECT)
                .score(BigDecimal.TEN).build();
        mcqSub.setSelectedOptions(new ArrayList<>());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(mcqSub));

        var result = studentAssessmentService.getAttemptDetail(attemptId, STUDENT_ID);

        assertThat(result.getQuestions()).hasSize(1);
        assertThat(result.getQuestions().getFirst().getOptions()).hasSize(1);
    }

    @Test
    void getAttemptDetail_shouldReturnSelectedOptionIds_whenMcqSubmissionHasSelections() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        attempt.setCreatedAt(Instant.now());

        McqQuestion question = createMcqQuestion(questionId);
        AnswerOption option = new AnswerOption();
        option.setId(optionId);
        option.setQuestion(question);
        option.setCorrect(true);
        option.setContent("Answer");
        option.setOrderIndex(0);
        question.setAnswerOptions(List.of(option));

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        AnswerOptionMcqSubmission selected = AnswerOptionMcqSubmission.builder().option(option).build();
        McqSubmission mcqSub = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).status(QuestionSubmissionStatus.CORRECT)
                .score(BigDecimal.TEN).build();
        mcqSub.setSelectedOptions(new ArrayList<>(List.of(selected)));

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(mcqSub));

        var result = studentAssessmentService.getAttemptDetail(attemptId, STUDENT_ID);

        assertThat(result.getQuestions().getFirst().getSelectedOptionIds()).containsExactly(optionId);
    }

    // --- getAttemptResult (extended) ---

    @Test
    void getAttemptResult_shouldReturnMcqOptions_whenMcqQuestionSubmitted() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        attempt.setSubmitTime(Instant.now());

        McqQuestion question = createMcqQuestion(questionId);
        AnswerOption option = new AnswerOption();
        option.setId(optionId);
        option.setQuestion(question);
        option.setCorrect(true);
        option.setContent("Answer");
        option.setOrderIndex(0);
        question.setAnswerOptions(List.of(option));

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        McqSubmission mcqSub = McqSubmission.builder()
                .assessmentSubmission(attempt).question(question).status(QuestionSubmissionStatus.CORRECT)
                .score(BigDecimal.TEN).build();
        mcqSub.setSelectedOptions(new ArrayList<>());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(mcqSub));

        var result = studentAssessmentService.getAttemptResult(attemptId, STUDENT_ID);

        assertThat(result.getQuestionResults()).hasSize(1);
    }

    // --- private method tests ---

    @Test
    void hasEssayContent_shouldReturnFalse_whenEmpty() {
        EssaySubmission submission = EssaySubmission.builder().answerText(null).answerFileUrl(null).build();
        boolean result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "hasEssayContent", submission);
        assertThat(result).isFalse();
    }

    // --- saveAnswer with ESSAY ---

    @Test
    void saveAnswer_shouldSaveEssayAnswer_whenValid() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        SaveEssayAnswerRequest request = new SaveEssayAnswerRequest();
        request.setQuestionType(QuestionType.ESSAY);
        request.setTextContent("My answer");

        EssaySubmission saved = EssaySubmission.builder()
                .assessmentSubmission(attempt).question(essayQ).answerText("My answer").build();
        saved.setUpdatedAt(Instant.now());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId))
                .thenReturn(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(essayQ));
        when(essaySubmissionRepository.findByAssessmentSubmission_IdAndQuestion_Id(attemptId, questionId))
                .thenReturn(Optional.empty());
        when(essaySubmissionRepository.save(any(EssaySubmission.class))).thenReturn(saved);

        var result = studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request);

        assertThat(result).isNotNull();
        assertThat(result.getSavedAt()).isNotNull();
    }

    // --- saveAnswer with CODING ---

    @Test
    void saveAnswer_shouldSaveCodingAnswer_whenValid() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code");

        SaveCodingAnswerRequest request = new SaveCodingAnswerRequest();
        request.setQuestionType(QuestionType.CODING);
        request.setCode("print('hello')");
        request.setLanguage("python");

        CodingSubmission saved = CodingSubmission.builder()
                .assessmentSubmission(attempt).question(codingQ).inputCode("print('hello')").executionLanguage("python").build();
        saved.setUpdatedAt(Instant.now());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId))
                .thenReturn(true);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(codingQ));
        when(cppJudgeService.isSupportedLanguage("python")).thenReturn(true);
        when(codingSubmissionRepository.findByAssessmentSubmission_IdAndQuestion_Id(attemptId, questionId))
                .thenReturn(Optional.empty());
        when(codingSubmissionRepository.save(any(CodingSubmission.class))).thenReturn(saved);

        var result = studentAssessmentService.saveAnswer(attemptId, questionId, STUDENT_ID, request);

        assertThat(result).isNotNull();
    }

    // --- getAttemptDetail with CODING question ---

    @Test
    void getAttemptDetail_shouldReturnCodingFields_whenCodingQuestionWithSubmission() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        attempt.setCreatedAt(Instant.now());

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code");
        codingQ.setProblemDescription("Solve this");
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setLanguage("cpp");
        codingQ.setInitialCode("// start");

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(codingQ).orderIndex(0).point(BigDecimal.TEN).build();

        CodingSubmission codingSub = CodingSubmission.builder()
                .assessmentSubmission(attempt).question(codingQ).status(QuestionSubmissionStatus.PENDING)
                .inputCode("int main(){}").executionLanguage("cpp").build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(codingSub));

        var result = studentAssessmentService.getAttemptDetail(attemptId, STUDENT_ID);

        assertThat(result.getQuestions()).hasSize(1);
    }

    // --- getAttemptDetail with ESSAY question ---

    @Test
    void getAttemptDetail_shouldReturnEssayFields_whenEssayQuestionWithSubmission() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        attempt.setCreatedAt(Instant.now());

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setMaxFileSize(10);
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(essayQ).orderIndex(0).point(BigDecimal.TEN).build();

        EssaySubmission essaySub = EssaySubmission.builder()
                .assessmentSubmission(attempt).question(essayQ).status(QuestionSubmissionStatus.PENDING)
                .answerText("My essay").answerFileUrl("file.pdf").fileFormat("pdf")
                .numPages(2).wordCount(500).build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(essaySub));

        var result = studentAssessmentService.getAttemptDetail(attemptId, STUDENT_ID);

        assertThat(result.getQuestions()).hasSize(1);
    }

    // --- getAttemptResult with CODING submission ---

    @Test
    void getAttemptResult_shouldReturnCodingDetail_whenCodingSubmission() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        attempt.setSubmitTime(Instant.now());

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code");
        codingQ.setProblemDescription("Solve this");
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setLanguage("cpp");
        codingQ.setInitialCode("// start");

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(codingQ).orderIndex(0).point(BigDecimal.TEN).build();

        CodingSubmission codingSub = CodingSubmission.builder()
                .assessmentSubmission(attempt).question(codingQ).status(QuestionSubmissionStatus.CORRECT)
                .score(BigDecimal.TEN).inputCode("int main(){}").executionLanguage("cpp")
                .passedTestcases(3).totalTestcases(3).build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(codingSub));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(codingSub));
        when(cppJudgeService.isSupportedLanguage("cpp")).thenReturn(true);
        when(submissionTestCaseResultRepository.findAllByCodingSubmission_Id(codingSub.getId()))
                .thenReturn(List.of());

        var result = studentAssessmentService.getAttemptResult(attemptId, STUDENT_ID);

        assertThat(result.getQuestionResults()).hasSize(1);
    }

    // --- submitAttempt with unanswered coding question ---

    @Test
    void submitAttempt_shouldCreateUnansweredCoding_whenCodingQuestionNotAnswered() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code");
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>(List.of(new com.hcmut.lms.assessment.domain.entity.answer.TestCase())));

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(codingQ).orderIndex(0).point(BigDecimal.TEN).build();

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of());
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenAnswer(inv -> inv.getArgument(0));
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    // --- ensureStudentEnrolled via ReflectionTestUtils ---

    @Test
    void ensureStudentEnrolled_shouldAllow_whenFeignException500() {
        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(500);
        when(learningInternalEnrollmentClient.checkEnrollment("Bearer token", CLASS_ID)).thenThrow(ex);

        // should not throw
        ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "ensureStudentEnrolled", "Bearer token", CLASS_ID);
    }

    @Test
    void ensureStudentEnrolled_shouldAllow_whenRetryableException() {
        feign.RetryableException ex = mock(feign.RetryableException.class);
        when(learningInternalEnrollmentClient.checkEnrollment("Bearer token", CLASS_ID)).thenThrow(ex);

        // should not throw
        ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "ensureStudentEnrolled", "Bearer token", CLASS_ID);
    }

    // --- private method: trimDetailForStudent ---

    @Test
    void trimDetailForStudent_shouldReturnNull_whenNull() {
        String result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "trimDetailForStudent", (String) null);
        assertThat(result).isNull();
    }

    @Test
    void trimDetailForStudent_shouldTruncate_whenTooLong() {
        String longString = "a".repeat(700);
        String result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "trimDetailForStudent", longString);
        assertThat(result).endsWith("...(truncated)");
        assertThat(result.length()).isEqualTo(614);
    }

    // --- private method: mapStatus ---

    @Test
    void mapStatus_shouldReturnCorrect() {
        var result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "mapStatus", com.hcmut.lms.assessment.handler.dto.GradingStatus.CORRECT);
        assertThat(result).isEqualTo(QuestionSubmissionStatus.CORRECT);
    }

    @Test
    void mapStatus_shouldReturnPartial() {
        var result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "mapStatus", com.hcmut.lms.assessment.handler.dto.GradingStatus.PARTIAL);
        assertThat(result).isEqualTo(QuestionSubmissionStatus.PARTIAL);
    }

    @Test
    void mapStatus_shouldReturnIncorrect() {
        var result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "mapStatus", com.hcmut.lms.assessment.handler.dto.GradingStatus.INCORRECT);
        assertThat(result).isEqualTo(QuestionSubmissionStatus.INCORRECT);
    }

    // --- private method: computeAttemptExpiresAt ---

    @Test
    void computeAttemptExpiresAt_shouldReturnNull_whenTimeLimitZero() {
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        assessment.setTimeLimit(0);
        AssessmentSubmission attempt = createSubmission(UUID.randomUUID(), assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);
        attempt.setCreatedAt(Instant.now());

        Instant result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "computeAttemptExpiresAt", attempt, assessment);
        assertThat(result).isNull();
    }

    // --- private method: ensureAssessmentCanStart ---

    @Test
    void ensureAssessmentCanStart_shouldThrow_whenBeforeStart() {
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        assessment.setStartTime(Instant.now().plusSeconds(3600));

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "ensureAssessmentCanStart", assessment))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not started");
    }

    @Test
    void ensureAssessmentCanStart_shouldThrow_whenAfterClose() {
        Assessment assessment = createPublishedAssessment(UUID.randomUUID());
        assessment.setCloseTime(Instant.now().minusSeconds(3600));

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "ensureAssessmentCanStart", assessment))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("closed");
    }

    // --- submitAttempt with already submitted attempt ---

    @Test
    void submitAttempt_shouldReturnExistingResult_whenAlreadySubmitted() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.SUBMITTED);
        attempt.setSubmitTime(Instant.now());
        attempt.setScore(BigDecimal.TEN);
        attempt.setActualScore(BigDecimal.ONE);

        McqQuestion mcqQ = createMcqQuestion(questionId);
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(mcqQ).orderIndex(0).point(BigDecimal.TEN).build();

        McqSubmission mcqSub = McqSubmission.builder()
                .assessmentSubmission(attempt).question(mcqQ).status(QuestionSubmissionStatus.CORRECT)
                .score(BigDecimal.TEN).build();
        mcqSub.setSelectedOptions(new ArrayList<>());

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(mcqSub));

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    // --- submitAttempt with answered coding question ---

    @Test
    void submitAttempt_shouldGradeCodingSubmission_whenAnswered() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID testCaseId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        TestCase testCase = TestCase.builder()
                .id(testCaseId).input("1 2").expected("3").hidden(false).build();

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code");
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>(List.of(testCase)));

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(codingQ).orderIndex(0).point(BigDecimal.TEN).build();

        CodingSubmission codingSub = CodingSubmission.builder()
                .assessmentSubmission(attempt).question(codingQ).status(QuestionSubmissionStatus.PENDING)
                .inputCode("int main(){}").executionLanguage("cpp").build();
        codingSub.setId(UUID.randomUUID());

        var evaluation = com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation.builder()
                .passedCount(1).totalCount(1)
                .overallVerdict(com.hcmut.lms.assessment.service.judge.dto.JudgeVerdict.AC)
                .detail("All tests passed")
                .testCaseResults(List.of(
                        com.hcmut.lms.assessment.service.judge.dto.TestCaseJudgeResult.builder()
                                .testCaseId(testCaseId).pass(true)
                                .verdict(com.hcmut.lms.assessment.service.judge.dto.JudgeVerdict.AC)
                                .output("3").executionTimeMs(10).build()
                ))
                .build();

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(codingSub));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(codingSub);
        when(cppJudgeService.isSupportedLanguage("cpp")).thenReturn(true);
        when(cppJudgeService.evaluate(eq(codingQ), eq("int main(){}"), eq("cpp"), eq(false)))
                .thenReturn(evaluation);
        when(codingSubmissionRepository.save(any(CodingSubmission.class))).thenReturn(codingSub);
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
        assertThat(result.getGradingStatus()).isEqualTo("GRADED");
    }

    // --- private method: validateCodingQuestionConfiguration ---

    @Test
    void validateCodingQuestionConfiguration_shouldThrow_whenNoTestCases() {
        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>());

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "validateCodingQuestionConfiguration", codingQ))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("missing test cases");
    }

    @Test
    void validateCodingQuestionConfiguration_shouldThrow_whenInvalidTimeLimit() {
        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setExecutionTimeLimit(0);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>(List.of(TestCase.builder().build())));

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "validateCodingQuestionConfiguration", codingQ))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("executionTimeLimit");
    }

    @Test
    void validateCodingQuestionConfiguration_shouldThrow_whenInvalidMemoryLimit() {
        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(0);
        codingQ.setTestCases(new ArrayList<>(List.of(TestCase.builder().build())));

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "validateCodingQuestionConfiguration", codingQ))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("executionMemoryLimit");
    }

    // --- private method: calculateCodingEarnedPoints ---

    @Test
    void calculateCodingEarnedPoints_shouldReturnPartial_whenSomePass() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calculateCodingEarnedPoints", BigDecimal.TEN, 1, 2);
        assertThat(result).isEqualTo(new BigDecimal("5.000"));
    }

    @Test
    void calculateCodingEarnedPoints_shouldReturnZero_whenNoPass() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "calculateCodingEarnedPoints", BigDecimal.TEN, 0, 2);
        assertThat(result).isEqualTo(new BigDecimal("0.000"));
    }

    // --- private method: mapCodingGradingStatus ---

    @Test
    void mapCodingGradingStatus_shouldReturnCorrect_whenAllPass() {
        var result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "mapCodingGradingStatus", 3, 3);
        assertThat(result).isEqualTo(com.hcmut.lms.assessment.handler.dto.GradingStatus.CORRECT);
    }

    @Test
    void mapCodingGradingStatus_shouldReturnPartial_whenSomePass() {
        var result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "mapCodingGradingStatus", 2, 4);
        assertThat(result).isEqualTo(com.hcmut.lms.assessment.handler.dto.GradingStatus.PARTIAL);
    }

    @Test
    void mapCodingGradingStatus_shouldReturnIncorrect_whenNonePass() {
        var result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "mapCodingGradingStatus", 0, 3);
        assertThat(result).isEqualTo(com.hcmut.lms.assessment.handler.dto.GradingStatus.INCORRECT);
    }

    // --- submitAttempt with blank coding answer ---

    @Test
    void submitAttempt_shouldReturnZero_whenCodingCodeIsBlank() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        TestCase testCase = TestCase.builder().input("1").expected("2").hidden(false).build();
        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Code");
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>(List.of(testCase)));

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(codingQ).orderIndex(0).point(BigDecimal.TEN).build();

        CodingSubmission codingSub = CodingSubmission.builder()
                .assessmentSubmission(attempt).question(codingQ).status(QuestionSubmissionStatus.PENDING)
                .inputCode("").executionLanguage("cpp").build();
        codingSub.setId(UUID.randomUUID());

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(codingSub));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(codingSub);
        when(codingSubmissionRepository.save(any(CodingSubmission.class))).thenReturn(codingSub);
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    // --- submitAttempt with essay no content ---

    @Test
    void submitAttempt_shouldReturnZero_whenEssaySubmissionIsNotEssayType() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(essayQ).orderIndex(0).point(BigDecimal.TEN).build();

        // Plain QuestionSubmission (not EssaySubmission) → resolveEssaySubmission returns null
        QuestionSubmission plainSub = new QuestionSubmission();
        plainSub.setId(UUID.randomUUID());
        plainSub.setQuestion(essayQ);
        plainSub.setStatus(QuestionSubmissionStatus.PENDING);

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(plainSub));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(plainSub);
        when(essaySubmissionRepository.findById(plainSub.getId())).thenReturn(Optional.empty());
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    @Test
    void submitAttempt_shouldReturnZero_whenEssayHasNoContent() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createPublishedAssessment(assessmentId);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, STUDENT_ID, 1, AssessmentSubmissionStatus.IN_PROGRESS);

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(essayQ).orderIndex(0).point(BigDecimal.TEN).build();

        EssaySubmission essaySub = EssaySubmission.builder()
                .assessmentSubmission(attempt).question(essayQ).status(QuestionSubmissionStatus.PENDING)
                .answerText(null).answerFileUrl(null).build();

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(essaySub));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(essaySub);
        when(assessmentSubmissionRepository.save(any(AssessmentSubmission.class))).thenReturn(attempt);

        var result = studentAssessmentService.submitAttempt(attemptId, STUDENT_ID);

        assertThat(result.getStatus()).isEqualTo(AssessmentSubmissionStatus.SUBMITTED);
    }

    // --- loadStudentProfile (Teacher) via ReflectionTestUtils ---
    // Note: This test is for StudentAssessmentServiceImpl but testing enrollment edge cases

    @Test
    void ensureStudentEnrolled_shouldThrow_whenGenericException() {
        when(learningInternalEnrollmentClient.checkEnrollment("Bearer token", CLASS_ID))
                .thenThrow(new RuntimeException("Boom"));

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "ensureStudentEnrolled", "Bearer token", CLASS_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void resolveConcreteQuestion_shouldReturnNull_whenNull() {
        Object result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "resolveConcreteQuestion", (Question) null);
        assertThat(result).isNull();
    }

    // --- summarizeCodingSubmissionDetail via ReflectionTestUtils ---

    @Test
    void summarizeCodingSubmissionDetail_shouldReturnUnsupportedLanguage_whenLanguageNotSupported() {
        CodingSubmission codingSub = CodingSubmission.builder()
                .inputCode("code").executionLanguage("brainfuck").build();
        codingSub.setId(UUID.randomUUID());
        when(cppJudgeService.isSupportedLanguage("brainfuck")).thenReturn(false);

        String result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "summarizeCodingSubmissionDetail", codingSub);

        assertThat(result).contains("Unsupported language");
    }

    @Test
    void summarizeCodingSubmissionDetail_shouldReturnCompileError_whenCEWithDetail() {
        CodingSubmission codingSub = CodingSubmission.builder()
                .inputCode("bad code").executionLanguage("cpp")
                .passedTestcases(0).totalTestcases(3).build();
        codingSub.setId(UUID.randomUUID());
        when(cppJudgeService.isSupportedLanguage("cpp")).thenReturn(true);

        SubmissionTestCaseResult ceResult = SubmissionTestCaseResult.builder()
                .codingSubmission(codingSub).testCaseId(UUID.randomUUID())
                .pass(false).verdict("CE").detailError("syntax error: missing semicolon").build();
        when(submissionTestCaseResultRepository.findAllByCodingSubmission_Id(codingSub.getId()))
                .thenReturn(List.of(ceResult));

        String result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "summarizeCodingSubmissionDetail", codingSub);

        assertThat(result).isEqualTo("syntax error: missing semicolon");
    }

    @Test
    void summarizeCodingSubmissionDetail_shouldReturnCompileError_whenCEWithoutDetail() {
        CodingSubmission codingSub = CodingSubmission.builder()
                .inputCode("bad code").executionLanguage("cpp")
                .passedTestcases(0).totalTestcases(3).build();
        codingSub.setId(UUID.randomUUID());
        when(cppJudgeService.isSupportedLanguage("cpp")).thenReturn(true);

        SubmissionTestCaseResult ceResult = SubmissionTestCaseResult.builder()
                .codingSubmission(codingSub).testCaseId(UUID.randomUUID())
                .pass(false).verdict("CE").detailError("").build();
        when(submissionTestCaseResultRepository.findAllByCodingSubmission_Id(codingSub.getId()))
                .thenReturn(List.of(ceResult));

        String result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "summarizeCodingSubmissionDetail", codingSub);

        assertThat(result).isEqualTo("Compile error");
    }

    // --- gradeCodingSubmission via ReflectionTestUtils ---

    @Test
    void gradeCodingSubmission_shouldThrowBadRequest_whenUnsupportedLanguage() {
        CodingQuestion codingQ = new CodingQuestion();
        codingQ.setId(UUID.randomUUID());
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>(List.of(new TestCase())));

        CodingSubmission codingSub = CodingSubmission.builder()
                .inputCode("code").executionLanguage("brainfuck").build();
        codingSub.setId(UUID.randomUUID());
        when(cppJudgeService.isSupportedLanguage("brainfuck")).thenReturn(false);

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "gradeCodingSubmission", codingQ, codingSub, BigDecimal.TEN))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unsupported language");
    }

    @Test
    void gradeCodingSubmission_shouldThrowCodeJudgeUnavailable_whenRuntimeException() {
        CodingQuestion codingQ = new CodingQuestion();
        codingQ.setId(UUID.randomUUID());
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setTestCases(new ArrayList<>(List.of(new TestCase())));

        CodingSubmission codingSub = CodingSubmission.builder()
                .inputCode("code").executionLanguage("cpp").build();
        codingSub.setId(UUID.randomUUID());
        when(cppJudgeService.isSupportedLanguage("cpp")).thenReturn(true);
        when(cppJudgeService.evaluate(any(), any(), any(), anyBoolean()))
                .thenThrow(new RuntimeException("Boom"));

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "gradeCodingSubmission", codingQ, codingSub, BigDecimal.TEN))
                .isInstanceOf(CodeJudgeUnavailableException.class)
                .hasMessageContaining("unavailable");
    }

    // --- toSubmissionDto via ReflectionTestUtils ---

    @Test
    void toSubmissionDto_shouldReturnEssayDto_whenEssayQuestion() {
        EssayQuestion essayQ = new EssayQuestion();
        essayQ.setId(UUID.randomUUID());
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain...");

        EssaySubmission essaySub = EssaySubmission.builder()
                .answerText("My answer").answerFileUrl("http://file.url").build();
        essaySub.setId(UUID.randomUUID());

        SubmissionDto result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "toSubmissionDto", essayQ, essaySub, STUDENT_ID);

        assertThat(result).isNotNull();
    }

    @Test
    void toSubmissionDto_shouldReturnCodingDto_whenCodingQuestion() {
        CodingQuestion codingQ = new CodingQuestion();
        codingQ.setId(UUID.randomUUID());
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Write code...");

        CodingSubmission codingSub = CodingSubmission.builder()
                .inputCode("int main(){}").executionLanguage("cpp").build();
        codingSub.setId(UUID.randomUUID());

        SubmissionDto result = ReflectionTestUtils.invokeMethod(studentAssessmentService,
                "toSubmissionDto", codingQ, codingSub, STUDENT_ID);

        assertThat(result).isNotNull();
    }

    // --- helper methods ---

    private Assessment createAssessment(UUID id, AssessmentStatus status) {
        Assessment a = new Assessment();
        a.setId(id);
        a.setClassId(CLASS_ID);
        a.setTitle("Test Assessment");
        a.setAssessmentType(AssessmentType.ASSIGNMENT);
        a.setAssessmentStatus(status);
        a.setMaxAttempts(0);
        a.setTimeLimit(0);
        a.setGradingRule(GradingRule.LAST_ATTEMPT);
        a.setAssessmentQuestions(new HashSet<>());
        return a;
    }

    private Assessment createPublishedAssessment(UUID id) {
        Assessment a = createAssessment(id, AssessmentStatus.PUBLISHED);
        a.setStartTime(Instant.now().minusSeconds(3600));
        a.setCloseTime(Instant.now().plusSeconds(3600));
        return a;
    }

    private AssessmentSubmission createSubmission(UUID id, Assessment assessment, UUID studentId, int attemptNo, AssessmentSubmissionStatus status) {
        return AssessmentSubmission.builder()
                .id(id)
                .assessment(assessment)
                .studentId(studentId)
                .attemptNo(attemptNo)
                .status(status)
                .questionSubmissions(new ArrayList<>())
                .build();
    }

    private McqQuestion createMcqQuestion(UUID id) {
        McqQuestion q = new McqQuestion();
        q.setId(id);
        q.setQuestionType(QuestionType.MCQ);
        q.setContent("Sample");
        q.setAnswerOptions(new ArrayList<>());
        return q;
    }

    private AnswerOption createAnswerOption(UUID id, McqQuestion question, boolean correct) {
        AnswerOption option = new AnswerOption();
        option.setId(id);
        option.setQuestion(question);
        option.setCorrect(correct);
        option.setContent("Option");
        option.setOrderIndex(0);
        return option;
    }

    private AssessmentSubmission createSubmissionWithScore(UUID id, UUID studentId, int attemptNo, BigDecimal score) {
        return AssessmentSubmission.builder()
                .id(id)
                .assessment(createPublishedAssessment(UUID.randomUUID()))
                .studentId(studentId)
                .attemptNo(attemptNo)
                .status(AssessmentSubmissionStatus.SUBMITTED)
                .submitTime(Instant.now())
                .score(score)
                .actualScore(score)
                .questionSubmissions(new ArrayList<>())
                .build();
    }
}
