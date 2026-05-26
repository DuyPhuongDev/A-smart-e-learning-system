package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SubjectSemesterMetricsControllerTest {

    @Mock
    private SubjectSemesterMetricsService metricsService;

    @InjectMocks
    private SubjectSemesterMetricsController controller;

    private final UUID subjectId = UUID.randomUUID();
    private final UUID semesterId = UUID.randomUUID();

    @Test
    void computeAllMetrics_shouldReturnSuccessMap() {
        when(metricsService.computeAndSaveAllMetrics()).thenReturn(42);

        ResponseEntity<Map<String, Object>> result = controller.computeAllMetrics();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody().get("status"));
        assertEquals(42, result.getBody().get("metricsComputed"));
        assertTrue(true);
    }

    @Test
    void getMetrics_shouldReturnMetrics_whenExists() {
        SubjectSemesterMetrics metrics = new SubjectSemesterMetrics();
        when(metricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.of(metrics));

        ResponseEntity<?> result = controller.getMetrics(subjectId, semesterId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void getMetrics_shouldReturnNotFound_whenNotExists() {
        when(metricsService.findBySubjectIdAndSemesterId(subjectId, semesterId))
                .thenReturn(Optional.empty());

        ResponseEntity<?> result = controller.getMetrics(subjectId, semesterId);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void getSubjectHistory_shouldReturnList() {
        when(metricsService.getMetricsHistoryForSubject(subjectId)).thenReturn(List.of());

        ResponseEntity<List<SubjectSemesterMetrics>> result = controller.getSubjectHistory(subjectId);
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
