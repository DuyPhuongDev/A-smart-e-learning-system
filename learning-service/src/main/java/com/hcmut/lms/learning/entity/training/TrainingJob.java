package com.hcmut.lms.learning.entity.training;

import com.hcmut.lms.learning.entity.BaseEntity;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "training_job")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_id", nullable = false, unique = true, length = 50)
    private String jobId;

    @Column(name = "dataset_version_id", nullable = false)
    private UUID datasetVersionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_version_id", insertable = false, updatable = false)
    private GradePredictionDatasetVersion datasetVersion;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "s3_dataset_url", nullable = false, columnDefinition = "TEXT")
    private String s3DatasetUrl;

    @Column(name = "s3_model_url", columnDefinition = "TEXT")
    private String s3ModelUrl;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TrainingJobStatus status = TrainingJobStatus.PENDING;

    @Column(name = "training_time_seconds")
    private Double trainingTimeSeconds;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(mappedBy = "trainingJob", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainingMetrics> metrics = new ArrayList<>();

    public enum TrainingJobStatus {
        PENDING,    // Created but not yet queued
        QUEUED,     // Sent to SQS
        RUNNING,    // Worker is processing
        COMPLETED,  // Successfully completed
        FAILED      // Failed with error
    }

    // Lifecycle methods
    public void markAsQueued() {
        this.status = TrainingJobStatus.QUEUED;
        this.submittedAt = Instant.now();
    }

    public void markAsRunning() {
        this.status = TrainingJobStatus.RUNNING;
    }

    public void markAsCompleted(String s3ModelUrl, Double trainingTimeSeconds) {
        this.status = TrainingJobStatus.COMPLETED;
        this.s3ModelUrl = s3ModelUrl;
        this.trainingTimeSeconds = trainingTimeSeconds;
        this.completedAt = Instant.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = TrainingJobStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = Instant.now();
    }

    public boolean isTerminal() {
        return status == TrainingJobStatus.COMPLETED || status == TrainingJobStatus.FAILED;
    }
}
