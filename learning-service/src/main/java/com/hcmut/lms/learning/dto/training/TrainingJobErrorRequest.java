package com.hcmut.lms.learning.dto.training;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingJobErrorRequest {

    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("error")
    private String error;
}
