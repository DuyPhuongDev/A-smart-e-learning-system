package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;
import com.hcmut.lms.learning.repository.GradeSemesterMetricsRepository;
import com.hcmut.lms.learning.service.GradeSemesterMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeSemesterMetricsServiceImpl implements GradeSemesterMetricsService {

  private static final int BATCH_SIZE = 500;

  private final GradeSemesterMetricsRepository gradeSemesterMetricsRepository;

  @Override
  @Transactional
  public List<GradeSemesterMetrics> replaceAll(List<GradeSemesterMetrics> metrics) {
    log.info("Replacing all grade semester metrics. New count: {}", metrics.size());

    // Delete existing data first, then insert new data in batches.
    // This runs within a transaction, so a failure rolls back both delete and inserts.
    gradeSemesterMetricsRepository.deleteAllInBatch();

    List<GradeSemesterMetrics> saved = new ArrayList<>();
    for (int i = 0; i < metrics.size(); i += BATCH_SIZE) {
      int end = Math.min(i + BATCH_SIZE, metrics.size());
      saved.addAll(gradeSemesterMetricsRepository.saveAll(metrics.subList(i, end)));
    }

    log.info("Saved {} grade semester metrics records", saved.size());
    return saved;
  }

  @Override
  @Transactional
  public void upsertAll(List<GradeSemesterMetrics> metrics) {
    log.info("Upserting {} grade semester metrics records", metrics.size());

    for (int i = 0; i < metrics.size(); i += BATCH_SIZE) {
      int end = Math.min(i + BATCH_SIZE, metrics.size());
      List<GradeSemesterMetrics> batch = metrics.subList(i, end);

      UUID[] semesterIds = batch.stream().map(GradeSemesterMetrics::getSemesterId).toArray(UUID[]::new);
      Integer[] semKeys = batch.stream().map(GradeSemesterMetrics::getSemKey).toArray(Integer[]::new);
      Double[] medianGrades = batch.stream().map(GradeSemesterMetrics::getMedianGrade).toArray(Double[]::new);
      Double[] meanGrades = batch.stream().map(GradeSemesterMetrics::getMeanGrade).toArray(Double[]::new);
      Integer[] sampleCounts = batch.stream().map(GradeSemesterMetrics::getSampleCount).toArray(Integer[]::new);

      gradeSemesterMetricsRepository.batchUpsert(semesterIds, semKeys, medianGrades, meanGrades, sampleCounts);
      log.debug("Upserted batch {}/{}", end, metrics.size());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<GradeSemesterMetrics> findBySemesterId(UUID semesterId) {
    return gradeSemesterMetricsRepository.findBySemesterId(semesterId);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<GradeSemesterMetrics> findClosestBySemKey(int targetSemKey) {
    return gradeSemesterMetricsRepository.findClosestBySemKey(targetSemKey);
  }
}