package com.hcmut.lms.learning.dto.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerTrainingRequest {

    /**
     * Optional: specific dataset version ID to use.
     * If null, uses latest completed version.
     */
    private UUID datasetVersionId;

    /**
     * Optional description for this training run
     */
    private String description;
}
