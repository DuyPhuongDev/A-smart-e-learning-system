package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingMetricsResponse {
    private UUID id;
    private UUID trainingJobId;
    private String metricType;
    private String datasetSplit;
    private String scaleType;
    private Double mae;
    private Double rmse;
    private Double r2;
    private Double muError;
    private Double sigmaError;
    private Integer sampleCount;
    private String createdAt;
    private String updatedAt;
}
