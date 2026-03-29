package com.hcmut.lms.learning.dto.request;

import com.hcmut.lms.learning.entity.progress.ContentType;
import com.hcmut.lms.learning.entity.progress.PositionUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for recording study time
 */
@Getter
@Setter
public class StudyTimeRequest {
    @NotNull(message = "Class ID is mandatory!")
    private UUID classId;

    @NotNull(message = "Lecture ID is mandatory!")
    private UUID lectureId;

    /**
     * Duration in seconds
     */
    @NotNull(message = "Duration is mandatory!")
    @Positive(message = "Duration must be positive!")
    private Integer durationSeconds;

    private Integer currentPosition;

    private ContentType contentType;

    private PositionUnit positionUnit;

    /**
     * Start time of the study session
     */
    @NotNull(message = "Start time is mandatory!")
    private LocalDateTime startedAt;

    /**
     * Additional metadata (e.g., device type, platform) as JSON object
     */
    private Map<String, Object> metadata;
}
