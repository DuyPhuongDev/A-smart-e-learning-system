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
    private UUID subjectId;
    private String subjectName;
    private UUID semesterId;
    private UUID createdBy;
    private String semesterCode;
    private Instant createdAt;
    private Instant updatedAt;
}

