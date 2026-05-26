package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.client.GeminiEmbeddingClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeminiEmbeddingServiceImplTest {

    @Mock private GeminiEmbeddingClient geminiEmbeddingClient;

    @InjectMocks
    private GeminiEmbeddingServiceImpl geminiEmbeddingService;

    @Test void embedTexts_shouldReturnEmbeddings_whenValidInput() {
        when(geminiEmbeddingClient.getEmbeddingDimension()).thenReturn(768);
        List<Float> floats = new ArrayList<>();
        floats.add(0.1f);
        floats.add(0.2f);
        when(geminiEmbeddingClient.generateEmbeddings(any(), anyString()))
            .thenReturn(List.of(floats));

        List<float[]> result = geminiEmbeddingService.embedTexts(List.of("test"), "RETRIEVAL_DOCUMENT");
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).length == 768);
    }

    @Test void embedTexts_shouldReturnEmpty_whenEmptyInput() {
        List<float[]> result = geminiEmbeddingService.embedTexts(Collections.emptyList(), "RETRIEVAL_DOCUMENT");
        assertTrue(result.isEmpty());
    }

    @Test void getEmbeddingDimension_shouldReturnDimension() {
        when(geminiEmbeddingClient.getEmbeddingDimension()).thenReturn(768);
        int dim = geminiEmbeddingService.getEmbeddingDimension();
        assertTrue(dim == 768);
    }
}
