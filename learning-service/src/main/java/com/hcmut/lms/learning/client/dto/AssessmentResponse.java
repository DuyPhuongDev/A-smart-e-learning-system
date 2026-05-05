package com.hcmut.lms.learning.client.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {
    private UUID id;
    private UUID classId;
    private String assessmentType;
    private String assessmentStatus;
    private String title;
    private Instant startTime;
    private Instant closeTime;
}
