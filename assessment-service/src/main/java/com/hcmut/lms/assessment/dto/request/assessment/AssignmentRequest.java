package com.hcmut.lms.assessment.dto.request.assessment;


import com.hcmut.lms.assessment.dto.request.question.FileUploadRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
    @NotNull(message = "classId is required")
    private UUID classId;
    private String title;
    private String description;
    private Instant startTime;
    private Instant closeTime;
    private List<FileUploadRequest> files;
}
