package com.hcmut.lms.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.assessment.dto.request.question.FileUploadRequest;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentResponse {
    private UUID assessmentId;
    private UUID questionId;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;

    List<FileUploadRequest> fileUploads;

}
