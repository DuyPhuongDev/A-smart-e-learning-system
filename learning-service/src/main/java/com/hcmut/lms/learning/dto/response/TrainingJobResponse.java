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
public class TrainingJobResponse {
    private UUID id;
    private String jobId;
    private UUID datasetVersionId;
    private String modelName;
    private String status;
    private String ecsTaskArn;
    private String s3ModelPath;
    private String errorMessage;
    private String startedAt;
    private String completedAt;
    private String createdAt;
    private String updatedAt;
    private TrainingMetricsResponse metrics;
}
