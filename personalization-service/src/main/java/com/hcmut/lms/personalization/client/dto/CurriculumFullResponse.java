package com.hcmut.lms.personalization.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurriculumFullResponse {
    private String curriculumCode;
    private UUID specializationId;
    private UUID intakeYearId;
    private String name;
    private Integer totalCredits;
    private List<CurriculumSectionFull> sections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CurriculumSectionFull {
        private UUID sectionId;
        private String sectionName;
        private Integer requiredCredits;
        private Integer displayOrder;
        private Integer priorityWeight;
        private List<CurriculumSubjectFull> subjects;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CurriculumSubjectFull {
        private Integer curriculumSubjectId;
        private UUID subjectId;
        private String subjectCode;
        private String subjectName;
        private Integer credits;
        private Boolean isRequired;
        private Integer displayOrder;
        private Integer recommendedYear;
        private Integer recommendedSemesterInYear;
        private List<SubjectRelation> prerequisites;
        private List<SubjectRelation> recommendations;
        private List<SubjectRelation> parallels;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubjectRelation {
        private UUID subjectId;
        private String subjectCode;
        private String subjectName;
    }
}
