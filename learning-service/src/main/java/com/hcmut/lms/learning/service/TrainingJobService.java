package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.dto.response.TrainingJobResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.dto.training.TriggerTrainingRequest;
import com.hcmut.lms.learning.entity.training.TrainingJob;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TrainingJobService {

    /**
     * Trigger a new training job:
     * 1. Fetch latest completed dataset version
     * 2. Generate CSV from dataset rows
     * 3. Upload CSV to S3
     * 4. Send message to SQS
     * 5. Save training job record
     */
    CompletableFuture<TrainingJobResponse> triggerTrainingJob(TriggerTrainingRequest request);

    /**
     * Handle callback from Fargate worker with training results
     */
    void handleTrainingCallback(TrainingJobCallbackRequest callback);

    /**
     * Handle error callback from Fargate worker
     */
    void handleTrainingError(String jobId, String errorMessage);

    /**
     * Get training job by job ID
     */
    TrainingJob getTrainingJobByJobId(String jobId);

    /**
     * Get all training jobs for a dataset version
     */
    List<TrainingJob> getTrainingJobsByDatasetVersion(UUID datasetVersionId);

    /**
     * Get recent training jobs (paginated)
     */
    List<TrainingJob> getRecentTrainingJobs(int limit);
}
