package com.hcmut.lms.personalization.application.service;

import java.util.List;

public interface EmbeddingService {
    List<float[]> embedTexts(List<String> texts, String taskType);
    int getEmbeddingDimension();
}
