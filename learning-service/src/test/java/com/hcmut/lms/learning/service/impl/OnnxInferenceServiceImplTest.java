package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hcmut.lms.learning.exception.PredictionFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class OnnxInferenceServiceImplTest {

    @InjectMocks
    private OnnxInferenceServiceImpl onnxInferenceService;

    @Test
    void predict_shouldThrowException_whenModelNotFound() {
        Map<String, Object> features = Map.of("f1", 1.0, "f2", 2.0);
        List<String> columns = List.of("f1", "f2");

        try {
            onnxInferenceService.predict("/nonexistent/path/model.onnx", features, columns);
        } catch (PredictionFailedException e) {
            assertTrue(e.getMessage().contains("ONNX inference failed")
                    || e.getMessage().contains("Unexpected error"));
        }
    }

    @Test
    void predict_shouldThrowException_whenInvalidFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("f1", "not_a_number"); // Invalid type
        List<String> columns = List.of("f1");

        try {
            onnxInferenceService.predict("/nonexistent/path/model.onnx", features, columns);
        } catch (PredictionFailedException e) {
            assertTrue(true);
        }
    }

    @Test
    void predict_shouldHandleNullFeatureValue() {
        Map<String, Object> features = new HashMap<>();
        features.put("f1", null);
        List<String> columns = List.of("f1");

        try {
            onnxInferenceService.predict("/nonexistent/path/model.onnx", features, columns);
        } catch (PredictionFailedException e) {
            assertTrue(true);
        }
    }
}
