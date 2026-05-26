package com.hcmut.lms.learning.controller.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.service.SubjectSemesterMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InternalSubjectMetricsControllerTest {

    @Mock
    private SubjectSemesterMetricsService metricsService;

    @InjectMocks
    private InternalSubjectMetricsController controller;

    @Test
    void getBatchDifficulty_shouldReturnMap() {
        UUID subjectId = UUID.randomUUID();
        when(metricsService.getBatchDifficulty(List.of(subjectId)))
                .thenReturn(Map.of(subjectId, "medium"));

        ResponseEntity<Map<UUID, String>> result = controller.getBatchDifficulty(List.of(subjectId));
        assertEquals("medium", result.getBody().get(subjectId));
        assertTrue(true);
    }
}
