package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lightweight DTO for grade prediction dataset computation.
 * Contains class section metadata enriched with subject credits and parsed semester key.
 * Used exclusively by the internal enrollment-data endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassSectionDatasetResponse {
    private UUID classId;
    private UUID subjectId;
    private String subjectCode;
    /** Curriculum section ID - subjects in the same section are considered related for grade prediction */
    private UUID curriculumSectionId;
    private Integer credits;
    private UUID semesterId;
    private String semesterCode;
    private String yearCode;
    /** Parsed from semesterCode, e.g. "HK1" -> 1, "HK2" -> 2 */
    private Integer semesterNumber;
    /** Computed: startYear * 10 + semesterNumber, e.g. "2023-2024" + "HK1" -> 20231 */
    private Integer semKey;
}
