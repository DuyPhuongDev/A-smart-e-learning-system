package com.hcmut.lms.learning.dto.training;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingJobCallbackRequest {

    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("modelS3Path")
    private String modelS3Path;

    @JsonProperty("status")
    private String status;

    @JsonProperty("trainingTimeSeconds")
    private Double trainingTimeSeconds;

    @JsonProperty("metrics")
    private Map<String, Object> metrics;
}
