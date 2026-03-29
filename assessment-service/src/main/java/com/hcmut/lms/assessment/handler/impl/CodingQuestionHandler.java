package com.hcmut.lms.assessment.handler.impl;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.*;
import com.hcmut.lms.assessment.dto.request.question.CodingQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.TestCaseRequest;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.handler.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for CODING questions.
 * Code execution against test cases is delegated to an external runner service;
 * grading returns PENDING_REVIEW until the async result arrives.
 */
@Component
public class CodingQuestionHandler implements QuestionHandler {

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
        if (!(submission instanceof CodingSubmissionDto sub)) {
            throw new IllegalArgumentException("Expected CodingSubmissionDto but got: " + submission.getClass().getSimpleName());
        }

        if (sub.getCode() == null || sub.getCode().isBlank()) {
            throw new IllegalArgumentException("Code submission must not be empty.");
        }
        if (sub.getLanguage() == null || sub.getLanguage().isBlank()) {
            throw new IllegalArgumentException("Programming language must be specified.");
        }
    }

    @Override
    public GradingResult grade(Question question, SubmissionDto submission) {
        BigDecimal maxPoints = question.getPoint() != null ? question.getPoint() : BigDecimal.ZERO;

        return GradingResult.builder()
                .questionId(question.getId())
                .earnedPoints(BigDecimal.ZERO)
                .maxPoints(maxPoints)
                .status(GradingStatus.PENDING_REVIEW)
                .detail("Code submitted. Awaiting execution against test cases.")
                .build();
    }

    @Override
    public FeedbackDto generateFeedback(Question question, GradingResult result) {
        String message = switch (result.getStatus()) {
            case CORRECT -> "All test cases passed. Great job!";
            case PARTIAL -> "Some test cases passed. Score: " + result.getEarnedPoints() + "/" + result.getMaxPoints();
            case INCORRECT -> "No test cases passed. Review your logic and try again.";
            case PENDING_REVIEW -> "Your code is being evaluated. Results will be available shortly.";
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
