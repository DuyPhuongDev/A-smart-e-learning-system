package com.hcmut.lms.assessment.dto.response.teacher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TeacherAssessmentReportResponse {
    private UUID classId;
    private int totalStudents;
    private List<TeacherAssessmentReportItemResponse> assessments;
}
