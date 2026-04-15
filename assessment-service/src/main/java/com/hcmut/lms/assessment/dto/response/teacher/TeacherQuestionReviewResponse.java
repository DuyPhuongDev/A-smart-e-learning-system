package com.hcmut.lms.assessment.dto.response.teacher;

import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmissionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherQuestionReviewResponse {
    private UUID questionId;
    private Integer orderIndex;
    private QuestionType questionType;
    private String content;
    private BigDecimal maxPoints;
    private BigDecimal earnedPoints;
    private QuestionSubmissionStatus status;
    private String feedback;

    private List<TeacherQuestionOptionResponse> options;
    private Boolean allowMultiAnswer;
    private List<UUID> selectedOptionIds;

    private String problemDescription;
    private Integer executionTimeLimit;
    private Integer executionMemoryLimit;
    private String language;
    private String initialCode;
    private String submittedCode;
    private String submittedLanguage;

    private Integer maxFileSize;
    private List<String> acceptedFileTypes;
    private String submittedText;
    private String submittedFileUrl;
    private String submittedFileFormat;
    private Integer submittedNumPages;
    private Integer submittedWordCount;
}
