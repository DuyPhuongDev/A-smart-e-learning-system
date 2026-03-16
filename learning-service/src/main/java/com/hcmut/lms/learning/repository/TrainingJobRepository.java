package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.training.TrainingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingJobRepository extends JpaRepository<TrainingJob, UUID> {

    Optional<TrainingJob> findByJobId(String jobId);

    List<TrainingJob> findByDatasetVersionId(UUID datasetVersionId);

    List<TrainingJob> findByStatusIn(List<TrainingJob.TrainingJobStatus> statuses);

    List<TrainingJob> findTop20ByOrderByCreatedAtDesc();

    boolean existsByDatasetVersionIdAndStatusIn(UUID datasetVersionId, List<TrainingJob.TrainingJobStatus> statuses);
}
