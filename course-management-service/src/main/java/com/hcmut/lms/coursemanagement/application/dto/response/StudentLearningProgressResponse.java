package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentLearningProgressResponse {
    private StudentProgramInfo programInfo;
    private StudentLearningSummary summary;
    private List<StudentProgressSectionItem> sections;
    private String updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentProgramInfo {
        private String specializationId;
        private String facultyName;
        private String specializationName;
        private String specializationCode;
        private String curriculumCode;
        private Integer curriculumYear;
        private Integer studentIntakeYear;
        private String studentName;
        private String studentCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentLearningSummary {
        private Integer earnedCredits;
        private Integer requiredCredits;
        private Integer remainingCredits;
        private Double cumulativeGpa10;
        private Double cumulativeGpa4;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentProgressSectionItem {
        private String sectionId;
        private String sectionName;
        private Integer displayOrder;
        private Boolean isRequired;
        private Integer requiredCredits;
        private Integer completedCredits;
        private Integer totalSectionCredits;
        private String notes;
        private List<StudentProgressSubjectItem> subjects;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentProgressSubjectItem {
        private String subjectId;
        private Integer order;
        private String semesterId;
        private String academicYearId;
        private String semesterCode;
        private String academicYear;
        private Integer semesterOrder;
        private Integer academicYearOrder;
        private String subjectCode;
        private String subjectName;
        private Integer credits;
        private Double grade10;
        private String letterGrade;
        private Double grade4;
        private Boolean isPassed;
        private Integer attemptNo;
        private Boolean isHighestResult;
    }
}
