package com.hcmut.lms.learning.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingAssessmentResponse {
    private UUID assessmentId;
    private String title;
    private String assessmentType;
    private String subjectName;
    private String subjectCode;
    private UUID classId;
    private String className;
    private Instant startTime;
    private Instant closeTime;
}
