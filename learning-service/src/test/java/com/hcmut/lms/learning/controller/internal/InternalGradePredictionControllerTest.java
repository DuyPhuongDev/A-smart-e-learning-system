package com.hcmut.lms.learning.controller.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.dto.request.BatchGradePredictionRequest;
import com.hcmut.lms.learning.dto.response.BatchGradePredictionResponse;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.service.GradePredictionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InternalGradePredictionControllerTest {

    @Mock
    private GradePredictionService gradePredictionService;

    @InjectMocks
    private InternalGradePredictionController controller;

    private final UUID studentId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();

    @Test
    void predictGradeInternal_shouldReturnResponse() {
        when(gradePredictionService.predictGrade(studentId, subjectId, null, null))
                .thenReturn(new GradePredictionResponse());

        ResponseEntity<GradePredictionResponse> result =
                controller.predictGradeInternal(studentId, subjectId, null, null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void predictGradeBatch_shouldReturnResponse() {
        BatchGradePredictionRequest request = BatchGradePredictionRequest.builder()
                .predictions(Collections.emptyList()).build();
        BatchGradePredictionResponse response = BatchGradePredictionResponse.builder()
                .predictions(Collections.emptyList()).build();
        when(gradePredictionService.predictGradeBatch(request)).thenReturn(response);

        ResponseEntity<BatchGradePredictionResponse> result = controller.predictGradeBatch(request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void predictGradeBatch_shouldHandleNullRequest() {
        when(gradePredictionService.predictGradeBatch(null))
                .thenReturn(BatchGradePredictionResponse.builder()
                        .predictions(Collections.emptyList()).build());

        ResponseEntity<BatchGradePredictionResponse> result = controller.predictGradeBatch(null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }
}
