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
public class GradePredictionDatasetVersionResponse {
    private UUID id;
    private Integer versionNumber;
    private String description;
    private Integer totalRows;
    private String status;
    private String completedAt;
    private String createdAt;
    private String updatedAt;
}
