package com.hcmut.lms.learning.entity.training;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "training_metrics")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingMetrics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "training_job_id", nullable = false)
    private UUID trainingJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_job_id", insertable = false, updatable = false)
    private TrainingJob trainingJob;

    @Column(name = "metric_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @Column(name = "dataset_split", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private DatasetSplit datasetSplit;

    @Column(name = "scale_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ScaleType scaleType;

    @Column(name = "mae")
    private Double mae;

    @Column(name = "rmse")
    private Double rmse;

    @Column(name = "r2")
    private Double r2;

    @Column(name = "mu_error")
    private Double muError;

    @Column(name = "sigma_error")
    private Double sigmaError;

    @Column(name = "sample_count")
    private Integer sampleCount;

    public enum MetricType {
        baseline,
        model
    }

    public enum DatasetSplit {
        train,
        test
    }

    public enum ScaleType {
        FOUR_POINT  // Model only trains on 4-point scale (0.0-4.0)
    }
}
