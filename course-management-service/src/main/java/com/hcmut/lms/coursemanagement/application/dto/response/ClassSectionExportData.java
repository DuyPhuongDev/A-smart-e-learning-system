package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSectionExportData {
    private String subjectCode;
    private String subjectName;
    private Integer credits;
    private String classCode;
    private String sectionName;
    private String semesterCode;
    private String status;
    private Integer maxStudents;
    private Integer currentStudents;
    private String teacherId;
    private String teacherName;
}
