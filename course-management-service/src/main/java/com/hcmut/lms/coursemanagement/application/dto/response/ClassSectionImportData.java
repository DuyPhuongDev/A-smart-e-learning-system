package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for class section import data from Excel file
 * Each row represents one class section with its subject info
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSectionImportData {
    private int rowNumber;

    private String subjectCode;
    private String classCode;
    private String sectionName;
    private String status;
    private Integer maxStudents;
    private String teacherCode;
}
