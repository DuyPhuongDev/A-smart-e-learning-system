package com.hcmut.lms.assessment.handler.impl;

import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.AnswerOptionRequest;
import com.hcmut.lms.assessment.dto.request.question.McqQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.handler.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class McqQuestionHandler implements QuestionHandler {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.MCQ;
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    @Override
    public Question create(QuestionRequest request) {
        McqQuestionRequest req = (McqQuestionRequest) request;

        McqQuestion question = McqQuestion.builder()
                .questionType(QuestionType.MCQ)
                .difficultLevel(req.getDifficultLevel())
                .required(req.isRequired())
                .allowMultiAnswer(req.isAllowMultiAnswer())
                .content(req.getContent())
                .shuffleOption(req.isShuffleOption())
                .banks(new HashSet<>())
                .build();

        if (req.getAnswerOptions() != null) {
            req.getAnswerOptions().forEach(o -> question.addOption(buildOption(o)));
        }
        return question;
    }

    @Override
    public Question update(Question existing, QuestionRequest request) {
        McqQuestionRequest req = (McqQuestionRequest) request;
        McqQuestion mcq = (McqQuestion) existing;

        if (req.getDifficultLevel() != null) mcq.setDifficultLevel(req.getDifficultLevel());
        mcq.setRequired(req.isRequired());
        mcq.setAllowMultiAnswer(req.isAllowMultiAnswer());
        mcq.setShuffleOption(req.isShuffleOption());
        mcq.setContent(req.getContent());


        if (req.getAnswerOptions() != null) {
            req.getAnswerOptions().forEach(o -> mcq.addOption(buildOption(o)));
        }
        return mcq;
    }

    // ── Strategy ──────────────────────────────────────────────────────────────

    @Override
    public void validate(Question question, SubmissionDto submission) {
        McqQuestion mcq = cast(question, McqQuestion.class, "McqQuestion");
        McqSubmissionDto sub = cast(submission, McqSubmissionDto.class, "McqSubmissionDto");

        List<UUID> selected = sub.getSelectedOptionIds();
        if (selected == null || selected.isEmpty()) {
            throw new IllegalArgumentException("At least one option must be selected.");
        }
        if (!mcq.isAllowMultiAnswer() && selected.size() > 1) {
            throw new IllegalArgumentException("This question only allows a single answer.");
        }

        Set<UUID> validIds = mcq.getAnswerOptions().stream()
                .map(AnswerOption::getId).collect(Collectors.toSet());
        selected.forEach(id -> {
            if (!validIds.contains(id)) {
                throw new IllegalArgumentException("Invalid option id: " + id);
            }
        });
    }

    /// need handler again
    @Override
    public GradingResult grade(Question question, SubmissionDto submission, BigDecimal maxPoints) {
        McqQuestion mcq = (McqQuestion) question;
        McqSubmissionDto sub = (McqSubmissionDto) submission;

        Set<UUID> selected = Set.copyOf(sub.getSelectedOptionIds());
        Set<UUID> correctIds = mcq.getAnswerOptions().stream()
                .filter(AnswerOption::isCorrect)
                .map(AnswerOption::getId)
                .collect(Collectors.toSet());

        long correctSelected = selected.stream().filter(correctIds::contains).count();
        boolean fullyCorrect = selected.equals(correctIds);

        GradingStatus status;
        BigDecimal earned;

        if (fullyCorrect) {
            status = GradingStatus.CORRECT;
            earned = maxPoints;
        } else if (correctSelected > 0 && mcq.isAllowMultiAnswer()) {
            status = GradingStatus.PARTIAL;
            earned = maxPoints
                    .multiply(BigDecimal.valueOf(correctSelected))
                    .divide(BigDecimal.valueOf(correctIds.size()), 2, RoundingMode.HALF_UP);
        } else {
            status = GradingStatus.INCORRECT;
            earned = BigDecimal.ZERO;
        }

        return GradingResult.builder()
                .questionId(question.getId())
                .earnedPoints(earned)
                .maxPoints(maxPoints)
                .status(status)
                .detail(String.format("Selected %d/%d correct options.", correctSelected, correctIds.size()))
                .build();
    }

    @Override
    public FeedbackDto generateFeedback(Question question, GradingResult result) {
        McqQuestion mcq = (McqQuestion) question;

        String correctAnswer = mcq.getAnswerOptions().stream()
                .filter(AnswerOption::isCorrect)
                .map(AnswerOption::getContent)
                .collect(Collectors.joining("; "));

        String message = switch (result.getStatus()) {
            case CORRECT -> "Correct! Well done.";
            case PARTIAL -> "Partially correct. You got " + result.getEarnedPoints() + "/" + result.getMaxPoints() + " points.";
            case INCORRECT -> "Incorrect. Review the material and try again.";
            default -> "Graded.";
        };

        return FeedbackDto.builder()
                .questionId(question.getId())
                .message(message)
                .correctAnswer(correctAnswer)
                .gradingStatus(result.getStatus())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AnswerOption buildOption(AnswerOptionRequest req) {
        return AnswerOption.builder()
                .content(req.getContent())
                .correct(req.getCorrect())
                .orderIndex(req.getOrderIndex())
                .explanation(req.getExplanation())
                .build();
    }

    private <T> T cast(Object obj, Class<T> type, String expectedName) {
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(
                    "Expected " + expectedName + " but got: " + obj.getClass().getSimpleName());
        }
        return type.cast(obj);
    }
}
