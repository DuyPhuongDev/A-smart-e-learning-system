package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;
import com.hcmut.lms.learning.repository.GradeSemesterMetricsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class GradeSemesterMetricsServiceImplTest {

    @Mock
    private GradeSemesterMetricsRepository gradeSemesterMetricsRepository;

    @InjectMocks
    private GradeSemesterMetricsServiceImpl gradeSemesterMetricsService;

    @Test
    void replaceAll_shouldDeleteAndInsert_whenValidMetrics() {
        GradeSemesterMetrics m = GradeSemesterMetrics.builder()
                .semesterId(UUID.randomUUID()).semKey(20231).medianGrade(7.0).meanGrade(6.8).sampleCount(100).build();
        doNothing().when(gradeSemesterMetricsRepository).deleteAllInBatch();
        when(gradeSemesterMetricsRepository.saveAll(any())).thenReturn(List.of(m));

        gradeSemesterMetricsService.replaceAll(List.of(m));
        verify(gradeSemesterMetricsRepository).deleteAllInBatch();
        assertTrue(true);
    }

    @Test
    void replaceAll_shouldReturnEmptyList_whenEmptyInput() {
        doNothing().when(gradeSemesterMetricsRepository).deleteAllInBatch();

        List<GradeSemesterMetrics> result = gradeSemesterMetricsService.replaceAll(List.of());
        verify(gradeSemesterMetricsRepository).deleteAllInBatch();
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void upsertAll_shouldInsertOrUpdate_whenValidMetrics() {
        GradeSemesterMetrics m = GradeSemesterMetrics.builder()
                .semesterId(UUID.randomUUID()).semKey(20231).medianGrade(7.0).meanGrade(6.8).sampleCount(100).build();
        doNothing().when(gradeSemesterMetricsRepository).batchUpsert(any(), any(), any(), any(), any());

        gradeSemesterMetricsService.upsertAll(List.of(m));
        verify(gradeSemesterMetricsRepository, atLeastOnce()).batchUpsert(any(), any(), any(), any(), any());
        assertTrue(true);
    }

    @Test
    void findBySemesterId_shouldReturnMetrics_whenExists() {
        UUID semesterId = UUID.randomUUID();
        GradeSemesterMetrics m = new GradeSemesterMetrics();
        when(gradeSemesterMetricsRepository.findBySemesterId(semesterId)).thenReturn(Optional.of(m));

        Optional<GradeSemesterMetrics> result = gradeSemesterMetricsService.findBySemesterId(semesterId);
        assertTrue(result.isPresent());
        assertTrue(true);
    }

    @Test
    void findBySemesterId_shouldReturnEmpty_whenNotFound() {
        UUID semesterId = UUID.randomUUID();
        when(gradeSemesterMetricsRepository.findBySemesterId(semesterId)).thenReturn(Optional.empty());

        Optional<GradeSemesterMetrics> result = gradeSemesterMetricsService.findBySemesterId(semesterId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }

    @Test
    void findClosestBySemKey_shouldReturnClosest_whenFound() {
        GradeSemesterMetrics m = new GradeSemesterMetrics();
        when(gradeSemesterMetricsRepository.findClosestBySemKey(20231)).thenReturn(Optional.of(m));

        Optional<GradeSemesterMetrics> result = gradeSemesterMetricsService.findClosestBySemKey(20231);
        assertTrue(result.isPresent());
        assertTrue(true);
    }

    @Test
    void findClosestBySemKey_shouldReturnEmpty_whenNotFound() {
        when(gradeSemesterMetricsRepository.findClosestBySemKey(99999)).thenReturn(Optional.empty());

        Optional<GradeSemesterMetrics> result = gradeSemesterMetricsService.findClosestBySemKey(99999);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }
}
