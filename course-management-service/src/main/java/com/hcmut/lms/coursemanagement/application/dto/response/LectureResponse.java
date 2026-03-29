package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureResponse {
    private UUID id;
    private Integer orderIndex;
    private String title;
    private String description;
    private Boolean isMandatory;
    private LectureType lectureType;
    private Float completionRate;
    private Integer viewCount;
    private Boolean allowPreview;
    private Boolean isDownloadable;
    private UUID chapterId;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer estimateTimeSpent;
    
    // For video lectures
    private String videoUrl;
    private Integer duration;
    
    // For document lectures
    private String fileUrl;
    private Integer numPages;
    private String fileFormat;
    
    // For text lectures
    private String content;
    private Integer wordCount;
    private String formatType;
}

