package com.hcmut.lms.personalization.client.dto;

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
public class BatchGradePredictionRequest {
    private List<GradePredictionItem> predictions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradePredictionItem {
        private UUID studentId;
        private UUID subjectId;
        private Integer plannedSemesterCredits;
    }
}
