package com.hcmut.lms.learning.client.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
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
