package com.hcmut.lms.assessment.dto.request.question;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EssayQuestionRequest extends QuestionRequest {

    private String sampleAnswer;

    private int maxFileSize;

    private List<String> acceptedFileTypes;

    private List<FileUploadRequest> fileUploads;
}
