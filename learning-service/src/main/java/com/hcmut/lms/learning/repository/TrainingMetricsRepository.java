package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.training.TrainingMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingMetricsRepository extends JpaRepository<TrainingMetrics, UUID> {

    List<TrainingMetrics> findByTrainingJobId(UUID trainingJobId);
}
