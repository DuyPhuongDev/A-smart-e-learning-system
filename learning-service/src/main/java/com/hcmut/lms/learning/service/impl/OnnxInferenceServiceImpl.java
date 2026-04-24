package com.hcmut.lms.learning.service.impl;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.hcmut.lms.learning.exception.PredictionFailedException;
import com.hcmut.lms.learning.service.OnnxInferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OnnxInferenceServiceImpl implements OnnxInferenceService {

    private final OrtEnvironment env;

    @Value("${model.onnx.session.threads:4}")
    private int sessionThreads;

    public OnnxInferenceServiceImpl() {
        this.env = OrtEnvironment.getEnvironment();
    }

    @Override
    public double predict(String modelPath, Map<String, Object> features, List<String> featureColumns) {
        try {
            // Create session options
            OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
            sessionOptions.setIntraOpNumThreads(sessionThreads);

            // Create session
            try (OrtSession session = env.createSession(modelPath, sessionOptions)) {

                // 1. Build feature vector (handle missing values with NaN)
                float[] featureVector = new float[featureColumns.size()];
                for (int i = 0; i < featureColumns.size(); i++) {
                    String col = featureColumns.get(i);
                    Object value = features.get(col);

                  switch (value) {
                    case null -> featureVector[i] = Float.NaN;
                    case Number number -> featureVector[i] = number.floatValue();
                    case Boolean b -> featureVector[i] = b ? 1.0f : 0.0f;
                    default -> {
                      log.warn("Unexpected feature type for {}: {}", col, value.getClass());
                      featureVector[i] = Float.NaN;
                    }
                  }
                }

                // 2. Create ONNX tensor (shape: [1, n_features])
                long[] shape = {1, featureColumns.size()};
                OnnxTensor inputTensor = OnnxTensor.createTensor(env, new float[][]{featureVector});

                // 3. Run inference
                Map<String, OnnxTensor> inputs = Map.of("float_input", inputTensor);
                OrtSession.Result result = session.run(inputs);

                // 4. Extract prediction
                float[][] output = (float[][]) result.get(0).getValue();
                double prediction = output[0][0];

                log.debug("Prediction: {}", prediction);

                // Clean up
                inputTensor.close();
                result.close();

                return prediction;

            }

        } catch (OrtException e) {
            log.error("ONNX inference failed", e);
            throw new PredictionFailedException("ONNX inference failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during inference", e);
            throw new PredictionFailedException("Unexpected error during inference", e);
        }
    }
}
