package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ClassSectionResponse {
    private UUID id;
    private String sectionName;
    private String status;
    private Boolean isOfficial;
    private UUID teacherId;
    private String teacherName;
    private UUID subjectId;
    private String subjectName;
    private String subjectCode;
    private Integer credits;
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

