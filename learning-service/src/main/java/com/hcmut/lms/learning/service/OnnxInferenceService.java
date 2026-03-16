package com.hcmut.lms.learning.service;

import java.util.List;
import java.util.Map;

public interface OnnxInferenceService {
    /**
     * Run inference using ONNX model.
     *
     * @param modelPath Path to .onnx file
     * @param features Feature map (47 features)
     * @param featureColumns Ordered list of feature column names
     * @return Predicted grade (0.0-4.0 scale)
     */
    double predict(String modelPath, Map<String, Object> features, List<String> featureColumns);
}
