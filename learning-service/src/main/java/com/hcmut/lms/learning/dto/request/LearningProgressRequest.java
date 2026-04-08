package com.hcmut.lms.learning.dto.request;

import com.hcmut.lms.learning.entity.progress.ContentType;
import com.hcmut.lms.learning.entity.progress.PositionUnit;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Request DTO for updating learning progress
 */
@Getter
@Setter
public class LearningProgressRequest {

    @NotNull(message = "Lecture ID is mandatory!")
    private UUID lectureId;

    @NotNull(message = "Class ID is mandatory!")
    private UUID classId;

    /**
     * Current position in the content (e.g., video time in seconds, page number)
     */
    private Integer currentPosition;
    /**
     * Whether the lecture is completed
     */
    private Boolean isCompleted;

    /**
     * Content type (VIDEO, DOCUMENT, TEXT)
     */
    private ContentType contentType;

    /**
     * Position unit (SECOND, PAGE, OFFSET)
     */
    private PositionUnit positionUnit;
}
