package com.hcmut.lms.assessment.dto.response;

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
public class GradingBreakdownResponse {

    /** Grading component name: QUIZ, MIDTERM, FINAL, etc. */
    private String gradingType;

    /** Total weight (%) this grading component contributes to the final grade. */
    private Float totalWeight;

    /** Individual assessments that belong to this grading component. */
    private List<AssessmentWeightItem> assessments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AssessmentWeightItem {
        private UUID assessmentId;
        private String title;
        /** Each assessment's contribution = totalWeight / number of assessments of this type. */
        private Float weight;
    }
}
