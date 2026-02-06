package com.hcmut.lms.coursemanagement.application.dto.request;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class BaseLectureRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Boolean isMandatory;

    private Integer orderIndex;

    private Integer estimate_time_spent;

    @NotNull(message = "Lecture type is required")
    private LectureType lectureType;

    private Boolean allowPreview;

    private Boolean isDownloadable;

    @NotNull(message = "Chapter ID is required")
    private UUID chapterId;
}
