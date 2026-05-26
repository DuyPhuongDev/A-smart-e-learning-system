package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.dto.request.DocumentEnrichmentCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.DocumentEnrichmentCallbackResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.DocumentEnrichmentCallbackMapper;
import com.hcmut.lms.coachingchatbot.application.service.EmbeddingService;
import com.hcmut.lms.coachingchatbot.application.service.QdrantVectorStoreService;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class DocumentEnrichmentCallbackServiceImplTest {

    @Mock private LectureKnowledgeRepository lectureKnowledgeRepository;
    @Mock private LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository;
    @Mock private DocumentEnrichmentCallbackMapper documentEnrichmentCallbackMapper;
    @Mock private EmbeddingService embeddingService;
    @Mock private QdrantVectorStoreService vectorStoreService;

    @InjectMocks
    private DocumentEnrichmentCallbackServiceImpl documentEnrichmentCallbackService;

    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID lectureKnowledgeId = UUID.randomUUID();

    @Test
    void processEnrichmentCallback_shouldReturnSuccess_whenNewKnowledge() throws Exception {
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryMaxAttempts", 1);
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryDelayMs", 0L);

        DocumentEnrichmentCallbackRequest.EnrichedChunk enrichedChunk =
                DocumentEnrichmentCallbackRequest.EnrichedChunk.builder()
                        .chunkIndex(0).chunkContent("enriched content").tokenCount(100).build();
        DocumentEnrichmentCallbackRequest request = DocumentEnrichmentCallbackRequest.builder()
                .lectureId(lectureId).contentType("DOCUMENT").chunks(List.of(enrichedChunk)).build();

        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);

        LectureKnowledgeChunk savedChunk = new LectureKnowledgeChunk();
        savedChunk.setId(UUID.randomUUID());
        savedChunk.setChunkContent("enriched content");
        savedChunk.setChunkIndex(0);
        savedChunk.setQdrantPointId(UUID.randomUUID());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(null);
        when(documentEnrichmentCallbackMapper.toLectureKnowledge(lectureId, "DOCUMENT")).thenReturn(lectureKnowledge);
        when(lectureKnowledgeRepository.save(any())).thenReturn(lectureKnowledge);
        when(lectureKnowledgeChunkRepository.countByLectureKnowledgeLectureKnowledgeId(lectureId)).thenReturn(0L);
        when(documentEnrichmentCallbackMapper.toChunkEntities(any(), any())).thenReturn(List.of(savedChunk));
        when(lectureKnowledgeChunkRepository.saveAll(any())).thenReturn(List.of(savedChunk));
        when(embeddingService.generateEmbeddings(any())).thenReturn(List.of(List.of(0.1f, 0.2f)));
        doNothing().when(vectorStoreService).upsertPoints(anyString(), any());

        CompletableFuture<DocumentEnrichmentCallbackResponse> future =
                documentEnrichmentCallbackService.processEnrichmentCallback(request);
        DocumentEnrichmentCallbackResponse result = future.get();

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(lectureId, result.getLectureId());
        assertEquals(1, result.getChunksSaved());
    }

    @Test
    void processEnrichmentCallback_shouldUseExistingKnowledge_whenAlreadyExists() throws Exception {
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryMaxAttempts", 1);
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryDelayMs", 0L);

        DocumentEnrichmentCallbackRequest.EnrichedChunk enrichedChunk =
                DocumentEnrichmentCallbackRequest.EnrichedChunk.builder()
                        .chunkIndex(0).chunkContent("content").build();
        DocumentEnrichmentCallbackRequest request = DocumentEnrichmentCallbackRequest.builder()
                .lectureId(lectureId).contentType("TEXT").chunks(List.of(enrichedChunk)).build();

        LectureKnowledge existingKnowledge = new LectureKnowledge();
        existingKnowledge.setLectureKnowledgeId(lectureKnowledgeId);

        LectureKnowledgeChunk savedChunk = new LectureKnowledgeChunk();
        savedChunk.setId(UUID.randomUUID());
        savedChunk.setChunkContent("content");
        savedChunk.setChunkIndex(0);
        savedChunk.setQdrantPointId(UUID.randomUUID());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(existingKnowledge);
        when(lectureKnowledgeChunkRepository.countByLectureKnowledgeLectureKnowledgeId(lectureId)).thenReturn(5L);
        doNothing().when(lectureKnowledgeChunkRepository).deleteByLectureKnowledgeLectureKnowledgeId(lectureId);
        when(documentEnrichmentCallbackMapper.toChunkEntities(any(), any())).thenReturn(List.of(savedChunk));
        when(lectureKnowledgeChunkRepository.saveAll(any())).thenReturn(List.of(savedChunk));
        when(embeddingService.generateEmbeddings(any())).thenReturn(List.of(List.of(0.1f)));
        doNothing().when(vectorStoreService).upsertPoints(anyString(), any());

        CompletableFuture<DocumentEnrichmentCallbackResponse> future =
                documentEnrichmentCallbackService.processEnrichmentCallback(request);
        DocumentEnrichmentCallbackResponse result = future.get();

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        verify(documentEnrichmentCallbackMapper, never()).toLectureKnowledge(any(), any());
        verify(lectureKnowledgeChunkRepository).deleteByLectureKnowledgeLectureKnowledgeId(lectureId);
    }

    @Test
    void processEnrichmentCallback_shouldReturnFailure_whenExceptionOccurs() throws Exception {
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryMaxAttempts", 1);
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryDelayMs", 0L);

        DocumentEnrichmentCallbackRequest request = DocumentEnrichmentCallbackRequest.builder()
                .lectureId(lectureId).contentType("DOCUMENT").chunks(List.of()).build();

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId))
                .thenThrow(new RuntimeException("DB connection error"));

        CompletableFuture<DocumentEnrichmentCallbackResponse> future =
                documentEnrichmentCallbackService.processEnrichmentCallback(request);
        DocumentEnrichmentCallbackResponse result = future.get();

        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
        assertTrue(result.getMessage().contains("DB connection error"));
    }

    @Test
    void processEnrichmentCallback_shouldHandleNullChunks() throws Exception {
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryMaxAttempts", 1);
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryDelayMs", 0L);

        DocumentEnrichmentCallbackRequest request = DocumentEnrichmentCallbackRequest.builder()
                .lectureId(lectureId).contentType("TEXT").chunks(null).build();

        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(lectureKnowledge);
        when(lectureKnowledgeChunkRepository.countByLectureKnowledgeLectureKnowledgeId(lectureId)).thenReturn(0L);
        when(documentEnrichmentCallbackMapper.toChunkEntities(any(), any())).thenReturn(List.of());
        when(lectureKnowledgeChunkRepository.saveAll(any())).thenReturn(List.of());
        when(embeddingService.generateEmbeddings(any())).thenReturn(List.of());
        doNothing().when(vectorStoreService).upsertPoints(anyString(), any());

        CompletableFuture<DocumentEnrichmentCallbackResponse> future =
                documentEnrichmentCallbackService.processEnrichmentCallback(request);
        DocumentEnrichmentCallbackResponse result = future.get();

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(0, result.getChunksSaved());
    }

    @Test
    void processEnrichmentCallback_shouldRetryQdrantSync_whenFirstAttemptFails() throws Exception {
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryMaxAttempts", 2);
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryDelayMs", 0L);

        DocumentEnrichmentCallbackRequest.EnrichedChunk enrichedChunk =
                DocumentEnrichmentCallbackRequest.EnrichedChunk.builder()
                        .chunkIndex(0).chunkContent("content").build();
        DocumentEnrichmentCallbackRequest request = DocumentEnrichmentCallbackRequest.builder()
                .lectureId(lectureId).contentType("DOCUMENT").chunks(List.of(enrichedChunk)).build();

        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);

        LectureKnowledgeChunk savedChunk = new LectureKnowledgeChunk();
        savedChunk.setId(UUID.randomUUID());
        savedChunk.setChunkContent("content");
        savedChunk.setChunkIndex(0);
        savedChunk.setQdrantPointId(UUID.randomUUID());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(lectureKnowledge);
        when(lectureKnowledgeChunkRepository.countByLectureKnowledgeLectureKnowledgeId(lectureId)).thenReturn(0L);
        when(documentEnrichmentCallbackMapper.toChunkEntities(any(), any())).thenReturn(List.of(savedChunk));
        when(lectureKnowledgeChunkRepository.saveAll(any())).thenReturn(List.of(savedChunk));
        when(embeddingService.generateEmbeddings(any())).thenReturn(List.of(List.of(0.1f)));
        // First call throws, second succeeds
        doThrow(new RuntimeException("Qdrant temp error"))
                .doNothing()
                .when(vectorStoreService).upsertPoints(anyString(), any());

        CompletableFuture<DocumentEnrichmentCallbackResponse> future =
                documentEnrichmentCallbackService.processEnrichmentCallback(request);
        DocumentEnrichmentCallbackResponse result = future.get();

        assertEquals("SUCCESS", result.getStatus());
        verify(vectorStoreService, times(2)).upsertPoints(anyString(), any());
    }

    @Test
    void processEnrichmentCallback_shouldFail_whenAllQdrantSyncRetriesFail() throws Exception {
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryMaxAttempts", 2);
        ReflectionTestUtils.setField(documentEnrichmentCallbackService, "retryDelayMs", 0L);

        DocumentEnrichmentCallbackRequest.EnrichedChunk enrichedChunk =
                DocumentEnrichmentCallbackRequest.EnrichedChunk.builder()
                        .chunkIndex(0).chunkContent("content").build();
        DocumentEnrichmentCallbackRequest request = DocumentEnrichmentCallbackRequest.builder()
                .lectureId(lectureId).contentType("DOCUMENT").chunks(List.of(enrichedChunk)).build();

        LectureKnowledge lectureKnowledge = new LectureKnowledge();
        lectureKnowledge.setLectureKnowledgeId(lectureKnowledgeId);

        LectureKnowledgeChunk savedChunk = new LectureKnowledgeChunk();
        savedChunk.setId(UUID.randomUUID());
        savedChunk.setChunkContent("content");
        savedChunk.setChunkIndex(0);
        savedChunk.setQdrantPointId(UUID.randomUUID());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(lectureKnowledge);
        when(lectureKnowledgeChunkRepository.countByLectureKnowledgeLectureKnowledgeId(lectureId)).thenReturn(0L);
        when(documentEnrichmentCallbackMapper.toChunkEntities(any(), any())).thenReturn(List.of(savedChunk));
        when(lectureKnowledgeChunkRepository.saveAll(any())).thenReturn(List.of(savedChunk));
        when(embeddingService.generateEmbeddings(any())).thenReturn(List.of(List.of(0.1f)));
        doThrow(new RuntimeException("Qdrant permanent error"))
                .when(vectorStoreService).upsertPoints(anyString(), any());

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(lectureKnowledge);

        CompletableFuture<DocumentEnrichmentCallbackResponse> future =
                documentEnrichmentCallbackService.processEnrichmentCallback(request);
        DocumentEnrichmentCallbackResponse result = future.get();

        assertEquals("FAILED", result.getStatus());
        assertTrue(result.getMessage().contains("Qdrant"));
    }
}
