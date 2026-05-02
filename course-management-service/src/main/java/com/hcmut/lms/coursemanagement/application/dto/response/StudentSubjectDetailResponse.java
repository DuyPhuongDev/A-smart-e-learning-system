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
public class StudentSubjectDetailResponse {
    private String subjectId;
    private Integer recommendedYear;
    private Integer recommendedSemester;
    private Boolean isPassed;
    private List<StudentSubjectAttemptItem> attempts;
    private List<SubjectRelationItem> prerequisites;
    private List<SubjectRelationItem> recommendations;
    private List<SubjectRelationItem> parallels;
    private List<SubjectLearningOutcomeItem> learningOutcomes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StudentSubjectAttemptItem {
        private Integer attemptNo;
        private String semesterLabel;
        private Double grade10;
        private String letterGrade;
        private Double grade4;
        private Boolean isPassed;
        private Boolean isApplied;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubjectRelationItem {
        private String subjectId;
        private String subjectCode;
        private String subjectName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubjectLearningOutcomeItem {
        private String code;
        private String description;
        private List<SubjectLearningOutcomeItem> children;
    }
}
