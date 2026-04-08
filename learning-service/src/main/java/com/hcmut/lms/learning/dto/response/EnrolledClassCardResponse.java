package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for displaying enrolled class cards in UI
 * Contains class information + enrollment progress
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrolledClassCardResponse {
    // Enrollment info
    private UUID enrollmentId;
    private LocalDateTime enrolledAt;
    private Double progressPercentage;
    private Double finalGrade;
    private Integer attemptNo;
    
    // Class info
    private UUID id;
    private String sectionName;
    private String status;
    private Boolean isOfficial;
    private UUID teacherId;
    private String teacherName;
    private UUID subjectId;
    private String subjectName;
    private UUID semesterId;
    private String semesterCode;
    private UUID createdBy;
    private String description;
    private String language;
    private String level;
    private String thumbnailUrl;
    private String introVideo;
    private Integer durationHours;
    private String objective;
    private String topic;
    private Instant createdAt;
    private Instant updatedAt;
    private String code;
    private Integer maxStudents;
    private Integer currentStudents;
}
