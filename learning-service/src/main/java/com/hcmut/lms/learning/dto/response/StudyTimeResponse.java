package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for study time
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyTimeResponse {
    private UUID id;
    private UUID studentId;
    private UUID classId;
    private UUID lectureId;
    private Integer durationSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private Instant updatedAt;
}
