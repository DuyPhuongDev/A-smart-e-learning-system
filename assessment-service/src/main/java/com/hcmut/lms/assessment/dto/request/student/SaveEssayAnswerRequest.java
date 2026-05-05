package com.hcmut.lms.assessment.dto.request.student;

import com.hcmut.lms.assessment.dto.request.question.FileUploadRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SaveEssayAnswerRequest extends SaveAnswerRequest {
    private String textContent;

    List<FileUploadRequest> fileUploads;
}
