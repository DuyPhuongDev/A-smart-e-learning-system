package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.dto.request.RawFeaturePredictionRequest;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.service.GradePredictionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GradePredictionControllerTest {

    @Mock
    private GradePredictionService gradePredictionService;

    @InjectMocks
    private GradePredictionController controller;

    private final UUID studentId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();

    @Test
    void predictGrade_shouldReturnResponse() {
        when(gradePredictionService.predictGrade(studentId, subjectId, null, null))
                .thenReturn(new GradePredictionResponse());

        ResponseEntity<GradePredictionResponse> result = controller.predictGrade(studentId, subjectId, null, null);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void predictGradeFromRawFeatures_shouldReturnResponse() {
        RawFeaturePredictionRequest request = new RawFeaturePredictionRequest();
        request.setSemCredits(17);
        request.setRetakeNo(0);
        request.setNumSemestersPrior(2);
        request.setCumulativeGradeAvg(2.5);
        request.setPreviousSemGradeAvg(2.3);
        request.setSubjectHistMedianSmooth(2.4);
        request.setRelativeAvgCourseGrade(2.6);

        when(gradePredictionService.predictGradeFromRawFeatures(request))
                .thenReturn(new GradePredictionResponse());

        ResponseEntity<GradePredictionResponse> result = controller.predictGradeFromRawFeatures(request);
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void predictMyGrade_shouldReturnNotImplemented() {
        ResponseEntity<GradePredictionResponse> result = controller.predictMyGrade(subjectId, null, null);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, result.getStatusCode());
        assertTrue(true);
    }
}
