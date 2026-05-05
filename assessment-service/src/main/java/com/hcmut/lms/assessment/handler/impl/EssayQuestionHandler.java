package com.hcmut.lms.assessment.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.assessment.domain.entity.answer.EssayAcceptedFileType;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.EssayQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.QuestionRequest;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.handler.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for ESSAY questions.
 * Essays require manual review; grading always returns PENDING_REVIEW.
 */
@Component
public class EssayQuestionHandler implements QuestionHandler {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.ESSAY;
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    @Override
    public Question create(QuestionRequest request) {
        EssayQuestionRequest req = (EssayQuestionRequest) request;

        EssayQuestion question = EssayQuestion.builder()
                .questionType(QuestionType.ESSAY)
                .difficultLevel(req.getDifficultLevel())
                .banks(new HashSet<>())
                .required(req.isRequired())
                .content(req.getContent())
                .sampleAnswer(req.getSampleAnswer())
                .maxFileSize(req.getMaxFileSize())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonFiles = objectMapper.writeValueAsString(req.getFileUploads());
            question.setInstructionFiles(jsonFiles);
        } catch (JsonProcessingException e) {
            question.setInstructionFiles("[]"); // Giá trị mặc định nếu lỗi
        }

        if (req.getAcceptedFileTypes() != null) {
            req.getAcceptedFileTypes().forEach(ft ->
                    question.addAcceptedFileType(EssayAcceptedFileType.builder().fileType(ft).build()));
        }
        return question;
    }

    @Override
    public Question update(Question existing, QuestionRequest request) {
        EssayQuestionRequest req = (EssayQuestionRequest) request;
        EssayQuestion essay = (EssayQuestion) existing;

        if (req.getDifficultLevel() != null) essay.setDifficultLevel(req.getDifficultLevel());
        essay.setRequired(req.isRequired());
        if (req.getSampleAnswer() != null) essay.setSampleAnswer(req.getSampleAnswer());
        if (req.getMaxFileSize() > 0) essay.setMaxFileSize(req.getMaxFileSize());
        essay.setContent(req.getContent());
        if (req.getAcceptedFileTypes() != null) {
            req.getAcceptedFileTypes().forEach(ft ->
                    essay.addAcceptedFileType(EssayAcceptedFileType.builder().fileType(ft).build()));
        }
        return essay;
    }

    // ── Strategy ──────────────────────────────────────────────────────────────

    @Override
    public void validate(Question question, SubmissionDto submission) {
        if (!(question instanceof EssayQuestion essayQuestion)) {
            throw new IllegalArgumentException("Expected EssayQuestion but got: " + question.getClass().getSimpleName());
        }
        if (!(submission instanceof EssaySubmissionDto sub)) {
            throw new IllegalArgumentException("Expected EssaySubmissionDto but got: " + submission.getClass().getSimpleName());
        }

        boolean hasText = sub.getTextContent() != null && !sub.getTextContent().isBlank();
        boolean hasFile = sub.getFileUrl() != null && !sub.getFileUrl().isBlank();

        if (!hasText && !hasFile) {
            throw new IllegalArgumentException("Essay submission must contain text content or a file URL.");
        }

        if (hasFile) {
            Set<String> accepted = essayQuestion.getAcceptedFileTypes().stream()
                    .map(EssayAcceptedFileType::getFileType).collect(Collectors.toSet());
            if (!accepted.isEmpty()) {
                String url = sub.getFileUrl();
                String ext = url.contains(".")
                        ? url.substring(url.lastIndexOf('.') + 1).toLowerCase() : "";
                if (!accepted.contains(ext)) {
                    throw new IllegalArgumentException(
                            "File type '." + ext + "' is not accepted. Allowed: " + accepted);
                }
            }
        }
    }

    // need handler
    @Override
    public GradingResult grade(Question question, SubmissionDto submission,  BigDecimal maxPoints) {


        return GradingResult.builder()
                .questionId(question.getId())
                .earnedPoints(BigDecimal.ZERO)
                .maxPoints(maxPoints)
                .status(GradingStatus.PENDING_REVIEW)
                .detail("Essay submitted. Awaiting instructor review.")
                .build();
    }

    @Override
    public FeedbackDto generateFeedback(Question question, GradingResult result) {
        EssayQuestion essay = (EssayQuestion) question;

        String message = switch (result.getStatus()) {
            case CORRECT -> "Excellent essay! Full marks awarded.";
            case PARTIAL -> "Good effort. Score: " + result.getEarnedPoints() + "/" + result.getMaxPoints();
            case INCORRECT -> "Your essay did not meet the requirements. Please review the sample answer.";
            case PENDING_REVIEW -> "Your essay has been submitted and is awaiting instructor review.";
        };

        return FeedbackDto.builder()
                .questionId(question.getId())
                .message(message)
                .correctAnswer(essay.getSampleAnswer())
                .gradingStatus(result.getStatus())
                .hints(List.of("Refer to the course material", "Structure your answer clearly"))
                .build();
    }
}
