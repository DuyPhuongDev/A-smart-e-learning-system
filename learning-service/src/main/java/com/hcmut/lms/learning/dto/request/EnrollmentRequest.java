package com.hcmut.lms.learning.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EnrollmentRequest {
    @NotNull(message = "Student id is mandatory!")
    private UUID studentId;

    @NotNull(message = "Class id is mandatory!")
    private UUID classId;
}
