package com.hcmut.lms.coursemanagement.application.dto.request;

import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LectureRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private Boolean isMandatory;

    private Integer orderIndex;
    
    @NotNull(message = "Lecture type is required")
    private LectureType lectureType;
    
    private Boolean allowPreview;
    
    private Boolean isDownloadable;
    
    @NotNull(message = "Chapter ID is required")
    private UUID chapterId;
    
    // For video lectures
    private String videoUrl;
    private Integer duration;
    
    // For document lectures
    private String fileUrl;
    private Integer numPages;
    private String fileFormat;
    
    // For text lectures
    private String content;
    private String formatType;
}

