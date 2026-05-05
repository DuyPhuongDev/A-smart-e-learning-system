package com.hcmut.lms.assessment.dto.response.student;

import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.FileUploadRequest;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptQuestionResponse {
    private UUID questionId;
    private int orderIndex;
    private QuestionType questionType;
    private String content;
    private BigDecimal point;
    private boolean required;

    private List<AttemptQuestionOptionResponse> options;
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
    private List<FileUploadRequest> instructionFiles;
    private String submittedText;
    private List<FileUploadRequest> submittedFiles;
}
