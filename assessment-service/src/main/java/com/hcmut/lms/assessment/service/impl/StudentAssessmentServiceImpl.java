package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.client.LearningEnrollmentClient;
import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.answer.EssayAcceptedFileType;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.submission.*;
import com.hcmut.lms.assessment.dto.request.student.*;
import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.dto.response.student.*;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.CodeJudgeUnavailableException;
import com.hcmut.lms.assessment.exception.ForbiddenException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.handler.dto.*;
import com.hcmut.lms.assessment.repository.*;
import com.hcmut.lms.assessment.service.AssessmentExecutionService;
import com.hcmut.lms.assessment.service.StudentAssessmentService;
import com.hcmut.lms.assessment.service.judge.CppJudgeService;
import com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation;
import com.hcmut.lms.assessment.service.judge.dto.JudgeVerdict;
import com.hcmut.lms.common.dto.PageResponse;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StudentAssessmentServiceImpl implements StudentAssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentSubmissionRepository assessmentSubmissionRepository;
    private final QuestionSubmissionRepository questionSubmissionRepository;
    private final McqSubmissionRepository mcqSubmissionRepository;
    private final EssaySubmissionRepository essaySubmissionRepository;
    private final CodingSubmissionRepository codingSubmissionRepository;
    private final SubmissionTestCaseResultRepository submissionTestCaseResultRepository;
    private final LearningEnrollmentClient learningEnrollmentClient;
    private final AssessmentExecutionService assessmentExecutionService;
    private final CppJudgeService cppJudgeService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentCourseAssessmentResponse> listCourseAssessments(
            UUID courseId,
            UUID studentId,
            String authorizationHeader,
            int page,
            int size
    ) {
        ensureStudentEnrolled(authorizationHeader, courseId);

        Page<Assessment> assessmentPage = assessmentRepository.findAllByClassId(
                courseId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        List<UUID> assessmentIds = assessmentPage.getContent().stream()
                .map(Assessment::getId)
                .toList();

        final Map<UUID, List<AssessmentSubmission>> submissionsByAssessmentId;
        final Set<UUID> pendingReviewAttemptIds;
        if (!assessmentIds.isEmpty()) {
            List<AssessmentSubmission> submissions = assessmentSubmissionRepository
                    .findAllByStudentIdAndAssessment_IdIn(studentId, assessmentIds)
                    .stream().toList();
            submissionsByAssessmentId = submissions.stream()
                    .collect(Collectors.groupingBy(s -> s.getAssessment().getId()));
            pendingReviewAttemptIds = findPendingReviewAttemptIds(
                    submissions.stream().map(AssessmentSubmission::getId).toList()
            );
        } else {
            submissionsByAssessmentId = Map.of();
            pendingReviewAttemptIds = Set.of();
        }

        Page<StudentCourseAssessmentResponse> mappedPage = assessmentPage.map(assessment -> {
            List<AssessmentSubmission> submissions = submissionsByAssessmentId
                    .getOrDefault(assessment.getId(), List.of());
            return toCourseAssessmentResponse(assessment, submissions, pendingReviewAttemptIds);
        });

        return PageResponse.fromPage(mappedPage);
    }

    @Override
    public StartAttemptResponse startAttempt(UUID assessmentId, UUID studentId, String authorizationHeader) {
        Assessment assessment = findAssessment(assessmentId);
        ensureAssessmentCanStart(assessment);
        ensureStudentEnrolled(authorizationHeader, assessment.getClassId());

        Optional<AssessmentSubmission> inProgressOpt = assessmentSubmissionRepository
                .findFirstByAssessment_IdAndStudentIdAndStatusOrderByCreatedAtDesc(
                        assessmentId,
                        studentId,
                        AssessmentSubmissionStatus.IN_PROGRESS
                );

        if (inProgressOpt.isPresent()) {
            AssessmentSubmission inProgress = inProgressOpt.get();
            if (!isAttemptExpired(inProgress, assessment)) {
                return toStartAttemptResponse(inProgress, assessment, true);
            }
            submitAttempt(inProgress.getId(), studentId);
        }

        int maxAttemptNo = assessmentSubmissionRepository.findMaxAttemptNo(assessmentId, studentId);
        int nextAttemptNo = maxAttemptNo + 1;

        if (assessment.getMaxAttempts() > 0 && nextAttemptNo > assessment.getMaxAttempts()) {
            throw new BadRequestException("Maximum attempt limit reached for this assessment");
        }

        AssessmentSubmission created = assessmentSubmissionRepository.saveAndFlush(
                AssessmentSubmission.builder()
                        .assessment(assessment)
                        .studentId(studentId)
                        .attemptNo(nextAttemptNo)
                        .status(AssessmentSubmissionStatus.IN_PROGRESS)
                        .build()
        );

        return toStartAttemptResponse(created, assessment, false);
    }

    @Override
    @Transactional(readOnly = true)
    public AttemptDetailResponse getAttemptDetail(UUID attemptId, UUID studentId) {
        AssessmentSubmission attempt = findOwnedAttempt(attemptId, studentId);
        Assessment assessment = attempt.getAssessment();

        Map<UUID, QuestionSubmission> submissionByQuestionId = questionSubmissionRepository
                .findAllByAttemptIdWithQuestion(attemptId)
                .stream()
                .collect(Collectors.toMap(s -> s.getQuestion().getId(), s -> s, (a, b) -> a));

        List<AttemptQuestionResponse> questions = assessmentQuestionRepository
                .findByAssessmentIdOrderByIndex(assessment.getId())
                .stream()
                .map(aq -> toAttemptQuestionResponse(aq, submissionByQuestionId.get(aq.getQuestion().getId())))
                .toList();

        return AttemptDetailResponse.builder()
                .attemptId(attempt.getId())
                .assessmentId(assessment.getId())
                .assessmentTitle(assessment.getTitle())
                .attemptNo(attempt.getAttemptNo())
                .status(attempt.getStatus())
                .startedAt(attempt.getCreatedAt())
                .expiresAt(computeAttemptExpiresAt(attempt, assessment))
                .submittedAt(attempt.getSubmitTime())
                .takenTime(attempt.getTakenTime())
                .timeLimit(assessment.getTimeLimit())
                .questions(questions)
                .build();
    }

    @Override
    public SaveAnswerResponse saveAnswer(UUID attemptId, UUID questionId, UUID studentId, SaveAnswerRequest request) {
        if (request == null || request.getQuestionType() == null) {
            throw new BadRequestException("questionType is required");
        }

        AssessmentSubmission attempt = findOwnedAttempt(attemptId, studentId);
        ensureAttemptInProgress(attempt);
        ensureAttemptWritable(attempt);

        UUID assessmentId = attempt.getAssessment().getId();
        if (!assessmentQuestionRepository.existsByAssessment_IdAndQuestion_Id(assessmentId, questionId)) {
            throw new BadRequestException("Question does not belong to this assessment");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        question = (Question) Hibernate.unproxy(question);

        if (question.getQuestionType() != request.getQuestionType()) {
            throw new BadRequestException("questionType does not match the target question");
        }

        Instant savedAt;
        switch (request.getQuestionType()) {
            case MCQ -> savedAt = saveMcqAnswer(attempt, question, (SaveMcqAnswerRequest) request);
            case ESSAY -> savedAt = saveEssayAnswer(attempt, question, (SaveEssayAnswerRequest) request);
            case CODING -> savedAt = saveCodingAnswer(attempt, question, (SaveCodingAnswerRequest) request);
            default -> throw new BadRequestException("Unsupported questionType");
        }

        return SaveAnswerResponse.builder()
                .attemptId(attemptId)
                .questionId(questionId)
                .savedAt(savedAt)
                .build();
    }

    @Override
    public SubmitAttemptResponse submitAttempt(UUID attemptId, UUID studentId) {
        AssessmentSubmission attempt = assessmentSubmissionRepository
                .findByIdForUpdate(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("AssessmentSubmission", attemptId));
        if (!attempt.getStudentId().equals(studentId)) {
            throw new ForbiddenException("You are not allowed to access this attempt");
        }

        if (attempt.getStatus() == AssessmentSubmissionStatus.SUBMITTED) {
            return buildSubmittedResponse(attempt);
        }

        ensureAttemptInProgress(attempt);

        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByIndex(attempt.getAssessment().getId());

        Map<UUID, QuestionSubmission> submissionByQuestionId = questionSubmissionRepository
                .findAllByAttemptIdWithQuestion(attemptId)
                .stream()
                .collect(Collectors.toMap(s -> s.getQuestion().getId(), s -> s, (a, b) -> a));

        List<SubmitQuestionResultResponse> questionResults = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal maxScore = BigDecimal.ZERO;
        boolean hasPendingReview = false;

        for (AssessmentQuestion aq : assessmentQuestions) {
            Question question = resolveConcreteQuestion(aq.getQuestion());
            maxScore = maxScore.add(defaultPoint(question));
            if (question instanceof CodingQuestion codingQuestion) {
                validateCodingQuestionConfiguration(codingQuestion);
            }

            QuestionSubmission stored = submissionByQuestionId.get(question.getId());
            if (stored == null) {
                QuestionSubmission unanswered;
                if (question.getQuestionType() == QuestionType.CODING && question instanceof CodingQuestion codingQuestion) {
                    unanswered = CodingSubmission.builder()
                            .assessmentSubmission(attempt)
                            .question(question)
                            .score(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP))
                            .status(QuestionSubmissionStatus.INCORRECT)
                            .passedTestcases(0)
                            .totalTestcases(codingQuestion.getTestCases() == null ? 0 : codingQuestion.getTestCases().size())
                            .build();
                } else {
                    unanswered = QuestionSubmission.builder()
                            .assessmentSubmission(attempt)
                            .question(question)
                            .score(BigDecimal.ZERO)
                            .status(QuestionSubmissionStatus.INCORRECT)
                            .build();
                }
                questionSubmissionRepository.save(unanswered);

                questionResults.add(SubmitQuestionResultResponse.builder()
                        .questionId(question.getId())
                        .earnedPoints(BigDecimal.ZERO)
                        .maxPoints(defaultPoint(question))
                        .status(QuestionSubmissionStatus.INCORRECT)
                        .detail("Question is not answered")
                        .build());
                continue;
            }

            GradingResponse grading = gradeStoredSubmission(question, stored, studentId);
            QuestionSubmissionStatus mappedStatus = mapStatus(grading.getStatus());

            stored.setScore(grading.getEarnedPoints());
            stored.setStatus(mappedStatus);
            questionSubmissionRepository.save(stored);

            if (mappedStatus == QuestionSubmissionStatus.PENDING_REVIEW) {
                hasPendingReview = true;
            }

            BigDecimal earned = nonNull(grading.getEarnedPoints());
            totalScore = totalScore.add(earned);

            questionResults.add(SubmitQuestionResultResponse.builder()
                    .questionId(question.getId())
                    .earnedPoints(earned)
                    .maxPoints(nonNull(grading.getMaxPoints()))
                    .status(mappedStatus)
                    .detail(grading.getDetail())
                    .build());
        }

        attempt.setStatus(AssessmentSubmissionStatus.SUBMITTED);
        attempt.setSubmitTime(Instant.now());
        attempt.setTakenTime(computeTakenTimeInSeconds(attempt));
        attempt.setScore(totalScore);
        assessmentSubmissionRepository.save(attempt);

        return SubmitAttemptResponse.builder()
                .attemptId(attempt.getId())
                .assessmentId(attempt.getAssessment().getId())
                .attemptNo(attempt.getAttemptNo())
                .status(attempt.getStatus())
                .submittedAt(attempt.getSubmitTime())
                .takenTime(attempt.getTakenTime())
                .score(totalScore)
                .maxScore(maxScore)
                .gradingStatus(hasPendingReview ? "PENDING_REVIEW" : "GRADED")
                .questionResults(questionResults)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAttemptSummaryResponse> getMyAttempts(UUID assessmentId, UUID studentId, String authorizationHeader) {
        Assessment assessment = findAssessment(assessmentId);
        ensureStudentEnrolled(authorizationHeader, assessment.getClassId());

        List<AssessmentSubmission> attempts = assessmentSubmissionRepository
                .findAllByAssessment_IdAndStudentIdOrderByAttemptNoDesc(assessmentId, studentId);
        Set<UUID> pendingReviewAttemptIds = findPendingReviewAttemptIds(
                attempts.stream().map(AssessmentSubmission::getId).toList()
        );

        return attempts
                .stream()
                .map(attempt -> toAttemptSummaryResponse(
                        attempt,
                        pendingReviewAttemptIds.contains(attempt.getId())
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubmitAttemptResponse getAttemptResult(UUID attemptId, UUID studentId) {
        AssessmentSubmission attempt = findOwnedAttempt(attemptId, studentId);
        if (attempt.getStatus() != AssessmentSubmissionStatus.SUBMITTED) {
            throw new BadRequestException("Attempt is not submitted yet");
        }
        return buildSubmittedResponse(attempt);
    }

    private Instant saveMcqAnswer(AssessmentSubmission attempt, Question question, SaveMcqAnswerRequest request) {
        if (!(question instanceof McqQuestion mcqQuestion)) {
            throw new BadRequestException("Question type mismatch for MCQ answer");
        }

        removeIncompatibleSubmission(attempt.getId(), question.getId(), McqSubmission.class);

        McqSubmission submission = mcqSubmissionRepository
                .findByAssessmentSubmission_IdAndQuestion_Id(attempt.getId(), question.getId())
                .orElseGet(() -> McqSubmission.builder()
                        .assessmentSubmission(attempt)
                        .question(question)
                        .build());

        List<UUID> selectedOptionIds = request.getSelectedOptionIds() == null
                ? List.of()
                : request.getSelectedOptionIds().stream().filter(Objects::nonNull).distinct().toList();

        Map<UUID, AnswerOption> optionsById = mcqQuestion.getAnswerOptions().stream()
                .collect(Collectors.toMap(AnswerOption::getId, option -> option));

        for (UUID optionId : selectedOptionIds) {
            if (!optionsById.containsKey(optionId)) {
                throw new BadRequestException("Invalid option id: " + optionId);
            }
        }

        submission.getSelectedOptions().clear();
        for (UUID optionId : selectedOptionIds) {
            submission.addSelectedOption(AnswerOptionMcqSubmission.builder()
                    .option(optionsById.get(optionId))
                    .build());
        }

        submission.setSelectedCount(selectedOptionIds.size());
        submission.setScore(null);
        submission.setStatus(QuestionSubmissionStatus.PENDING);

        McqSubmission saved = mcqSubmissionRepository.save(submission);
        return saved.getUpdatedAt();
    }

    private Instant saveEssayAnswer(AssessmentSubmission attempt, Question question, SaveEssayAnswerRequest request) {
        removeIncompatibleSubmission(attempt.getId(), question.getId(), EssaySubmission.class);

        EssaySubmission submission = essaySubmissionRepository
                .findByAssessmentSubmission_IdAndQuestion_Id(attempt.getId(), question.getId())
                .orElseGet(() -> EssaySubmission.builder()
                        .assessmentSubmission(attempt)
                        .question(question)
                        .build());

        submission.setAnswerText(request.getTextContent());
        submission.setAnswerFileUrl(request.getFileUrl());
        submission.setFileFormat(request.getFileFormat());
        submission.setNumPages(request.getNumPages());
        submission.setWordCount(request.getWordCount());
        submission.setScore(null);
        submission.setStatus(QuestionSubmissionStatus.PENDING);

        EssaySubmission saved = essaySubmissionRepository.save(submission);
        return saved.getUpdatedAt();
    }

    private Instant saveCodingAnswer(AssessmentSubmission attempt, Question question, SaveCodingAnswerRequest request) {
        removeIncompatibleSubmission(attempt.getId(), question.getId(), CodingSubmission.class);
        validateSupportedCodingLanguageForSubmission(request.getCode(), request.getLanguage());

        CodingSubmission submission = codingSubmissionRepository
                .findByAssessmentSubmission_IdAndQuestion_Id(attempt.getId(), question.getId())
                .orElseGet(() -> CodingSubmission.builder()
                        .assessmentSubmission(attempt)
                        .question(question)
                        .build());

        submission.setInputCode(request.getCode());
        submission.setExecutionLanguage(request.getLanguage());
        submission.setScore(null);
        submission.setStatus(QuestionSubmissionStatus.PENDING);

        CodingSubmission saved = codingSubmissionRepository.save(submission);
        return saved.getUpdatedAt();
    }

    private AttemptQuestionResponse toAttemptQuestionResponse(AssessmentQuestion assessmentQuestion, QuestionSubmission submission) {
        Question question = (Question) Hibernate.unproxy(assessmentQuestion.getQuestion());
        QuestionSubmission concreteSubmission = resolveConcreteSubmission(submission);

        AttemptQuestionResponse.AttemptQuestionResponseBuilder builder = AttemptQuestionResponse.builder()
                .questionId(question.getId())
                .orderIndex(assessmentQuestion.getOrderIndex())
                .questionType(question.getQuestionType())
                .content(question.getContent())
                .point(defaultPoint(question))
                .required(question.isRequired());

        switch (question.getQuestionType()) {
            case MCQ -> {
                McqQuestion mcqQuestion = (McqQuestion) question;
                builder.allowMultiAnswer(mcqQuestion.isAllowMultiAnswer());
                builder.options(mcqQuestion.getAnswerOptions().stream()
                        .sorted(Comparator.comparingInt(AnswerOption::getOrderIndex))
                        .map(option -> AttemptQuestionOptionResponse.builder()
                                .id(option.getId())
                                .content(option.getContent())
                                .orderIndex(option.getOrderIndex())
                                .build())
                        .toList());

                if (concreteSubmission instanceof McqSubmission mcqSubmission) {
                    builder.selectedOptionIds(
                            mcqSubmission.getSelectedOptions().stream()
                                    .map(item -> item.getOption().getId())
                                    .toList()
                    );
                }
            }
            case CODING -> {
                CodingQuestion codingQuestion = (CodingQuestion) question;
                builder.problemDescription(codingQuestion.getProblemDescription());
                builder.executionTimeLimit(codingQuestion.getExecutionTimeLimit());
                builder.executionMemoryLimit(codingQuestion.getExecutionMemoryLimit());
                builder.language(codingQuestion.getLanguage());
                builder.initialCode(codingQuestion.getInitialCode());

                if (concreteSubmission instanceof CodingSubmission codingSubmission) {
                    builder.submittedCode(codingSubmission.getInputCode());
                    builder.submittedLanguage(codingSubmission.getExecutionLanguage());
                }
            }
            case ESSAY -> {
                EssayQuestion essayQuestion = (EssayQuestion) question;
                builder.maxFileSize(essayQuestion.getMaxFileSize());
                builder.acceptedFileTypes(essayQuestion.getAcceptedFileTypes().stream()
                        .map(EssayAcceptedFileType::getFileType)
                        .toList());

                if (concreteSubmission instanceof EssaySubmission essaySubmission) {
                    builder.submittedText(essaySubmission.getAnswerText());
                    builder.submittedFileUrl(essaySubmission.getAnswerFileUrl());
                    builder.submittedFileFormat(essaySubmission.getFileFormat());
                    builder.submittedNumPages(essaySubmission.getNumPages());
                    builder.submittedWordCount(essaySubmission.getWordCount());
                }
            }
        }

        return builder.build();
    }

    private void removeIncompatibleSubmission(UUID attemptId, UUID questionId, Class<?> expectedType) {
        questionSubmissionRepository.findByAssessmentSubmission_IdAndQuestion_Id(attemptId, questionId)
                .ifPresent(existing -> {
                    if (!expectedType.isInstance(existing)) {
                        questionSubmissionRepository.delete(existing);
                        questionSubmissionRepository.flush();
                    }
                });
    }

    private SubmitAttemptResponse buildSubmittedResponse(AssessmentSubmission attempt) {
        List<AssessmentQuestion> assessmentQuestions = assessmentQuestionRepository
                .findByAssessmentIdOrderByIndex(attempt.getAssessment().getId());

        Map<UUID, QuestionSubmission> submissionByQuestionId = questionSubmissionRepository
                .findAllByAttemptIdWithQuestion(attempt.getId())
                .stream()
                .collect(Collectors.toMap(s -> s.getQuestion().getId(), s -> s, (a, b) -> a));

        List<SubmitQuestionResultResponse> questionResults = new ArrayList<>();
        BigDecimal maxScore = BigDecimal.ZERO;
        boolean hasPendingReview = false;

        for (AssessmentQuestion aq : assessmentQuestions) {
            Question question = aq.getQuestion();
            maxScore = maxScore.add(defaultPoint(question));

            QuestionSubmission submission = submissionByQuestionId.get(question.getId());
            if (submission == null) {
                questionResults.add(SubmitQuestionResultResponse.builder()
                        .questionId(question.getId())
                        .earnedPoints(BigDecimal.ZERO)
                        .maxPoints(defaultPoint(question))
                        .status(QuestionSubmissionStatus.INCORRECT)
                        .detail("Question is not answered")
                        .build());
                continue;
            }

            if (submission.getStatus() == QuestionSubmissionStatus.PENDING_REVIEW) {
                hasPendingReview = true;
            }

            String detail = null;
            if (question.getQuestionType() == QuestionType.CODING) {
                CodingSubmission codingSubmission = resolveCodingSubmission(submission);
                detail = summarizeCodingSubmissionDetail(codingSubmission);
            }

            questionResults.add(SubmitQuestionResultResponse.builder()
                    .questionId(question.getId())
                    .earnedPoints(nonNull(submission.getScore()))
                    .maxPoints(defaultPoint(question))
                    .status(submission.getStatus())
                    .detail(detail)
                    .build());
        }

        return SubmitAttemptResponse.builder()
                .attemptId(attempt.getId())
                .assessmentId(attempt.getAssessment().getId())
                .attemptNo(attempt.getAttemptNo())
                .status(attempt.getStatus())
                .submittedAt(attempt.getSubmitTime())
                .takenTime(attempt.getTakenTime())
                .score(nonNull(attempt.getScore()))
                .maxScore(maxScore)
                .gradingStatus(hasPendingReview ? "PENDING_REVIEW" : "GRADED")
                .questionResults(questionResults)
                .build();
    }

    private StudentAttemptSummaryResponse toAttemptSummaryResponse(
            AssessmentSubmission attempt,
            boolean hasPendingReview
    ) {
        String gradingStatus = switch (attempt.getStatus()) {
            case IN_PROGRESS -> "IN_PROGRESS";
            case SUBMITTED -> hasPendingReview ? "PENDING_REVIEW" : "GRADED";
        };

        return StudentAttemptSummaryResponse.builder()
                .attemptId(attempt.getId())
                .attemptNo(attempt.getAttemptNo())
                .status(attempt.getStatus())
                .score(nonNull(attempt.getScore()))
                .startedAt(attempt.getCreatedAt())
                .submittedAt(attempt.getSubmitTime())
                .takenTime(attempt.getTakenTime())
                .gradingStatus(gradingStatus)
                .build();
    }

    private StudentCourseAssessmentResponse toCourseAssessmentResponse(
            Assessment assessment,
            List<AssessmentSubmission> submissions,
            Set<UUID> pendingReviewAttemptIds
    ) {
        int attemptsUsed = submissions.size();
        BigDecimal bestScore = submissions.stream()
                .map(AssessmentSubmission::getScore)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        boolean hasInProgress = submissions.stream()
                .anyMatch(s -> s.getStatus() == AssessmentSubmissionStatus.IN_PROGRESS);

        String myStatus;
        if (hasInProgress) {
            myStatus = "IN_PROGRESS";
        } else if (submissions.isEmpty()) {
            myStatus = "NOT_STARTED";
        } else if (submissions.stream().anyMatch(s -> pendingReviewAttemptIds.contains(s.getId()))) {
            myStatus = "PENDING_REVIEW";
        } else {
            myStatus = "SUBMITTED";
        }

        boolean hasRemainingAttempts = assessment.getMaxAttempts() <= 0 || attemptsUsed < assessment.getMaxAttempts();
        boolean canStart = isAssessmentOpenNow(assessment) && (hasInProgress || hasRemainingAttempts);

        return StudentCourseAssessmentResponse.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .assessmentType(assessment.getAssessmentType())
                .assessmentStatus(assessment.getAssessmentStatus())
                .startTime(assessment.getStartTime())
                .closeTime(assessment.getCloseTime())
                .maxAttempts(assessment.getMaxAttempts())
                .timeLimit(assessment.getTimeLimit())
                .attemptsUsed(attemptsUsed)
                .bestScore(bestScore)
                .myStatus(myStatus)
                .canStart(canStart)
                .build();
    }

    private GradingResponse gradeStoredSubmission(Question question, QuestionSubmission submission, UUID studentId) {
        question = resolveConcreteQuestion(question);

        if (question.getQuestionType() == QuestionType.CODING) {
            CodingSubmission codingSubmission = resolveCodingSubmission(submission);
            return gradeCodingSubmission((CodingQuestion) question, codingSubmission);
        }

        if (question.getQuestionType() == QuestionType.ESSAY) {
            EssaySubmission essaySubmission = resolveEssaySubmission(submission);
            if (essaySubmission == null || !hasEssayContent(essaySubmission)) {
                return zeroScoreResponse(question, "Question is not answered");
            }

            return GradingResponse.builder()
                    .questionId(question.getId())
                    .earnedPoints(BigDecimal.ZERO)
                    .maxPoints(defaultPoint(question))
                    .status(GradingStatus.PENDING_REVIEW)
                    .detail("Essay submitted. Awaiting instructor review.")
                    .build();
        }

        SubmissionDto submissionDto = toSubmissionDto(question, submission, studentId);

        if (submissionDto == null) {
            return zeroScoreResponse(question, "Question is not answered");
        }

        try {
            return assessmentExecutionService.submitAnswer(question.getId(), submissionDto);
        } catch (IllegalArgumentException ex) {
            return zeroScoreResponse(question, ex.getMessage());
        } catch (RuntimeException ex) {
            log.warn("Auto grading failed for question {}. Fallback zero score. Cause: {}",
                    question.getId(), ex.getMessage(), ex);
            return zeroScoreResponse(question, "Unable to grade automatically at this time");
        }
    }

    private GradingResponse gradeCodingSubmission(CodingQuestion question, CodingSubmission submission) {
        validateCodingQuestionConfiguration(question);

        if (submission == null || submission.getInputCode() == null || submission.getInputCode().isBlank()) {
            persistCodingSummaryOnly(submission, question, 0);
            return zeroScoreResponse(question, "Question is not answered");
        }

        if (!cppJudgeService.isSupportedLanguage(submission.getExecutionLanguage())) {
            throw new BadRequestException(
                    "Unsupported language. Supported: " + cppJudgeService.supportedLanguagesDescription()
            );
        }

        try {
            CodingJudgeEvaluation evaluation = cppJudgeService.evaluate(
                    question,
                    submission.getInputCode(),
                    submission.getExecutionLanguage()
            );
            persistCodingJudgeResults(submission, evaluation);

            BigDecimal maxPoints = defaultPoint(question);
            BigDecimal earnedPoints = calculateCodingEarnedPoints(
                    maxPoints,
                    evaluation.getPassedCount(),
                    evaluation.getTotalCount()
            );

            return GradingResponse.builder()
                    .questionId(question.getId())
                    .earnedPoints(earnedPoints)
                    .maxPoints(maxPoints)
                    .status(mapCodingGradingStatus(evaluation.getPassedCount(), evaluation.getTotalCount()))
                    .detail(evaluation.getDetail())
                    .build();
        } catch (CodeJudgeUnavailableException ex) {
            log.error("Coding judge infrastructure unavailable for question {} submission {}: {}",
                    question.getId(), submission.getId(), ex.getMessage(), ex);
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Coding judge failed unexpectedly for question {} submission {}",
                    question.getId(), submission.getId(), ex);
            throw new CodeJudgeUnavailableException("Code judge is unavailable at this time", ex);
        }
    }

    private boolean hasEssayContent(EssaySubmission submission) {
        boolean hasText = submission.getAnswerText() != null && !submission.getAnswerText().isBlank();
        boolean hasFile = submission.getAnswerFileUrl() != null && !submission.getAnswerFileUrl().isBlank();
        return hasText || hasFile;
    }

    private SubmissionDto toSubmissionDto(Question question, QuestionSubmission stored, UUID studentId) {
        return switch (question.getQuestionType()) {
            case MCQ -> {
                McqSubmission mcqSubmission = resolveMcqSubmission(stored);
                if (mcqSubmission == null) {
                    yield null;
                }
                List<UUID> optionIds = mcqSubmission.getSelectedOptions().stream()
                        .map(option -> option.getOption().getId())
                        .toList();
                yield McqSubmissionDto.builder()
                        .questionType(QuestionType.MCQ)
                        .questionId(question.getId())
                        .studentId(studentId)
                        .selectedOptionIds(optionIds)
                        .build();
            }
            case ESSAY -> {
                EssaySubmission essaySubmission = resolveEssaySubmission(stored);
                if (essaySubmission == null) {
                    yield null;
                }
                yield EssaySubmissionDto.builder()
                        .questionType(QuestionType.ESSAY)
                        .questionId(question.getId())
                        .studentId(studentId)
                        .textContent(essaySubmission.getAnswerText())
                        .fileUrl(essaySubmission.getAnswerFileUrl())
                        .build();
            }
            case CODING -> {
                CodingSubmission codingSubmission = resolveCodingSubmission(stored);
                if (codingSubmission == null) {
                    yield null;
                }
                yield CodingSubmissionDto.builder()
                        .questionType(QuestionType.CODING)
                        .questionId(question.getId())
                        .studentId(studentId)
                        .code(codingSubmission.getInputCode())
                        .language(codingSubmission.getExecutionLanguage())
                        .build();
            }
        };
    }

    private QuestionSubmission resolveConcreteSubmission(QuestionSubmission submission) {
        if (submission == null) {
            return null;
        }
        return (QuestionSubmission) Hibernate.unproxy(submission);
    }

    private Question resolveConcreteQuestion(Question question) {
        if (question == null) {
            return null;
        }
        return (Question) Hibernate.unproxy(question);
    }

    private McqSubmission resolveMcqSubmission(QuestionSubmission stored) {
        QuestionSubmission concrete = resolveConcreteSubmission(stored);
        if (concrete == null) {
            return null;
        }
        if (concrete instanceof McqSubmission mcqSubmission) {
            return mcqSubmission;
        }
        return mcqSubmissionRepository.findById(concrete.getId()).orElse(null);
    }

    private EssaySubmission resolveEssaySubmission(QuestionSubmission stored) {
        QuestionSubmission concrete = resolveConcreteSubmission(stored);
        if (concrete == null) {
            return null;
        }
        if (concrete instanceof EssaySubmission essaySubmission) {
            return essaySubmission;
        }
        return essaySubmissionRepository.findById(concrete.getId()).orElse(null);
    }

    private CodingSubmission resolveCodingSubmission(QuestionSubmission stored) {
        QuestionSubmission concrete = resolveConcreteSubmission(stored);
        if (concrete == null) {
            return null;
        }
        if (concrete instanceof CodingSubmission codingSubmission) {
            return codingSubmission;
        }
        return codingSubmissionRepository.findById(concrete.getId()).orElse(null);
    }

    private void validateCodingQuestionConfiguration(CodingQuestion question) {
        if (question.getTestCases() == null || question.getTestCases().isEmpty()) {
            throw new BadRequestException("Coding question configuration is invalid: missing test cases");
        }
        if (question.getExecutionTimeLimit() <= 0) {
            throw new BadRequestException("Coding question configuration is invalid: executionTimeLimit must be > 0");
        }
        if (question.getExecutionMemoryLimit() <= 0) {
            throw new BadRequestException("Coding question configuration is invalid: executionMemoryLimit must be > 0");
        }
    }

    private void validateSupportedCodingLanguageForSubmission(String code, String language) {
        if (code == null || code.isBlank()) {
            return;
        }
        if (!cppJudgeService.isSupportedLanguage(language)) {
            throw new BadRequestException("Unsupported language. Supported: " + cppJudgeService.supportedLanguagesDescription());
        }
    }

    private void persistCodingSummaryOnly(CodingSubmission submission, CodingQuestion question, int passedCount) {
        if (submission == null) {
            return;
        }

        int totalCount = question.getTestCases() == null ? 0 : question.getTestCases().size();
        submission.setPassedTestcases(Math.max(0, passedCount));
        submission.setTotalTestcases(totalCount);
        codingSubmissionRepository.save(submission);
        submissionTestCaseResultRepository.deleteByCodingSubmission_Id(submission.getId());
    }

    private void persistCodingJudgeResults(CodingSubmission submission, CodingJudgeEvaluation evaluation) {
        submission.setPassedTestcases(evaluation.getPassedCount());
        submission.setTotalTestcases(evaluation.getTotalCount());
        codingSubmissionRepository.save(submission);

        submissionTestCaseResultRepository.deleteByCodingSubmission_Id(submission.getId());

        List<SubmissionTestCaseResult> detailResults = new ArrayList<>();
        for (var testCaseResult : evaluation.getTestCaseResults()) {
            detailResults.add(SubmissionTestCaseResult.builder()
                    .codingSubmission(submission)
                    .testCaseId(testCaseResult.getTestCaseId())
                    .pass(testCaseResult.isPass())
                    .verdict(testCaseResult.getVerdict().name())
                    .detailError(trimDetailForStudent(testCaseResult.getError()))
                    .output(trimDetailForStudent(testCaseResult.getOutput()))
                    .executionTimeMs(testCaseResult.getExecutionTimeMs())
                    .build());
        }

        if (!detailResults.isEmpty()) {
            submissionTestCaseResultRepository.saveAll(detailResults);
        }
    }

    private BigDecimal calculateCodingEarnedPoints(BigDecimal maxPoints, int passedCount, int totalCount) {
        if (totalCount <= 0 || passedCount <= 0) {
            return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        }
        return maxPoints.multiply(BigDecimal.valueOf(passedCount))
                .divide(BigDecimal.valueOf(totalCount), 3, RoundingMode.HALF_UP);
    }

    private GradingStatus mapCodingGradingStatus(int passedCount, int totalCount) {
        if (totalCount <= 0 || passedCount == 0) {
            return GradingStatus.INCORRECT;
        }
        if (passedCount == totalCount) {
            return GradingStatus.CORRECT;
        }
        return GradingStatus.PARTIAL;
    }

    private String summarizeCodingSubmissionDetail(CodingSubmission codingSubmission) {
        if (codingSubmission == null) {
            return null;
        }
        if (codingSubmission.getInputCode() == null || codingSubmission.getInputCode().isBlank()) {
            return "Question is not answered";
        }
        if (!cppJudgeService.isSupportedLanguage(codingSubmission.getExecutionLanguage())) {
            return "Unsupported language. Supported: " + cppJudgeService.supportedLanguagesDescription();
        }

        List<SubmissionTestCaseResult> testCaseResults = submissionTestCaseResultRepository
                .findAllByCodingSubmission_Id(codingSubmission.getId());

        Optional<SubmissionTestCaseResult> compileError = testCaseResults.stream()
                .filter(result -> JudgeVerdict.CE.name().equals(result.getVerdict()))
                .findFirst();

        if (compileError.isPresent()) {
            String error = compileError.get().getDetailError();
            if (error != null && !error.isBlank()) {
                return trimDetailForStudent(error);
            }
            return "Compile error";
        }

        int passed = codingSubmission.getPassedTestcases() == null ? 0 : codingSubmission.getPassedTestcases();
        int total = codingSubmission.getTotalTestcases() == null ? 0 : codingSubmission.getTotalTestcases();
        return "Passed " + passed + "/" + total + " testcases";
    }

    private String trimDetailForStudent(String detail) {
        if (detail == null || detail.isBlank()) {
            return null;
        }
        String normalized = detail.strip();
        final int maxLength = 600;
        return normalized.length() > maxLength
                ? normalized.substring(0, maxLength) + "...(truncated)"
                : normalized;
    }

    private GradingResponse zeroScoreResponse(Question question, String detail) {
        return GradingResponse.builder()
                .questionId(question.getId())
                .earnedPoints(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP))
                .maxPoints(defaultPoint(question))
                .status(GradingStatus.INCORRECT)
                .detail(detail)
                .build();
    }

    private QuestionSubmissionStatus mapStatus(GradingStatus status) {
        return switch (status) {
            case CORRECT -> QuestionSubmissionStatus.CORRECT;
            case PARTIAL -> QuestionSubmissionStatus.PARTIAL;
            case INCORRECT -> QuestionSubmissionStatus.INCORRECT;
            case PENDING_REVIEW -> QuestionSubmissionStatus.PENDING_REVIEW;
        };
    }

    private Assessment findAssessment(UUID assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", assessmentId));
    }

    private AssessmentSubmission findOwnedAttempt(UUID attemptId, UUID studentId) {
        AssessmentSubmission submission = assessmentSubmissionRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("AssessmentSubmission", attemptId));

        if (!submission.getStudentId().equals(studentId)) {
            throw new ForbiddenException("You are not allowed to access this attempt");
        }

        return submission;
    }

    private void ensureStudentEnrolled(String authorizationHeader, UUID classId) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ForbiddenException("Authorization header is required");
        }

        try {
            Boolean enrolled = learningEnrollmentClient.checkEnrollment(authorizationHeader, classId);
            if (!Boolean.TRUE.equals(enrolled)) {
                throw new ForbiddenException("Student is not enrolled in this course");
            }
        } catch (ForbiddenException ex) {
            throw ex;
        } catch (RetryableException ex) {
            // Do not block student assessment flow when inter-service check is temporarily unavailable.
            // We keep hard deny only when learning-service explicitly returns false.
            log.warn("Enrollment check unavailable for classId {}. Fallback allow. Cause: {}", classId, ex.getMessage());
        } catch (FeignException ex) {
            if (ex.status() >= 500 || ex.status() == -1) {
                log.warn("Enrollment service error for classId {} (status {}). Fallback allow. Cause: {}",
                        classId, ex.status(), ex.getMessage());
                return;
            }
            throw new ForbiddenException("Enrollment verification failed");
        } catch (Exception ex) {
            log.error("Enrollment check failed unexpectedly for classId {}. Deny by default.", classId, ex);
            throw new ForbiddenException("Enrollment verification failed");
        }
    }

    private void ensureAssessmentCanStart(Assessment assessment) {
        if (assessment.getAssessmentStatus() != AssessmentStatus.PUBLISHED) {
            throw new BadRequestException("Assessment is not open");
        }

        LocalDateTime now = LocalDateTime.now();
        if (assessment.getStartTime() != null && now.isBefore(assessment.getStartTime())) {
            throw new BadRequestException("Assessment has not started yet");
        }

        if (assessment.getCloseTime() != null && now.isAfter(assessment.getCloseTime())) {
            throw new BadRequestException("Assessment has already closed");
        }
    }

    private boolean isAssessmentOpenNow(Assessment assessment) {
        if (assessment.getAssessmentStatus() != AssessmentStatus.PUBLISHED) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (assessment.getStartTime() != null && now.isBefore(assessment.getStartTime())) {
            return false;
        }
        if (assessment.getCloseTime() != null && now.isAfter(assessment.getCloseTime())) {
            return false;
        }
        return true;
    }

    private void ensureAttemptInProgress(AssessmentSubmission attempt) {
        if (attempt.getStatus() != AssessmentSubmissionStatus.IN_PROGRESS) {
            throw new BadRequestException("Attempt is already submitted");
        }
    }

    private void ensureAttemptWritable(AssessmentSubmission attempt) {
        Assessment assessment = attempt.getAssessment();

        if (assessment.getCloseTime() != null && LocalDateTime.now().isAfter(assessment.getCloseTime())) {
            throw new BadRequestException("Assessment has already closed");
        }

        if (isAttemptExpired(attempt, assessment)) {
            throw new BadRequestException("Attempt has expired. Please submit your attempt");
        }
    }

    private boolean isAttemptExpired(AssessmentSubmission attempt, Assessment assessment) {
        Instant expiresAt = computeAttemptExpiresAt(attempt, assessment);
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    private StartAttemptResponse toStartAttemptResponse(AssessmentSubmission attempt, Assessment assessment, boolean resumed) {
        return StartAttemptResponse.builder()
                .attemptId(attempt.getId())
                .assessmentId(assessment.getId())
                .attemptNo(attempt.getAttemptNo())
                .status(attempt.getStatus())
                .startedAt(attempt.getCreatedAt())
                .expiresAt(computeAttemptExpiresAt(attempt, assessment))
                .resumed(resumed)
                .build();
    }

    private Instant computeAttemptExpiresAt(AssessmentSubmission attempt, Assessment assessment) {
        if (attempt.getCreatedAt() == null) {
            return null;
        }

        if (assessment.getTimeLimit() <= 0) {
            return null;
        }

        return attempt.getCreatedAt().plusSeconds(assessment.getTimeLimit() * 60L);
    }

    private int computeTakenTimeInSeconds(AssessmentSubmission attempt) {
        if (attempt.getCreatedAt() == null) {
            return 0;
        }
        return (int) Math.max(0, Instant.now().getEpochSecond() - attempt.getCreatedAt().getEpochSecond());
    }

    private BigDecimal defaultPoint(Question question) {
        return question.getPoint() != null ? question.getPoint() : BigDecimal.ZERO;
    }

    private BigDecimal nonNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private Set<UUID> findPendingReviewAttemptIds(List<UUID> attemptIds) {
        if (attemptIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(questionSubmissionRepository.findAttemptIdsByAttemptIdsAndStatus(
                attemptIds,
                QuestionSubmissionStatus.PENDING_REVIEW
        ));
    }
}
