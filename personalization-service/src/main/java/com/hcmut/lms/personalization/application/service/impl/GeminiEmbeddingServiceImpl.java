package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.service.EmbeddingService;
import com.hcmut.lms.personalization.client.GeminiEmbeddingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class GeminiEmbeddingServiceImpl implements EmbeddingService {

    private final GeminiEmbeddingClient geminiEmbeddingClient;

    @Override
    public List<float[]> embedTexts(List<String> texts, String taskType) {
        if (texts == null || texts.isEmpty()) {
            return List.of();
        }
        List<List<Float>> floats = geminiEmbeddingClient.generateEmbeddings(texts, taskType);
        int dimension = getEmbeddingDimension();
        List<float[]> vectors = new ArrayList<>(floats.size());
        for (List<Float> f : floats) {
            float[] arr = new float[dimension];
            if (f != null) {
                int limit = Math.min(dimension, f.size());
                for (int i = 0; i < limit; i++) {
                    arr[i] = f.get(i);
                }
            }
            vectors.add(arr);
        }
        return vectors;
    }

    @Override
    public int getEmbeddingDimension() {
        return geminiEmbeddingClient.getEmbeddingDimension();
    }
}
