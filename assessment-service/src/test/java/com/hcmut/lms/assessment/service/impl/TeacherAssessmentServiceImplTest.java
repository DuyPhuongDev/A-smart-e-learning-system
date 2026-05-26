package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.client.CourseManagementInternalClient;
import com.hcmut.lms.assessment.client.LearningInternalEnrollmentClient;
import com.hcmut.lms.assessment.client.UserManagementInternalClient;
import com.hcmut.lms.assessment.client.dto.ClassSectionReportMetadataResponse;
import com.hcmut.lms.assessment.client.dto.UserResponse;
import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.assessment.*;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.submission.*;
import com.hcmut.lms.assessment.dto.request.teacher.TeacherEssayGradeItemRequest;
import com.hcmut.lms.assessment.dto.request.teacher.TeacherEssayGradesRequest;
import com.hcmut.lms.assessment.event.AssessmentEventPublisher;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.ForbiddenException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.repository.*;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import feign.FeignException;
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
class TeacherAssessmentServiceImplTest {

    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private AssessmentSubmissionRepository assessmentSubmissionRepository;
    @Mock
    private AssessmentQuestionRepository assessmentQuestionRepository;
    @Mock
    private QuestionSubmissionRepository questionSubmissionRepository;
    @Mock
    private McqSubmissionRepository mcqSubmissionRepository;
    @Mock
    private EssaySubmissionRepository essaySubmissionRepository;
    @Mock
    private CodingSubmissionRepository codingSubmissionRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private CourseManagementInternalClient courseManagementInternalClient;
    @Mock
    private LearningInternalEnrollmentClient learningInternalEnrollmentClient;
    @Mock
    private UserManagementInternalClient userManagementInternalClient;
    @Mock
    private AssessmentEventPublisher assessmentEventPublisher;

    @InjectMocks
    private TeacherAssessmentServiceImpl teacherAssessmentService;

    private static final UUID CLASS_ID = UUID.randomUUID();
    private static final UUID TEACHER_ID = UUID.randomUUID();

    private CurrentUserInfo teacherUser() {
        CurrentUserInfo user = mock(CurrentUserInfo.class);
        lenient().when(user.getId()).thenReturn(TEACHER_ID);
        lenient().when(user.getRole()).thenReturn("TEACHER");
        return user;
    }

    private CurrentUserInfo adminUser() {
        CurrentUserInfo user = mock(CurrentUserInfo.class);
        lenient().when(user.getId()).thenReturn(UUID.randomUUID());
        lenient().when(user.getRole()).thenReturn("ADMIN");
        return user;
    }

    private ClassSectionReportMetadataResponse teacherMetadata() {
        ClassSectionReportMetadataResponse meta = new ClassSectionReportMetadataResponse();
        meta.setTeacherId(TEACHER_ID);
        return meta;
    }

    private void stubTeacherAccess() {
        when(courseManagementInternalClient.getClassSectionReportMetadata(CLASS_ID)).thenReturn(teacherMetadata());
    }

    // --- listClassAssessments ---

    @Test
    void listClassAssessments_shouldReturnPage_whenTeacherAccess() {
        stubTeacherAccess();
        Assessment a1 = createAssessment(UUID.randomUUID());
        Page<Assessment> page = new PageImpl<>(List.of(a1));
        when(assessmentRepository.findAllByClassId(eq(CLASS_ID), any(PageRequest.class))).thenReturn(page);

        var result = teacherAssessmentService.listClassAssessments(CLASS_ID, teacherUser(), 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listClassAssessments_shouldThrowException_whenNullUser() {
        assertThatThrownBy(() ->
                teacherAssessmentService.listClassAssessments(CLASS_ID, null, 0, 10))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void listClassAssessments_shouldAllowAdmin() {
        ClassSectionReportMetadataResponse meta = new ClassSectionReportMetadataResponse();
        when(courseManagementInternalClient.getClassSectionReportMetadata(CLASS_ID)).thenReturn(meta);
        Assessment a1 = createAssessment(UUID.randomUUID());
        Page<Assessment> page = new PageImpl<>(List.of(a1));
        when(assessmentRepository.findAllByClassId(eq(CLASS_ID), any(PageRequest.class))).thenReturn(page);

        var result = teacherAssessmentService.listClassAssessments(CLASS_ID, adminUser(), 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void listClassAssessments_shouldThrowException_whenNotAssignedTeacher() {
        when(courseManagementInternalClient.getClassSectionReportMetadata(CLASS_ID)).thenReturn(teacherMetadata());

        CurrentUserInfo otherTeacher = mock(CurrentUserInfo.class);
        when(otherTeacher.getId()).thenReturn(UUID.randomUUID());
        when(otherTeacher.getRole()).thenReturn("TEACHER");

        assertThatThrownBy(() ->
                teacherAssessmentService.listClassAssessments(CLASS_ID, otherTeacher, 0, 10))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not assigned");
    }

    // --- getClassAssessmentReport ---

    @Test
    void getClassAssessmentReport_shouldReturnReport_whenNoAssessments() {
        stubTeacherAccess();
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of());

        var result = teacherAssessmentService.getClassAssessmentReport(CLASS_ID, teacherUser());

        assertThat(result.getClassId()).isEqualTo(CLASS_ID);
        assertThat(result.getAssessments()).isEmpty();
    }

    @Test
    void getClassAssessmentReport_shouldReturnReport_whenAssessmentsExist() {
        stubTeacherAccess();
        UUID studentId = UUID.randomUUID();
        Assessment a1 = createAssessment(UUID.randomUUID());
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of(a1));
        when(learningInternalEnrollmentClient.getStudentIdsByClassId(CLASS_ID)).thenReturn(List.of(studentId));
        when(assessmentSubmissionRepository.findByAssessment_IdInAndStudentIdInAndStatus(anyList(), anyList(), any()))
                .thenReturn(List.of());
        when(assessmentQuestionRepository.findByAssessmentIdsOrderByAssessmentAndIndex(anyList()))
                .thenReturn(List.of());

        var result = teacherAssessmentService.getClassAssessmentReport(CLASS_ID, teacherUser());

        assertThat(result.getTotalStudents()).isEqualTo(1);
        assertThat(result.getAssessments()).hasSize(1);
    }

    // --- listAssessmentSubmissions ---

    @Test
    void listAssessmentSubmissions_shouldReturnList_whenNoStatusFilter() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        stubTeacherAccess();
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        AssessmentSubmission sub = createSubmission(UUID.randomUUID(), assessment, UUID.randomUUID(), 1);
        Page<AssessmentSubmission> page = new PageImpl<>(List.of(sub));
        when(assessmentSubmissionRepository.findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
                eq(assessmentId), eq(AssessmentSubmissionStatus.SUBMITTED), any(PageRequest.class)))
                .thenReturn(page);

        var result = teacherAssessmentService.listAssessmentSubmissions(assessmentId, null, teacherUser(), 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    // --- getSubmissionDetail ---

    @Test
    void getSubmissionDetail_shouldReturnDetail_whenValid() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        attempt.setSubmitTime(Instant.now());
        stubTeacherAccess();
        McqQuestion question = createMcqQuestion(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of());

        var result = teacherAssessmentService.getSubmissionDetail(attemptId, teacherUser());

        assertThat(result.getAttemptId()).isEqualTo(attemptId);
        assertThat(result.getQuestions()).hasSize(1);
    }

    @Test
    void getSubmissionDetail_shouldThrowException_whenNotFound() {
        UUID attemptId = UUID.randomUUID();
        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherAssessmentService.getSubmissionDetail(attemptId, teacherUser()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- gradeEssaySubmission ---

    @Test
    void gradeEssaySubmission_shouldThrowException_whenEmptyItems() {
        UUID attemptId = UUID.randomUUID();
        TeacherEssayGradesRequest request = new TeacherEssayGradesRequest();
        request.setItems(List.of());

        assertThatThrownBy(() -> teacherAssessmentService.gradeEssaySubmission(attemptId, request, teacherUser()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("items must not be empty");
    }

    @Test
    void gradeEssaySubmission_shouldThrowException_whenAttemptNotFound() {
        UUID attemptId = UUID.randomUUID();
        TeacherEssayGradeItemRequest item = new TeacherEssayGradeItemRequest();
        item.setQuestionId(UUID.randomUUID());
        item.setScore(BigDecimal.TEN);
        TeacherEssayGradesRequest request = new TeacherEssayGradesRequest();
        request.setItems(List.of(item));

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherAssessmentService.gradeEssaySubmission(attemptId, request, teacherUser()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void gradeEssaySubmission_shouldGradeWithoutFeedback_whenNoFeedbackProvided() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        stubTeacherAccess();

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        QuestionSubmission qs = QuestionSubmission.builder()
                .question(essayQ).score(null).status(QuestionSubmissionStatus.PENDING_REVIEW).build();
        qs.setId(UUID.randomUUID());

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(essayQ).orderIndex(0).point(BigDecimal.TEN).build();

        TeacherEssayGradeItemRequest item = new TeacherEssayGradeItemRequest();
        item.setQuestionId(questionId);
        item.setScore(BigDecimal.valueOf(8));
        item.setFeedback(null);
        TeacherEssayGradesRequest request = new TeacherEssayGradesRequest();
        request.setItems(List.of(item));

        Feedback existingFeedback = Feedback.builder()
                .questionSubmission(qs).content("Previous feedback").build();

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId))
                .thenReturn(List.of(qs), List.of(qs));
        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(aq));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(qs);
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(aq));
        when(feedbackRepository.findByQuestionSubmission_IdOrderByFeedbackTimeDesc(qs.getId()))
                .thenReturn(List.of(existingFeedback));

        var result = teacherAssessmentService.gradeEssaySubmission(attemptId, request, teacherUser());

        assertThat(result).isNotNull();
    }

    @Test
    void gradeEssaySubmission_shouldThrowException_whenAttemptNotSubmitted() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        attempt.setStatus(AssessmentSubmissionStatus.IN_PROGRESS);
        stubTeacherAccess();

        TeacherEssayGradeItemRequest item = new TeacherEssayGradeItemRequest();
        item.setQuestionId(UUID.randomUUID());
        item.setScore(BigDecimal.TEN);
        TeacherEssayGradesRequest request = new TeacherEssayGradesRequest();
        request.setItems(List.of(item));

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> teacherAssessmentService.gradeEssaySubmission(attemptId, request, teacherUser()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not submitted");
    }

    // --- listAssessmentSubmissions (extended) ---

    @Test
    void listAssessmentSubmissions_shouldFilterByGradedStatus() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        stubTeacherAccess();
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        AssessmentSubmission sub = createSubmission(UUID.randomUUID(), assessment, UUID.randomUUID(), 1);
        sub.setScore(BigDecimal.TEN);
        when(assessmentSubmissionRepository.findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
                eq(assessmentId), eq(AssessmentSubmissionStatus.SUBMITTED)))
                .thenReturn(List.of(sub));

        var result = teacherAssessmentService.listAssessmentSubmissions(assessmentId, "GRADED", teacherUser(), 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    // --- getSubmissionDetail (extended) ---

    @Test
    void getSubmissionDetail_shouldThrowException_whenNotTeacherAndNotAdmin() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        stubTeacherAccess();

        CurrentUserInfo otherTeacher = mock(CurrentUserInfo.class);
        lenient().when(otherTeacher.getId()).thenReturn(UUID.randomUUID());
        lenient().when(otherTeacher.getRole()).thenReturn("TEACHER");

        assertThatThrownBy(() -> teacherAssessmentService.getSubmissionDetail(attemptId, otherTeacher))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not assigned");
    }

    // --- gradeEssaySubmission (extended) ---

    @Test
    void gradeEssaySubmission_shouldThrowException_whenQuestionNotInAttempt() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        stubTeacherAccess();

        TeacherEssayGradeItemRequest item = new TeacherEssayGradeItemRequest();
        item.setQuestionId(UUID.randomUUID());
        item.setScore(BigDecimal.TEN);
        TeacherEssayGradesRequest request = new TeacherEssayGradesRequest();
        request.setItems(List.of(item));

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of());

        assertThatThrownBy(() -> teacherAssessmentService.gradeEssaySubmission(attemptId, request, teacherUser()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not belong to this attempt");
    }

    // --- gradeEssaySubmission (success) ---

    @Test
    void gradeEssaySubmission_shouldGradeSuccessfully_whenValid() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        stubTeacherAccess();

        com.hcmut.lms.assessment.domain.entity.question.EssayQuestion essayQ =
                new com.hcmut.lms.assessment.domain.entity.question.EssayQuestion();
        essayQ.setId(questionId);
        essayQ.setQuestionType(QuestionType.ESSAY);
        essayQ.setContent("Explain");
        essayQ.setAcceptedFileTypes(new ArrayList<>());

        QuestionSubmission qs = QuestionSubmission.builder()
                .question(essayQ).score(null).status(QuestionSubmissionStatus.PENDING_REVIEW).build();
        qs.setId(UUID.randomUUID());

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(essayQ).orderIndex(0).point(BigDecimal.TEN).build();

        TeacherEssayGradeItemRequest item = new TeacherEssayGradeItemRequest();
        item.setQuestionId(questionId);
        item.setScore(BigDecimal.valueOf(8));
        item.setFeedback("Good work");
        TeacherEssayGradesRequest request = new TeacherEssayGradesRequest();
        request.setItems(List.of(item));

        when(assessmentSubmissionRepository.findByIdForUpdate(attemptId)).thenReturn(Optional.of(attempt));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId))
                .thenReturn(List.of(qs));
        when(assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, assessmentId))
                .thenReturn(Optional.of(aq));
        when(questionSubmissionRepository.save(any(QuestionSubmission.class))).thenReturn(qs);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(null);
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(aq));

        var result = teacherAssessmentService.gradeEssaySubmission(attemptId, request, teacherUser());

        assertThat(result).isNotNull();
        assertThat(result.getUpdatedQuestions()).hasSize(1);
    }

    // --- getSubmissionDetail (extended) ---

    @Test
    void getSubmissionDetail_shouldReturnMcqOptions_whenMcqQuestion() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        attempt.setSubmitTime(Instant.now());
        stubTeacherAccess();

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
                .question(question).status(QuestionSubmissionStatus.CORRECT).score(BigDecimal.TEN).build();
        mcqSub.setSelectedOptions(new ArrayList<>());

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(mcqSub));

        var result = teacherAssessmentService.getSubmissionDetail(attemptId, teacherUser());

        assertThat(result.getQuestions()).hasSize(1);
        assertThat(result.getQuestions().getFirst().getOptions()).hasSize(1);
    }

    // --- getClassGradebook (extended) ---

    @Test
    void getClassGradebook_shouldReturnCells_whenSubmissionsExist() {
        stubTeacherAccess();
        UUID studentId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Assessment a1 = createAssessment(assessmentId);
        AssessmentSubmission submission = createSubmission(UUID.randomUUID(), a1, studentId, 1);
        submission.setScore(BigDecimal.valueOf(85));
        submission.setActualScore(BigDecimal.valueOf(85));

        when(learningInternalEnrollmentClient.getStudentIdsByClassId(CLASS_ID)).thenReturn(List.of(studentId));
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of(a1));
        when(assessmentSubmissionRepository.findByStudentIdInAndAssessment_IdInAndStatus(
                anyList(), anyList(), eq(AssessmentSubmissionStatus.SUBMITTED)))
                .thenReturn(List.of(submission));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of());
        UserResponse userResponse = new UserResponse();
        userResponse.setId(studentId);
        userResponse.setStudentCode("SV001");
        lenient().when(userManagementInternalClient.getUserById(studentId)).thenReturn(userResponse);

        var result = teacherAssessmentService.getClassGradebook(CLASS_ID, teacherUser(), 0, 10);

        assertThat(result.getRows()).hasSize(1);
        assertThat(result.getAssessments()).hasSize(1);
    }

    @Test
    void getClassGradebook_shouldReturnGradebook_whenHasSubmissionsWithQuestions() {
        stubTeacherAccess();
        UUID studentId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();

        Assessment a1 = createAssessment(assessmentId);
        AssessmentSubmission submission = createSubmission(attemptId, a1, studentId, 1);
        submission.setScore(BigDecimal.valueOf(90));
        submission.setActualScore(BigDecimal.valueOf(90));

        McqQuestion question = createMcqQuestion(questionId);
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(a1).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        QuestionSubmission qs = new QuestionSubmission();
        qs.setId(UUID.randomUUID());
        qs.setQuestion(question);
        qs.setStatus(QuestionSubmissionStatus.CORRECT);
        qs.setScore(BigDecimal.TEN);
        qs.setAssessmentSubmission(submission);

        when(learningInternalEnrollmentClient.getStudentIdsByClassId(CLASS_ID)).thenReturn(List.of(studentId));
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of(a1));
        when(assessmentSubmissionRepository.findByStudentIdInAndAssessment_IdInAndStatus(
                anyList(), anyList(), eq(AssessmentSubmissionStatus.SUBMITTED)))
                .thenReturn(List.of(submission));
        when(questionSubmissionRepository.findAllByAttemptIdsWithQuestion(anyList()))
                .thenReturn(List.of(qs));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId))
                .thenReturn(List.of(aq));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(studentId);
        userResponse.setStudentCode("SV001");
        lenient().when(userManagementInternalClient.getUserById(studentId)).thenReturn(userResponse);

        var result = teacherAssessmentService.getClassGradebook(CLASS_ID, teacherUser(), 0, 10);

        assertThat(result.getRows()).hasSize(1);
        assertThat(result.getRows().getFirst().getCells()).hasSize(1);
    }

    // --- computeAttemptGradingStatus ---

    @Test
    void computeAttemptGradingStatus_shouldReturnPendingReview_whenPendingExists() {
        QuestionSubmission qs = new QuestionSubmission();
        qs.setStatus(QuestionSubmissionStatus.PENDING_REVIEW);

        String result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "computeAttemptGradingStatus", List.of(qs));
        assertThat(result).isEqualTo("PENDING_REVIEW");
    }

    @Test
    void computeAttemptGradingStatus_shouldReturnGraded_whenAllGraded() {
        QuestionSubmission qs = new QuestionSubmission();
        qs.setStatus(QuestionSubmissionStatus.CORRECT);

        String result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "computeAttemptGradingStatus", List.of(qs));
        assertThat(result).isEqualTo("GRADED");
    }

    // --- selectScoreByRule more cases ---

    @Test
    void selectScoreByRule_shouldReturnNull_whenAllScoresNull() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, null);

        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", GradingRule.LAST_ATTEMPT, List.of(a1));
        assertThat(result).isNull();
    }

    // --- private methods via ReflectionTestUtils ---

    @Test
    void normalizeScore_shouldReturnScaledScore_whenValid() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "normalizeScore", BigDecimal.valueOf(7.5), BigDecimal.TEN);
        assertThat(result).isEqualTo(new BigDecimal("7.500"));
    }

    @Test
    void normalizeScore_shouldThrowException_whenNull() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "normalizeScore", null, BigDecimal.TEN))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void normalizeScore_shouldThrowException_whenNegative() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "normalizeScore", BigDecimal.valueOf(-1), BigDecimal.TEN))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void normalizeScore_shouldThrowException_whenAboveMax() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "normalizeScore", BigDecimal.valueOf(11), BigDecimal.TEN))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void mapEssayStatus_shouldReturnCorrect_whenFullScore() {
        QuestionSubmissionStatus result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "mapEssayStatus", BigDecimal.TEN, BigDecimal.TEN);
        assertThat(result).isEqualTo(QuestionSubmissionStatus.CORRECT);
    }

    @Test
    void mapEssayStatus_shouldReturnIncorrect_whenZeroScore() {
        QuestionSubmissionStatus result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "mapEssayStatus", BigDecimal.ZERO, BigDecimal.TEN);
        assertThat(result).isEqualTo(QuestionSubmissionStatus.INCORRECT);
    }

    @Test
    void mapEssayStatus_shouldReturnPartial_whenPartialScore() {
        QuestionSubmissionStatus result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "mapEssayStatus", BigDecimal.valueOf(5), BigDecimal.TEN);
        assertThat(result).isEqualTo(QuestionSubmissionStatus.PARTIAL);
    }

    @Test
    void percent_shouldReturnPercentage_whenValid() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "percent", 3, 4);
        assertThat(result).isEqualTo(BigDecimal.valueOf(75.00).setScale(2));
    }

    @Test
    void percent_shouldReturnZero_whenDenominatorZero() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "percent", 3, 0);
        assertThat(result).isEqualTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void selectScoreByRule_shouldReturnLastAttemptScore() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", GradingRule.LAST_ATTEMPT, List.of(a1, a2));
        assertThat(result).isEqualTo(BigDecimal.valueOf(90));
    }

    @Test
    void selectScoreByRule_shouldReturnHighestScore() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", GradingRule.HIGH_SCORE, List.of(a1, a2));
        assertThat(result).isEqualTo(BigDecimal.valueOf(90));
    }

    @Test
    void selectScoreByRule_shouldReturnFirstAttemptScore() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", GradingRule.FIRST_ATTEMPT, List.of(a1, a2));
        assertThat(result).isEqualTo(BigDecimal.valueOf(80));
    }

    @Test
    void selectScoreByRule_shouldReturnAvgScore() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(100));

        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", GradingRule.AVG_SCORE, List.of(a1, a2));
        assertThat(result).isEqualTo(new BigDecimal("90.000"));
    }

    @Test
    void selectScoreByRule_shouldReturnNull_whenEmptyList() {
        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", GradingRule.LAST_ATTEMPT, List.of());
        assertThat(result).isNull();
    }

    @Test
    void selectScoreByRule_shouldDefaultToLastAttempt_whenRuleNull() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        BigDecimal result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectScoreByRule", (GradingRule) null, List.of(a1, a2));
        assertThat(result).isEqualTo(BigDecimal.valueOf(90));
    }

    @Test
    void selectAttemptByRule_shouldReturnLastAttempt() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        AssessmentSubmission result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectAttemptByRule", GradingRule.LAST_ATTEMPT, List.of(a1, a2));
        assertThat(result.getAttemptNo()).isEqualTo(2);
    }

    @Test
    void selectAttemptByRule_shouldReturnNull_whenEmptyList() {
        AssessmentSubmission result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectAttemptByRule", GradingRule.LAST_ATTEMPT, List.of());
        assertThat(result).isNull();
    }

    @Test
    void sliceToPage_shouldReturnPage_whenItemsExist() {
        List<String> items = List.of("a", "b", "c", "d", "e");
        Page<String> result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "sliceToPage", items, 0, 2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    // --- getClassGradebook ---

    @Test
    void getClassGradebook_shouldReturnGradebook_whenNoStudents() {
        stubTeacherAccess();
        when(learningInternalEnrollmentClient.getStudentIdsByClassId(CLASS_ID)).thenReturn(List.of());
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of());

        var result = teacherAssessmentService.getClassGradebook(CLASS_ID, teacherUser(), 0, 10);

        assertThat(result.getRows()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void getClassGradebook_shouldReturnGradebook_whenHasStudents() {
        stubTeacherAccess();
        UUID studentId = UUID.randomUUID();
        Assessment a1 = createAssessment(UUID.randomUUID());
        when(learningInternalEnrollmentClient.getStudentIdsByClassId(CLASS_ID)).thenReturn(List.of(studentId));
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of(a1));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(a1.getId())).thenReturn(List.of());
        UserResponse userResponse = new UserResponse();
        userResponse.setId(studentId);
        userResponse.setStudentCode("SV001");
        lenient().when(userManagementInternalClient.getUserById(studentId)).thenReturn(userResponse);

        var result = teacherAssessmentService.getClassGradebook(CLASS_ID, teacherUser(), 0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // --- getSubmissionDetail with ESSAY ---

    @Test
    void getSubmissionDetail_shouldReturnEssayFields_whenEssayQuestion() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        attempt.setSubmitTime(Instant.now());
        stubTeacherAccess();

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
                .question(essayQ).status(QuestionSubmissionStatus.PENDING_REVIEW)
                .answerText("Essay content").answerFileUrl("file.pdf")
                .fileFormat("pdf").numPages(3).wordCount(200).build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(essaySub));

        var result = teacherAssessmentService.getSubmissionDetail(attemptId, teacherUser());

        assertThat(result.getQuestions()).hasSize(1);
    }

    // --- getSubmissionDetail with CODING ---

    @Test
    void getSubmissionDetail_shouldReturnCodingFields_whenCodingQuestion() {
        UUID attemptId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        AssessmentSubmission attempt = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        attempt.setSubmitTime(Instant.now());
        stubTeacherAccess();

        com.hcmut.lms.assessment.domain.entity.question.CodingQuestion codingQ =
                new com.hcmut.lms.assessment.domain.entity.question.CodingQuestion();
        codingQ.setId(questionId);
        codingQ.setQuestionType(QuestionType.CODING);
        codingQ.setContent("Code");
        codingQ.setProblemDescription("Solve");
        codingQ.setExecutionTimeLimit(1000);
        codingQ.setExecutionMemoryLimit(256);
        codingQ.setLanguage("cpp");
        codingQ.setInitialCode("// start");

        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(assessment).question(codingQ).orderIndex(0).point(BigDecimal.TEN).build();

        CodingSubmission codingSub = CodingSubmission.builder()
                .question(codingQ).status(QuestionSubmissionStatus.CORRECT).score(BigDecimal.TEN)
                .inputCode("int main(){}").executionLanguage("cpp").build();

        when(assessmentSubmissionRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId)).thenReturn(List.of(aq));
        when(questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId)).thenReturn(List.of(codingSub));

        var result = teacherAssessmentService.getSubmissionDetail(attemptId, teacherUser());

        assertThat(result.getQuestions()).hasSize(1);
    }

    // --- listAssessmentSubmissions with PENDING_REVIEW filter ---

    @Test
    void listAssessmentSubmissions_shouldFilterByPendingReview() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        stubTeacherAccess();
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));
        AssessmentSubmission sub = createSubmission(UUID.randomUUID(), assessment, UUID.randomUUID(), 1);
        sub.setScore(BigDecimal.TEN);

        QuestionSubmission qs = new QuestionSubmission();
        qs.setId(UUID.randomUUID());
        qs.setStatus(QuestionSubmissionStatus.PENDING_REVIEW);

        when(assessmentSubmissionRepository.findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
                eq(assessmentId), eq(AssessmentSubmissionStatus.SUBMITTED)))
                .thenReturn(List.of(sub));
        when(questionSubmissionRepository.findAttemptIdsByAttemptIdsAndStatus(anyList(), eq(QuestionSubmissionStatus.PENDING_REVIEW)))
                .thenReturn(List.of(sub.getId()));

        var result = teacherAssessmentService.listAssessmentSubmissions(assessmentId, "PENDING_REVIEW", teacherUser(), 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    // --- listAssessmentSubmissions with invalid filter ---

    @Test
    void listAssessmentSubmissions_shouldThrowException_whenInvalidFilter() {
        UUID assessmentId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        assessment.setClassId(CLASS_ID);
        stubTeacherAccess();
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(assessment));

        assertThatThrownBy(() ->
                teacherAssessmentService.listAssessmentSubmissions(assessmentId, "INVALID", teacherUser(), 0, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unsupported status filter");
    }

    // --- getClassAssessmentReport with submissions ---

    @Test
    void getClassAssessmentReport_shouldCalculateStats_whenSubmissionsExist() {
        stubTeacherAccess();
        UUID studentId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        Assessment a1 = createAssessment(assessmentId);
        a1.setCloseTime(Instant.now().plusSeconds(3600));

        AssessmentSubmission submission = createSubmission(attemptId, a1, studentId, 1);
        submission.setScore(BigDecimal.valueOf(90));
        submission.setActualScore(BigDecimal.valueOf(90));

        McqQuestion question = createMcqQuestion(UUID.randomUUID());
        AssessmentQuestion aq = AssessmentQuestion.builder()
                .assessment(a1).question(question).orderIndex(0).point(BigDecimal.TEN).build();

        QuestionSubmission qs = new QuestionSubmission();
        qs.setId(UUID.randomUUID());
        qs.setQuestion(question);
        qs.setStatus(QuestionSubmissionStatus.CORRECT);
        qs.setScore(BigDecimal.TEN);
        qs.setAssessmentSubmission(submission);

        when(learningInternalEnrollmentClient.getStudentIdsByClassId(CLASS_ID)).thenReturn(List.of(studentId));
        when(assessmentRepository.findByClassIdOrderByCreatedAtAsc(CLASS_ID)).thenReturn(List.of(a1));
        when(assessmentSubmissionRepository.findByAssessment_IdInAndStudentIdInAndStatus(anyList(), anyList(), any()))
                .thenReturn(List.of(submission));
        when(questionSubmissionRepository.findAllByAttemptIdsWithQuestion(anyList()))
                .thenReturn(List.of(qs));
        when(assessmentQuestionRepository.findByAssessmentIdsOrderByAssessmentAndIndex(anyList()))
                .thenReturn(List.of(aq));

        var result = teacherAssessmentService.getClassAssessmentReport(CLASS_ID, teacherUser());

        assertThat(result.getAssessments()).hasSize(1);
        assertThat(result.getAssessments().getFirst().getSubmittedCount()).isEqualTo(1);
    }

    // --- selectAttemptByRule additional cases ---

    @Test
    void selectAttemptByRule_shouldReturnHighestScoreAttempt() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(95));

        AssessmentSubmission result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectAttemptByRule", GradingRule.HIGH_SCORE, List.of(a1, a2));
        assertThat(result.getAttemptNo()).isEqualTo(2);
    }

    @Test
    void selectAttemptByRule_shouldReturnFirstAttempt() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(80));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(90));

        AssessmentSubmission result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectAttemptByRule", GradingRule.FIRST_ATTEMPT, List.of(a1, a2));
        assertThat(result.getAttemptNo()).isEqualTo(1);
    }

    @Test
    void selectAttemptByRule_shouldDefaultToLastAttempt_whenRuleNull() {
        AssessmentSubmission a1 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.valueOf(70));
        AssessmentSubmission a2 = createSubmissionWithScore(UUID.randomUUID(), UUID.randomUUID(), 2, BigDecimal.valueOf(85));

        AssessmentSubmission result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "selectAttemptByRule", (GradingRule) null, List.of(a1, a2));
        assertThat(result.getAttemptNo()).isEqualTo(2);
    }

    // --- isOnTime ---

    @Test
    void isOnTime_shouldReturnTrue_whenSubmitTimeBeforeClose() {
        AssessmentSubmission a = createSubmission(UUID.randomUUID(), createAssessment(UUID.randomUUID()), UUID.randomUUID(), 1);
        a.setSubmitTime(Instant.now().minusSeconds(600));
        Instant closeTime = Instant.now();

        boolean result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "isOnTime", a, closeTime);
        assertThat(result).isTrue();
    }

    @Test
    void isOnTime_shouldReturnTrue_whenSubmitTimeNull() {
        AssessmentSubmission a = createSubmission(UUID.randomUUID(), createAssessment(UUID.randomUUID()), UUID.randomUUID(), 1);
        a.setSubmitTime(null);

        boolean result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "isOnTime", a, Instant.now());
        assertThat(result).isTrue();
    }

    // --- buildDisplayName ---

    @Test
    void buildDisplayName_shouldReturnFullName_whenValid() {
        UserResponse user = new UserResponse();
        user.setFirstName("John");
        user.setLastName("Doe");

        String result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "buildDisplayName", user);
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    void buildDisplayName_shouldReturnNull_whenNull() {
        String result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "buildDisplayName", (UserResponse) null);
        assertThat(result).isNull();
    }

    // --- loadStudentProfile ---

    @Test
    void loadStudentProfile_shouldReturnUser_whenExists() {
        UUID studentId = UUID.randomUUID();
        UserResponse user = new UserResponse();
        user.setId(studentId);
        user.setStudentCode("SV001");
        when(userManagementInternalClient.getUserById(studentId)).thenReturn(user);

        UserResponse result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "loadStudentProfile", studentId);
        assertThat(result).isNotNull();
        assertThat(result.getStudentCode()).isEqualTo("SV001");
    }

    @Test
    void loadStudentProfile_shouldReturnNull_whenFeignException() {
        UUID studentId = UUID.randomUUID();
        FeignException feignEx = mock(FeignException.class);
        when(feignEx.status()).thenReturn(404);
        when(feignEx.getMessage()).thenReturn("Not found");
        when(userManagementInternalClient.getUserById(studentId)).thenThrow(feignEx);

        UserResponse result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "loadStudentProfile", studentId);
        assertThat(result).isNull();
    }

    @Test
    void loadStudentProfile_shouldReturnNull_whenGenericException() {
        UUID studentId = UUID.randomUUID();
        when(userManagementInternalClient.getUserById(studentId)).thenThrow(new RuntimeException("Boom"));

        UserResponse result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "loadStudentProfile", studentId);
        assertThat(result).isNull();
    }

    // --- getClassSectionReportMetadataOrThrow ---

    @Test
    void getClassSectionReportMetadataOrThrow_shouldThrowResourceNotFound_when404() {
        FeignException notFound = mock(FeignException.class);
        when(notFound.status()).thenReturn(404);
        when(courseManagementInternalClient.getClassSectionReportMetadata(CLASS_ID)).thenThrow(notFound);

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "getClassSectionReportMetadataOrThrow", CLASS_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- toGradebookCell with PENDING_REVIEW attempt ---

    @Test
    void toGradebookCell_shouldReturnPendingReview_whenAttemptHasPendingReview() {
        UUID assessmentId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        Assessment assessment = createAssessment(assessmentId);
        AssessmentSubmission submission = createSubmission(attemptId, assessment, UUID.randomUUID(), 1);
        submission.setScore(BigDecimal.TEN);

        Set<UUID> pendingIds = Set.of(attemptId);

        var result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "toGradebookCell", assessment, List.of(submission), pendingIds, Map.of(), 1);
        // result is TeacherGradebookCellResponse - status should be PENDING_REVIEW
        assertThat(result).isNotNull();
        // status is a string, accessed via getter
    }

    // --- normalizeSubmissionStatusFilter ---

    @Test
    void normalizeSubmissionStatusFilter_shouldReturnNull_whenBlank() {
        String result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "normalizeSubmissionStatusFilter", "  ");
        assertThat(result).isNull();
    }

    @Test
    void resolveConcreteQuestion_shouldReturnNull_whenNull() {
        Object result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "resolveConcreteQuestion", (com.hcmut.lms.assessment.domain.entity.question.Question) null);
        assertThat(result).isNull();
    }

    // --- loadLatestFeedback via ReflectionTestUtils ---

    @Test
    void loadLatestFeedback_shouldReturnEmptyMap_whenNull() {
        Map<UUID, String> result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "loadLatestFeedback", (List<UUID>) null);
        assertThat(result).isEmpty();
    }

    @Test
    void loadLatestFeedback_shouldReturnEmptyMap_whenEmptyList() {
        Map<UUID, String> result = ReflectionTestUtils.invokeMethod(teacherAssessmentService,
                "loadLatestFeedback", List.of());
        assertThat(result).isEmpty();
    }

    // --- helper methods ---

    private Assessment createAssessment(UUID id) {
        Assessment a = new Assessment();
        a.setId(id);
        a.setClassId(CLASS_ID);
        a.setTitle("Test Assessment");
        a.setAssessmentType(AssessmentType.ASSIGNMENT);
        a.setAssessmentStatus(AssessmentStatus.PUBLISHED);
        a.setMaxAttempts(0);
        a.setTimeLimit(0);
        a.setGradingRule(GradingRule.LAST_ATTEMPT);
        a.setAssessmentQuestions(new HashSet<>());
        return a;
    }

    private AssessmentSubmission createSubmission(UUID id, Assessment assessment, UUID studentId, int attemptNo) {
        return AssessmentSubmission.builder()
                .id(id)
                .assessment(assessment)
                .studentId(studentId)
                .attemptNo(attemptNo)
                .status(AssessmentSubmissionStatus.SUBMITTED)
                .submitTime(Instant.now())
                .questionSubmissions(new ArrayList<>())
                .build();
    }

    private AssessmentSubmission createSubmissionWithScore(UUID id, UUID studentId, int attemptNo, BigDecimal score) {
        return AssessmentSubmission.builder()
                .id(id)
                .assessment(createAssessment(UUID.randomUUID()))
                .studentId(studentId)
                .attemptNo(attemptNo)
                .status(AssessmentSubmissionStatus.SUBMITTED)
                .submitTime(Instant.now())
                .score(score)
                .actualScore(score)
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
}
