package com.hcmut.lms.learning.entity.dataset;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a snapshot/version of the grade prediction feature dataset.
 * Each version corresponds to one ETL run that populates
 * {@link GradePredictionDataset} rows with a matching version_id.
 *
 * Table: learning.grade_prediction_dataset_version
 */
@Entity
@Table(name = "grade_prediction_dataset_version")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GradePredictionDatasetVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Monotonically increasing version counter (unique per table). */
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "description")
    private String description;

    /** Number of feature rows belonging to this version. */
    @Column(name = "total_rows", nullable = false)
    @Builder.Default
    private Integer totalRows = 0;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DatasetVersionStatus status = DatasetVersionStatus.RUNNING;

    /** Timestamp when the ETL run finished (COMPLETED or FAILED). */
    @Column(name = "completed_at")
    private Instant completedAt;

    /** All feature rows that belong to this version. */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", referencedColumnName = "id",
                insertable = false, updatable = false)
    @Builder.Default
    private List<GradePredictionDataset> rows = new ArrayList<>();

    // ── Status lifecycle ──────────────────────────────────────────────────

    public enum DatasetVersionStatus {
        /** ETL is still running / data is being written. */
        RUNNING,
        /** All rows written successfully; version is ready for training. */
        COMPLETED,
        /** ETL run failed; version should not be used for training. */
        FAILED
    }

    /**
     * Marks the version as COMPLETED and captures the row count
     * from the in-memory collection if it has been loaded.
     */
    public void complete(int rowCount) {
        this.status = DatasetVersionStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.totalRows = rowCount;
    }

    /**
     * Marks the version as FAILED.
     */
    public void fail() {
        this.status = DatasetVersionStatus.FAILED;
        this.completedAt = Instant.now();
    }

    public boolean isCompleted() {
        return DatasetVersionStatus.COMPLETED.equals(this.status);
    }

    public boolean isRunning() {
        return DatasetVersionStatus.RUNNING.equals(this.status);
    }

    public boolean isFailed() {
        return DatasetVersionStatus.FAILED.equals(this.status);
    }
}
