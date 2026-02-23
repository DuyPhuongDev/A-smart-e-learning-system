package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subject import data from Excel file
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectImportData {
    private int rowNumber;
    private String code;
    private String name;
    private Integer credits;
    private String description;
}
