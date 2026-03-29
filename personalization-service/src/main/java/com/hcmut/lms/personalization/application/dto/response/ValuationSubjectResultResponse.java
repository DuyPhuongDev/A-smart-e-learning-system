package com.hcmut.lms.personalization.application.dto.response;

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
public class ValuationSubjectResultResponse {
    private UUID subjectId;
    private String occupationCode;
    private Double totalValue;
    private Double valueDensity;
    private List<ValuationMatchSummary> topMatches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValuationMatchSummary {
        private String requirementText;
        private String elementType;
        private Double importanceScore;
        private Double similarityScore;
        private Double weightedScore;
    }
}
