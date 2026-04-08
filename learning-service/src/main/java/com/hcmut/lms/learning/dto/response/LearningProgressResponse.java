package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.learning.entity.progress.ContentType;
import com.hcmut.lms.learning.entity.progress.PositionUnit;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for learning progress
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LearningProgressResponse {
    private UUID id;
    private UUID studentId;
    private UUID lectureId;
    private Integer currentPosition;
    private BigDecimal progressPercentage;
    private Integer totalTimeSpent;
    private ContentType contentType;
    private PositionUnit positionUnit;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
