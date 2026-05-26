package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.client.GeminiEmbeddingClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmbeddingServiceImplTest {

    @Mock private GeminiEmbeddingClient geminiEmbeddingClient;

    @InjectMocks
    private EmbeddingServiceImpl embeddingService;

    @Test
    void generateEmbedding_shouldReturnEmbedding_whenValidText() {
        List<Float> expectedEmbedding = List.of(0.1f, 0.2f, 0.3f);
        when(geminiEmbeddingClient.generateEmbedding("test text")).thenReturn(expectedEmbedding);

        List<Float> result = embeddingService.generateEmbedding("test text");
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void generateEmbedding_shouldThrowException_whenTextIsNull() {
        assertThrows(IllegalArgumentException.class, () -> embeddingService.generateEmbedding(null));
    }

    @Test
    void generateEmbedding_shouldThrowException_whenTextIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> embeddingService.generateEmbedding("   "));
    }

    @Test
    void generateEmbedding_shouldThrowException_whenClientFails() {
        when(geminiEmbeddingClient.generateEmbedding("bad text"))
                .thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> embeddingService.generateEmbedding("bad text"));
    }

    @Test
    void generateEmbeddings_shouldReturnBatchEmbeddings_whenValidInput() {
        List<String> texts = List.of("text1", "text2");
        List<List<Float>> expected = List.of(List.of(0.1f), List.of(0.2f));
        when(geminiEmbeddingClient.generateEmbeddings(texts)).thenReturn(expected);

        List<List<Float>> result = embeddingService.generateEmbeddings(texts);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void generateEmbeddings_shouldReturnEmptyList_whenInputIsEmpty() {
        List<List<Float>> result = embeddingService.generateEmbeddings(List.of());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void generateEmbeddings_shouldReturnEmptyList_whenInputIsNull() {
        List<List<Float>> result = embeddingService.generateEmbeddings(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void generateEmbeddings_shouldThrowException_whenClientFails() {
        when(geminiEmbeddingClient.generateEmbeddings(any()))
                .thenThrow(new RuntimeException("Batch API error"));

        assertThrows(RuntimeException.class, () -> embeddingService.generateEmbeddings(List.of("text")));
    }

    @Test
    void getEmbeddingDimension_shouldReturnCorrectDimension() {
        when(geminiEmbeddingClient.getEmbeddingDimension()).thenReturn(768);

        int dimension = embeddingService.getEmbeddingDimension();
        assertEquals(768, dimension);
    }
}
