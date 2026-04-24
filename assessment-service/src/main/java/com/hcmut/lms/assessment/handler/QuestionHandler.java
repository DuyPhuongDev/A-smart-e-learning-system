package com.hcmut.lms.assessment.handler;

import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.handler.dto.FeedbackDto;
import com.hcmut.lms.assessment.handler.dto.GradingResult;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;

import java.math.BigDecimal;

/**
 * Unified handler for a specific QuestionType.
 * Combines the Factory role (create/update entity from DTO)
 * and the Strategy role (validate → grade → feedback for submissions).
 *
 * One implementation per QuestionType, registered as a Spring bean
 * and dispatched via a Map<QuestionType, QuestionHandler> registry.
 */
public interface QuestionHandler {

    QuestionType getSupportedType();

    // ── Factory responsibility ────────────────────────────────────────────────

    /** Builds a new (unpersisted) Question entity from a creation request. */
    Question create(QuestionRequest request);

    /** Applies changes from a request onto an existing Question entity. */
    Question update(Question existing, QuestionRequest request);

    // ── Strategy responsibility ───────────────────────────────────────────────

    /**
     * Validates the submission against the question's constraints.
     * Throws {@link IllegalArgumentException} on invalid input.
     */
    void validate(Question question, SubmissionDto submission);

    /** Grades the submission and returns earned points and status. */
    GradingResult grade(Question question, SubmissionDto submission, BigDecimal maxPoints);

    /** Produces student-facing feedback from a completed GradingResult. */
    FeedbackDto generateFeedback(Question question, GradingResult result);
}
