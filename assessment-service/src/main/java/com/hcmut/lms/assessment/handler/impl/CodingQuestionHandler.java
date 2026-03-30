package com.hcmut.lms.assessment.handler.impl;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.*;
import com.hcmut.lms.assessment.dto.request.question.CodingQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.TestCaseRequest;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.handler.dto.*;
import com.hcmut.lms.assessment.service.judge.CppJudgeService;
import com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.HashSet;

/**
 * Handler for CODING questions.
 */
@Component
@RequiredArgsConstructor
public class CodingQuestionHandler implements QuestionHandler {

    private final CppJudgeService cppJudgeService;

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.CODING;
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    @Override
    public Question create(QuestionRequest request) {
        CodingQuestionRequest req = (CodingQuestionRequest) request;

        CodingQuestion question = CodingQuestion.builder()
                .questionType(QuestionType.CODING)
                .difficultLevel(req.getDifficultLevel())
                .point(req.getPoint())
                .required(req.isRequired())
                .content(req.getContent())
                .banks(new HashSet<>())
                .problemDescription(req.getProblemDescription())
                .executionTimeLimit(req.getExecutionTimeLimit())
                .executionMemoryLimit(req.getExecutionMemoryLimit())
                .language(req.getLanguage())
                .initialCode(req.getInitialCode())
                .build();

        if (req.getTestCases() != null) {
            req.getTestCases().forEach(tc -> question.addTestCase(buildTestCase(tc)));
        }
        return question;
    }

    @Override
    public Question update(Question existing, QuestionRequest request) {
        CodingQuestionRequest req = (CodingQuestionRequest) request;
        CodingQuestion coding = (CodingQuestion) existing;

        if (req.getDifficultLevel() != null) coding.setDifficultLevel(req.getDifficultLevel());
        if (req.getPoint() != null) coding.setPoint(req.getPoint());
        coding.setRequired(req.isRequired());
        if (req.getProblemDescription() != null) coding.setProblemDescription(req.getProblemDescription());
        if (req.getExecutionTimeLimit() > 0) coding.setExecutionTimeLimit(req.getExecutionTimeLimit());
        if (req.getExecutionMemoryLimit() > 0) coding.setExecutionMemoryLimit(req.getExecutionMemoryLimit());
        if (req.getLanguage() != null) coding.setLanguage(req.getLanguage());
        if (req.getInitialCode() != null) coding.setInitialCode(req.getInitialCode());

        if (req.getTestCases() != null) {
            coding.getTestCases().clear();
            req.getTestCases().forEach(tc -> coding.addTestCase(buildTestCase(tc)));
        }
        return coding;
    }

    // ── Strategy ──────────────────────────────────────────────────────────────

    @Override
    public void validate(Question question, SubmissionDto submission) {
        if (!(question instanceof CodingQuestion codingQuestion)) {
            throw new IllegalArgumentException("Expected CodingQuestion but got: " + question.getClass().getSimpleName());
        }
        if (!(submission instanceof CodingSubmissionDto codingSubmission)) {
            throw new IllegalArgumentException("Expected CodingSubmissionDto but got: " + submission.getClass().getSimpleName());
        }

        if (codingQuestion.getTestCases() == null || codingQuestion.getTestCases().isEmpty()) {
            throw new IllegalArgumentException("Coding question has no test cases configured");
        }
        if (codingQuestion.getExecutionTimeLimit() <= 0) {
            throw new IllegalArgumentException("Coding question execution time limit must be > 0");
        }
        if (codingQuestion.getExecutionMemoryLimit() <= 0) {
            throw new IllegalArgumentException("Coding question execution memory limit must be > 0");
        }
        if (codingSubmission.getCode() != null
                && !codingSubmission.getCode().isBlank()
                && !cppJudgeService.isSupportedLanguage(codingSubmission.getLanguage())) {
            throw new IllegalArgumentException(
                    "Unsupported language. Supported: " + cppJudgeService.supportedLanguagesDescription()
            );
        }
    }

    @Override
    public GradingResult grade(Question question, SubmissionDto submission) {
        CodingQuestion codingQuestion = (CodingQuestion) question;
        CodingSubmissionDto codingSubmission = (CodingSubmissionDto) submission;
        BigDecimal maxPoints = question.getPoint() != null ? question.getPoint() : BigDecimal.ZERO;

        if (codingSubmission.getCode() == null || codingSubmission.getCode().isBlank()) {
            return GradingResult.builder()
                    .questionId(question.getId())
                    .earnedPoints(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP))
                    .maxPoints(maxPoints)
                    .status(GradingStatus.INCORRECT)
                    .detail("Question is not answered")
                    .build();
        }

        if (!cppJudgeService.isSupportedLanguage(codingSubmission.getLanguage())) {
            throw new IllegalArgumentException(
                    "Unsupported language. Supported: " + cppJudgeService.supportedLanguagesDescription()
            );
        }

        CodingJudgeEvaluation evaluation = cppJudgeService.evaluate(
                codingQuestion,
                codingSubmission.getCode(),
                codingSubmission.getLanguage()
        );

        BigDecimal earnedPoints = evaluation.getTotalCount() <= 0 || evaluation.getPassedCount() <= 0
                ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                : maxPoints.multiply(BigDecimal.valueOf(evaluation.getPassedCount()))
                .divide(BigDecimal.valueOf(evaluation.getTotalCount()), 3, RoundingMode.HALF_UP);

        GradingStatus gradingStatus;
        if (evaluation.getPassedCount() == 0) {
            gradingStatus = GradingStatus.INCORRECT;
        } else if (evaluation.getPassedCount() == evaluation.getTotalCount()) {
            gradingStatus = GradingStatus.CORRECT;
        } else {
            gradingStatus = GradingStatus.PARTIAL;
        }

        return GradingResult.builder()
                .questionId(question.getId())
                .earnedPoints(earnedPoints)
                .maxPoints(maxPoints)
                .status(gradingStatus)
                .detail(evaluation.getDetail())
                .build();
    }

    @Override
    public FeedbackDto generateFeedback(Question question, GradingResult result) {
        String message = switch (result.getStatus()) {
            case CORRECT -> "All test cases passed. Great job!";
            case PARTIAL -> "Some test cases passed. Score: " + result.getEarnedPoints() + "/" + result.getMaxPoints();
            case INCORRECT -> "No test cases passed. Review your logic and try again.";
            case PENDING_REVIEW -> "Your code is awaiting manual review.";
        };

        return FeedbackDto.builder()
                .questionId(question.getId())
                .message(message)
                .gradingStatus(result.getStatus())
                .hints(List.of("Check edge cases", "Verify time/memory complexity"))
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TestCase buildTestCase(TestCaseRequest req) {
        return TestCase.builder()
                .input(req.getInput())
                .expected(req.getExpected())
                .hidden(req.isHidden())
                .build();
    }
}
