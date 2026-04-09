package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.client.CourseManagementInternalClient;
import com.hcmut.lms.assessment.client.LearningInternalEnrollmentClient;
import com.hcmut.lms.assessment.client.dto.ClassSectionReportMetadataResponse;
import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.assessment.GradingRule;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import com.hcmut.lms.assessment.domain.entity.submission.CodingSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.EssaySubmission;
import com.hcmut.lms.assessment.domain.entity.submission.Feedback;
import com.hcmut.lms.assessment.domain.entity.submission.McqSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmissionStatus;
import com.hcmut.lms.assessment.dto.request.teacher.TeacherEssayGradeItemRequest;
import com.hcmut.lms.assessment.dto.request.teacher.TeacherEssayGradesRequest;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherAssessmentSummaryResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherEssayGradeUpdatedQuestionResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherEssayGradesResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherGradebookAssessmentColumnResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherGradebookCellResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherGradebookResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherGradebookRowResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherQuestionOptionResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherQuestionReviewResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherSubmissionDetailResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherSubmissionSummaryResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.ForbiddenException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.event.AssessmentEventPublisher;
import com.hcmut.lms.assessment.repository.AssessmentQuestionRepository;
import com.hcmut.lms.assessment.repository.AssessmentRepository;
import com.hcmut.lms.assessment.repository.AssessmentSubmissionRepository;
import com.hcmut.lms.assessment.repository.CodingSubmissionRepository;
import com.hcmut.lms.assessment.repository.EssaySubmissionRepository;
import com.hcmut.lms.assessment.repository.FeedbackRepository;
import com.hcmut.lms.assessment.repository.McqSubmissionRepository;
import com.hcmut.lms.assessment.repository.QuestionSubmissionRepository;
import com.hcmut.lms.assessment.service.TeacherAssessmentService;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TeacherAssessmentServiceImpl implements TeacherAssessmentService {

    private static final String STATUS_NOT_STARTED = "NOT_STARTED";
    private static final String STATUS_PENDING_REVIEW = "PENDING_REVIEW";
    private static final String STATUS_GRADED = "GRADED";

    private final AssessmentRepository assessmentRepository;
    private final AssessmentSubmissionRepository assessmentSubmissionRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final QuestionSubmissionRepository questionSubmissionRepository;
    private final McqSubmissionRepository mcqSubmissionRepository;
    private final EssaySubmissionRepository essaySubmissionRepository;
    private final CodingSubmissionRepository codingSubmissionRepository;
    private final FeedbackRepository feedbackRepository;
    private final CourseManagementInternalClient courseManagementInternalClient;
    private final LearningInternalEnrollmentClient learningInternalEnrollmentClient;
    private final AssessmentEventPublisher assessmentEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TeacherAssessmentSummaryResponse> listClassAssessments(
            UUID classId,
            CurrentUserInfo currentUser,
            int page,
            int size
    ) {
        assertTeacherCanAccessClass(classId, currentUser);

        Page<Assessment> assessments = assessmentRepository.findAllByClassId(
                classId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        Page<TeacherAssessmentSummaryResponse> mappedPage = assessments.map(assessment ->
                TeacherAssessmentSummaryResponse.builder()
                        .assessmentId(assessment.getId())
                        .title(assessment.getTitle())
                        .gradingRule(assessment.getGradingRule())
                        .maxAttempts(assessment.getMaxAttempts())
                        .timeLimit(assessment.getTimeLimit())
                        .startTime(assessment.getStartTime())
                        .closeTime(assessment.getCloseTime())
                        .build()
        );

        return PageResponse.fromPage(mappedPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TeacherSubmissionSummaryResponse> listAssessmentSubmissions(
            UUID assessmentId,
            String status,
            CurrentUserInfo currentUser,
            int page,
            int size
    ) {
        Assessment assessment = findAssessment(assessmentId);
        assertTeacherCanAccessClass(assessment.getClassId(), currentUser);

        String normalizedStatusFilter = normalizeSubmissionStatusFilter(status);

        if (normalizedStatusFilter == null) {
            Page<AssessmentSubmission> attemptPage = assessmentSubmissionRepository
                    .findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
                            assessmentId,
                            AssessmentSubmissionStatus.SUBMITTED,
                            PageRequest.of(page, size)
                    );

            Set<UUID> pendingAttemptIds = findPendingReviewAttemptIds(
                    attemptPage.getContent().stream().map(AssessmentSubmission::getId).toList()
            );

            Page<TeacherSubmissionSummaryResponse> mappedPage = attemptPage.map(attempt ->
                    toSubmissionSummary(attempt, pendingAttemptIds)
            );
            return PageResponse.fromPage(mappedPage);
        }

        List<AssessmentSubmission> attempts = assessmentSubmissionRepository
                .findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
                        assessmentId,
                        AssessmentSubmissionStatus.SUBMITTED
                );

        Set<UUID> pendingAttemptIds = findPendingReviewAttemptIds(
                attempts.stream().map(AssessmentSubmission::getId).toList()
        );

        List<TeacherSubmissionSummaryResponse> filtered = attempts.stream()
                .map(attempt -> toSubmissionSummary(attempt, pendingAttemptIds))
                .filter(item -> normalizedStatusFilter.equals(item.getGradingStatus()))
                .toList();

        Page<TeacherSubmissionSummaryResponse> manualPage = sliceToPage(filtered, page, size);
        return PageResponse.fromPage(manualPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherSubmissionDetailResponse getSubmissionDetail(UUID attemptId, CurrentUserInfo currentUser) {
        AssessmentSubmission attempt = assessmentSubmissionRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("AssessmentSubmission", attemptId));

        Assessment assessment = attempt.getAssessment();
        assertTeacherCanAccessClass(assessment.getClassId(), currentUser);

        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByIndex(assessment.getId());

        Map<UUID, QuestionSubmission> submissionByQuestionId = questionSubmissionRepository
                .findAllByAttemptIdWithQuestion(attemptId)
                .stream()
                .collect(Collectors.toMap(qs -> qs.getQuestion().getId(), qs -> qs, (a, b) -> a));

        Map<UUID, String> latestFeedbackByQuestionSubmissionId = loadLatestFeedback(
                submissionByQuestionId.values().stream()
                        .map(QuestionSubmission::getId)
                        .toList()
        );

        BigDecimal maxScore = BigDecimal.ZERO;
        List<TeacherQuestionReviewResponse> questionDetails = new ArrayList<>();

        for (AssessmentQuestion assessmentQuestion : assessmentQuestions) {
            Question question = resolveConcreteQuestion(assessmentQuestion.getQuestion());
            QuestionSubmission questionSubmission = submissionByQuestionId.get(question.getId());

            BigDecimal questionMaxPoints = defaultPoint(question);
            maxScore = maxScore.add(questionMaxPoints);

            TeacherQuestionReviewResponse.TeacherQuestionReviewResponseBuilder builder =
                    TeacherQuestionReviewResponse.builder()
                            .questionId(question.getId())
                            .orderIndex(assessmentQuestion.getOrderIndex())
                            .questionType(question.getQuestionType())
                            .content(question.getContent())
                            .maxPoints(questionMaxPoints)
                            .earnedPoints(questionSubmission != null ? nonNull(questionSubmission.getScore()) : BigDecimal.ZERO)
                            .status(questionSubmission != null ? questionSubmission.getStatus() : QuestionSubmissionStatus.INCORRECT)
                            .feedback(questionSubmission != null ? latestFeedbackByQuestionSubmissionId.get(questionSubmission.getId()) : null);

            if (question.getQuestionType() == QuestionType.MCQ && question instanceof McqQuestion mcqQuestion) {
                builder.allowMultiAnswer(mcqQuestion.isAllowMultiAnswer());
                builder.options(mcqQuestion.getAnswerOptions().stream()
                        .sorted(Comparator.comparing(AnswerOption::getOrderIndex))
                        .map(option -> TeacherQuestionOptionResponse.builder()
                                .optionId(option.getId())
                                .content(option.getContent())
                                .correct(option.isCorrect())
                                .build())
                        .toList());

                McqSubmission mcqSubmission = resolveMcqSubmission(questionSubmission);
                if (mcqSubmission != null) {
                    builder.selectedOptionIds(mcqSubmission.getSelectedOptions().stream()
                            .map(selected -> selected.getOption().getId())
                            .toList());
                }
            }

            if (question.getQuestionType() == QuestionType.ESSAY && question instanceof EssayQuestion essayQuestion) {
                builder.maxFileSize(essayQuestion.getMaxFileSize());
                builder.acceptedFileTypes(essayQuestion.getAcceptedFileTypes().stream()
                        .map(fileType -> fileType.getFileType())
                        .toList());

                EssaySubmission essaySubmission = resolveEssaySubmission(questionSubmission);
                if (essaySubmission != null) {
                    builder.submittedText(essaySubmission.getAnswerText());
                    builder.submittedFileUrl(essaySubmission.getAnswerFileUrl());
                    builder.submittedFileFormat(essaySubmission.getFileFormat());
                    builder.submittedNumPages(essaySubmission.getNumPages());
                    builder.submittedWordCount(essaySubmission.getWordCount());
                }
            }

            if (question.getQuestionType() == QuestionType.CODING && question instanceof CodingQuestion codingQuestion) {
                builder.problemDescription(codingQuestion.getProblemDescription());
                builder.executionTimeLimit(codingQuestion.getExecutionTimeLimit());
                builder.executionMemoryLimit(codingQuestion.getExecutionMemoryLimit());
                builder.language(codingQuestion.getLanguage());
                builder.initialCode(codingQuestion.getInitialCode());

                CodingSubmission codingSubmission = resolveCodingSubmission(questionSubmission);
                if (codingSubmission != null) {
                    builder.submittedCode(codingSubmission.getInputCode());
                    builder.submittedLanguage(codingSubmission.getExecutionLanguage());
                }
            }

            questionDetails.add(builder.build());
        }

        String gradingStatus = computeAttemptGradingStatus(
                new ArrayList<>(submissionByQuestionId.values())
        );

        return TeacherSubmissionDetailResponse.builder()
                .attemptId(attempt.getId())
                .assessmentId(assessment.getId())
                .assessmentTitle(assessment.getTitle())
                .studentId(attempt.getStudentId())
                .attemptNo(attempt.getAttemptNo())
                .submittedAt(attempt.getSubmitTime())
                .takenTime(attempt.getTakenTime())
                .score(nonNull(attempt.getScore()))
                .maxScore(maxScore)
                .gradingStatus(gradingStatus)
                .questions(questionDetails)
                .build();
    }

    @Override
    public TeacherEssayGradesResponse gradeEssaySubmission(
            UUID attemptId,
            TeacherEssayGradesRequest request,
            CurrentUserInfo currentUser
    ) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("items must not be empty");
        }

        AssessmentSubmission attempt = assessmentSubmissionRepository.findByIdForUpdate(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("AssessmentSubmission", attemptId));

        Assessment assessment = attempt.getAssessment();
        assertTeacherCanAccessClass(assessment.getClassId(), currentUser);

        if (attempt.getStatus() != AssessmentSubmissionStatus.SUBMITTED) {
            throw new BadRequestException("Attempt is not submitted");
        }

        List<QuestionSubmission> questionSubmissions = questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId);
        Map<UUID, QuestionSubmission> questionSubmissionByQuestionId = questionSubmissions.stream()
                .collect(Collectors.toMap(qs -> qs.getQuestion().getId(), qs -> qs, (a, b) -> a));

        List<TeacherEssayGradeUpdatedQuestionResponse> updatedQuestions = new ArrayList<>();

        for (TeacherEssayGradeItemRequest item : request.getItems()) {
            QuestionSubmission questionSubmission = questionSubmissionByQuestionId.get(item.getQuestionId());
            if (questionSubmission == null) {
                throw new BadRequestException("Question does not belong to this attempt: " + item.getQuestionId());
            }

            Question question = resolveConcreteQuestion(questionSubmission.getQuestion());
            if (question.getQuestionType() != QuestionType.ESSAY) {
                throw new BadRequestException("Only ESSAY questions can be graded manually");
            }

            BigDecimal maxPoints = defaultPoint(question);
            BigDecimal score = normalizeScore(item.getScore(), maxPoints);

            questionSubmission.setScore(score);
            questionSubmission.setStatus(mapEssayStatus(score, maxPoints));
            questionSubmissionRepository.save(questionSubmission);

            String latestFeedback = null;
            if (item.getFeedback() != null) {
                feedbackRepository.save(Feedback.builder()
                        .questionSubmission(questionSubmission)
                        .teacherId(currentUser.getId())
                        .feedbackTime(Instant.now())
                        .content(item.getFeedback())
                        .build());
                latestFeedback = item.getFeedback();
            } else {
                latestFeedback = feedbackRepository.findByQuestionSubmission_IdOrderByFeedbackTimeDesc(questionSubmission.getId())
                        .stream()
                        .findFirst()
                        .map(Feedback::getContent)
                        .orElse(null);
            }

            updatedQuestions.add(TeacherEssayGradeUpdatedQuestionResponse.builder()
                    .questionId(question.getId())
                    .earnedPoints(score)
                    .maxPoints(maxPoints)
                    .status(questionSubmission.getStatus())
                    .feedback(latestFeedback)
                    .build());
        }

        List<QuestionSubmission> refreshedQuestionSubmissions = questionSubmissionRepository.findAllByAttemptIdWithQuestion(attemptId);

        BigDecimal totalScore = refreshedQuestionSubmissions.stream()
                .map(QuestionSubmission::getScore)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(3, RoundingMode.HALF_UP);

        attempt.setScore(totalScore);
        assessmentSubmissionRepository.save(attempt);

        String gradingStatus = computeAttemptGradingStatus(refreshedQuestionSubmissions);
        if (STATUS_GRADED.equals(gradingStatus)) {
            assessmentEventPublisher.publishSubmissionGraded(attempt, STATUS_GRADED);
        }

        BigDecimal maxScore = assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessment.getId())
                .stream()
                .map(aq -> defaultPoint(resolveConcreteQuestion(aq.getQuestion())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TeacherEssayGradesResponse.builder()
                .attemptId(attemptId)
                .score(totalScore)
                .maxScore(maxScore)
                .gradingStatus(gradingStatus)
                .updatedQuestions(updatedQuestions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherGradebookResponse getClassGradebook(
            UUID classId,
            CurrentUserInfo currentUser,
            int page,
            int size
    ) {
        assertTeacherCanAccessClass(classId, currentUser);

        List<UUID> allStudentIds = new ArrayList<>(new LinkedHashSet<>(learningInternalEnrollmentClient.getStudentIdsByClassId(classId)));
        allStudentIds.sort(Comparator.comparing(UUID::toString));

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;

        int fromIndex = Math.min(safePage * safeSize, allStudentIds.size());
        int toIndex = Math.min(fromIndex + safeSize, allStudentIds.size());
        List<UUID> pageStudentIds = fromIndex >= toIndex ? List.of() : allStudentIds.subList(fromIndex, toIndex);

        List<Assessment> assessments = assessmentRepository.findByClassIdOrderByCreatedAtAsc(classId);
        List<TeacherGradebookAssessmentColumnResponse> assessmentColumns = assessments.stream()
                .map(assessment -> TeacherGradebookAssessmentColumnResponse.builder()
                        .assessmentId(assessment.getId())
                        .title(assessment.getTitle())
                        .gradingRule(assessment.getGradingRule())
                        .build())
                .toList();

        List<TeacherGradebookRowResponse> rows = List.of();

        if (!pageStudentIds.isEmpty() && !assessments.isEmpty()) {
            List<UUID> assessmentIds = assessments.stream().map(Assessment::getId).toList();

            List<AssessmentSubmission> submissions = assessmentSubmissionRepository
                    .findByStudentIdInAndAssessment_IdInAndStatus(
                            pageStudentIds,
                            assessmentIds,
                            AssessmentSubmissionStatus.SUBMITTED
                    );

            Set<UUID> pendingAttemptIds = findPendingReviewAttemptIds(
                    submissions.stream().map(AssessmentSubmission::getId).toList()
            );

            Map<UUID, Map<UUID, List<AssessmentSubmission>>> submissionsByStudentAndAssessment = submissions.stream()
                    .collect(Collectors.groupingBy(
                            AssessmentSubmission::getStudentId,
                            Collectors.groupingBy(s -> s.getAssessment().getId())
                    ));

            List<TeacherGradebookRowResponse> builtRows = new ArrayList<>();

            for (UUID studentId : pageStudentIds) {
                Map<UUID, List<AssessmentSubmission>> byAssessment = submissionsByStudentAndAssessment
                        .getOrDefault(studentId, Map.of());

                List<TeacherGradebookCellResponse> cells = assessments.stream()
                        .map(assessment -> toGradebookCell(
                                assessment,
                                byAssessment.getOrDefault(assessment.getId(), List.of()),
                                pendingAttemptIds
                        ))
                        .toList();

                BigDecimal averageScore = cells.stream()
                        .map(TeacherGradebookCellResponse::getScore)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                long gradedCount = cells.stream().filter(cell -> cell.getScore() != null).count();
                if (gradedCount > 0) {
                    averageScore = averageScore.divide(BigDecimal.valueOf(gradedCount), 3, RoundingMode.HALF_UP);
                } else {
                    averageScore = null;
                }

                builtRows.add(TeacherGradebookRowResponse.builder()
                        .studentId(studentId)
                        .cells(cells)
                        .averageScore(averageScore)
                        .build());
            }

            rows = builtRows;
        }

        int totalElements = allStudentIds.size();
        int totalPages = safeSize <= 0 ? 0 : (int) Math.ceil(totalElements / (double) safeSize);

        return TeacherGradebookResponse.builder()
                .classId(classId)
                .assessments(assessmentColumns)
                .rows(rows)
                .pageNumber(safePage)
                .pageSize(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(safePage == 0)
                .last(safePage >= Math.max(totalPages - 1, 0))
                .empty(rows.isEmpty())
                .build();
    }

    private TeacherGradebookCellResponse toGradebookCell(
            Assessment assessment,
            List<AssessmentSubmission> attempts,
            Set<UUID> pendingAttemptIds
    ) {
        if (attempts.isEmpty()) {
            return TeacherGradebookCellResponse.builder()
                    .assessmentId(assessment.getId())
                    .status(STATUS_NOT_STARTED)
                    .score(null)
                    .build();
        }

        boolean hasPendingReview = attempts.stream().anyMatch(attempt -> pendingAttemptIds.contains(attempt.getId()));
        if (hasPendingReview) {
            return TeacherGradebookCellResponse.builder()
                    .assessmentId(assessment.getId())
                    .status(STATUS_PENDING_REVIEW)
                    .score(null)
                    .build();
        }

        BigDecimal score = selectScoreByRule(assessment.getGradingRule(), attempts);
        String status = score != null ? STATUS_GRADED : STATUS_NOT_STARTED;

        return TeacherGradebookCellResponse.builder()
                .assessmentId(assessment.getId())
                .status(status)
                .score(score)
                .build();
    }

    private BigDecimal selectScoreByRule(GradingRule gradingRule, List<AssessmentSubmission> attempts) {
        if (attempts.isEmpty()) {
            return null;
        }

        GradingRule rule = gradingRule != null ? gradingRule : GradingRule.LAST_ATTEMPT;

        return switch (rule) {
            case HIGH_SCORE -> attempts.stream()
                    .filter(attempt -> attempt.getScore() != null)
                    .max(Comparator
                            .comparing(AssessmentSubmission::getScore)
                            .thenComparing(AssessmentSubmission::getSubmitTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(AssessmentSubmission::getScore)
                    .orElse(null);
            case LAST_ATTEMPT -> attempts.stream()
                    .max(Comparator
                            .comparing(AssessmentSubmission::getAttemptNo, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(AssessmentSubmission::getSubmitTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(AssessmentSubmission::getScore)
                    .orElse(null);
            case FIRST_ATTEMPT -> attempts.stream()
                    .min(Comparator
                            .comparing(AssessmentSubmission::getAttemptNo, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(AssessmentSubmission::getSubmitTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(AssessmentSubmission::getScore)
                    .orElse(null);
            case AVG_SCORE -> {
                List<BigDecimal> scores = attempts.stream()
                        .map(AssessmentSubmission::getScore)
                        .filter(Objects::nonNull)
                        .toList();
                if (scores.isEmpty()) {
                    yield null;
                }
                BigDecimal total = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                yield total.divide(BigDecimal.valueOf(scores.size()), 3, RoundingMode.HALF_UP);
            }
        };
    }

    private String normalizeSubmissionStatusFilter(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (STATUS_PENDING_REVIEW.equals(normalized) || STATUS_GRADED.equals(normalized)) {
            return normalized;
        }

        throw new BadRequestException("Unsupported status filter: " + status);
    }

    private TeacherSubmissionSummaryResponse toSubmissionSummary(
            AssessmentSubmission attempt,
            Set<UUID> pendingAttemptIds
    ) {
        String gradingStatus = pendingAttemptIds.contains(attempt.getId())
                ? STATUS_PENDING_REVIEW
                : STATUS_GRADED;

        return TeacherSubmissionSummaryResponse.builder()
                .attemptId(attempt.getId())
                .studentId(attempt.getStudentId())
                .attemptNo(attempt.getAttemptNo())
                .submittedAt(attempt.getSubmitTime())
                .score(attempt.getScore())
                .gradingStatus(gradingStatus)
                .build();
    }

    private BigDecimal normalizeScore(BigDecimal score, BigDecimal maxPoints) {
        if (score == null) {
            throw new BadRequestException("score is required");
        }

        if (score.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("score must be >= 0");
        }

        if (score.compareTo(maxPoints) > 0) {
            throw new BadRequestException("score must be <= question max point");
        }

        return score.setScale(3, RoundingMode.HALF_UP);
    }

    private QuestionSubmissionStatus mapEssayStatus(BigDecimal score, BigDecimal maxPoints) {
        if (score.compareTo(BigDecimal.ZERO) == 0) {
            return QuestionSubmissionStatus.INCORRECT;
        }
        if (score.compareTo(maxPoints) == 0) {
            return QuestionSubmissionStatus.CORRECT;
        }
        return QuestionSubmissionStatus.PARTIAL;
    }

    private String computeAttemptGradingStatus(List<QuestionSubmission> questionSubmissions) {
        boolean hasPendingReview = questionSubmissions.stream()
                .anyMatch(qs -> qs.getStatus() == QuestionSubmissionStatus.PENDING_REVIEW);
        return hasPendingReview ? STATUS_PENDING_REVIEW : STATUS_GRADED;
    }

    private Map<UUID, String> loadLatestFeedback(List<UUID> questionSubmissionIds) {
        if (questionSubmissionIds == null || questionSubmissionIds.isEmpty()) {
            return Map.of();
        }

        Map<UUID, String> latestByQuestionSubmissionId = new LinkedHashMap<>();
        for (Feedback feedback : feedbackRepository.findByQuestionSubmission_IdInOrderByFeedbackTimeDesc(questionSubmissionIds)) {
            UUID questionSubmissionId = feedback.getQuestionSubmission().getId();
            latestByQuestionSubmissionId.putIfAbsent(questionSubmissionId, feedback.getContent());
        }
        return latestByQuestionSubmissionId;
    }

    private Set<UUID> findPendingReviewAttemptIds(List<UUID> attemptIds) {
        if (attemptIds == null || attemptIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(questionSubmissionRepository.findAttemptIdsByAttemptIdsAndStatus(
                attemptIds,
                QuestionSubmissionStatus.PENDING_REVIEW
        ));
    }

    private Assessment findAssessment(UUID assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", assessmentId));
    }

    private void assertTeacherCanAccessClass(UUID classId, CurrentUserInfo currentUser) {
        if (currentUser == null || currentUser.getRole() == null) {
            throw new ForbiddenException("Access denied");
        }

        ClassSectionReportMetadataResponse classMetadata = getClassSectionReportMetadataOrThrow(classId);

        String role = currentUser.getRole().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(role)) {
            return;
        }

        if (!"TEACHER".equals(role)) {
            throw new ForbiddenException("Access denied");
        }

        if (classMetadata.getTeacherId() == null || !classMetadata.getTeacherId().equals(currentUser.getId())) {
            throw new ForbiddenException("Teacher is not assigned to this class");
        }
    }

    private ClassSectionReportMetadataResponse getClassSectionReportMetadataOrThrow(UUID classId) {
        try {
            return courseManagementInternalClient.getClassSectionReportMetadata(classId);
        } catch (FeignException ex) {
            if (ex.status() == 404) {
                throw new ResourceNotFoundException("ClassSection", classId);
            }
            throw ex;
        }
    }

    private Question resolveConcreteQuestion(Question question) {
        if (question == null) {
            return null;
        }
        return (Question) Hibernate.unproxy(question);
    }

    private McqSubmission resolveMcqSubmission(QuestionSubmission questionSubmission) {
        if (questionSubmission == null) {
            return null;
        }
        QuestionSubmission concrete = (QuestionSubmission) Hibernate.unproxy(questionSubmission);
        if (concrete instanceof McqSubmission mcqSubmission) {
            return mcqSubmission;
        }
        return mcqSubmissionRepository.findById(concrete.getId()).orElse(null);
    }

    private EssaySubmission resolveEssaySubmission(QuestionSubmission questionSubmission) {
        if (questionSubmission == null) {
            return null;
        }
        QuestionSubmission concrete = (QuestionSubmission) Hibernate.unproxy(questionSubmission);
        if (concrete instanceof EssaySubmission essaySubmission) {
            return essaySubmission;
        }
        return essaySubmissionRepository.findById(concrete.getId()).orElse(null);
    }

    private CodingSubmission resolveCodingSubmission(QuestionSubmission questionSubmission) {
        if (questionSubmission == null) {
            return null;
        }
        QuestionSubmission concrete = (QuestionSubmission) Hibernate.unproxy(questionSubmission);
        if (concrete instanceof CodingSubmission codingSubmission) {
            return codingSubmission;
        }
        return codingSubmissionRepository.findById(concrete.getId()).orElse(null);
    }

    private BigDecimal defaultPoint(Question question) {
        return question.getPoint() != null ? question.getPoint() : BigDecimal.ZERO;
    }

    private BigDecimal nonNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private <T> Page<T> sliceToPage(List<T> items, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;

        int fromIndex = Math.min(safePage * safeSize, items.size());
        int toIndex = Math.min(fromIndex + safeSize, items.size());

        List<T> content = fromIndex >= toIndex ? List.of() : items.subList(fromIndex, toIndex);
        return new PageImpl<>(content, PageRequest.of(safePage, safeSize), items.size());
    }
}
