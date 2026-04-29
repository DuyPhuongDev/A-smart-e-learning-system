package com.hcmut.lms.learning.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Client-side DTO mirroring ClassSectionDatasetResponse from course-management-service.
 * Used for grade prediction dataset computation.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSectionDatasetResponse {
    private UUID classId;
    private UUID subjectId;
    private String subjectCode;
    private UUID curriculumSectionId;
    private Integer credits;
    private UUID semesterId;
    private String semesterCode;
    private Integer semKey;
}
