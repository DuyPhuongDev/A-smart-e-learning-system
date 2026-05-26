package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.service.EmbeddingService;
import com.hcmut.lms.coachingchatbot.application.service.KnowledgeSearchService.SearchResult;
import com.hcmut.lms.coachingchatbot.application.service.QdrantVectorStoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class KnowledgeSearchServiceImplTest {

    @Mock private EmbeddingService embeddingService;
    @Mock private QdrantVectorStoreService vectorStoreService;

    @InjectMocks
    private KnowledgeSearchServiceImpl knowledgeSearchService;

    private static final UUID chunkId = UUID.randomUUID();
    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID chapterId = UUID.randomUUID();
    private static final List<Float> queryVector = List.of(0.1f, 0.2f, 0.3f);

    private QdrantVectorStoreService.SearchResult buildQdrantResult(float score) {
        return new QdrantVectorStoreService.SearchResult(chunkId, score, Map.of(
                "chunkId", chunkId.toString(),
                "lectureKnowledgeId", lectureId.toString(),
                "content", "Test content",
                "chunkIndex", 0));
    }

    @Test
    void search_shouldReturnResults_whenVectorMatches() {
        ReflectionTestUtils.setField(knowledgeSearchService, "collectionName", "lecture_knowledge");
        when(embeddingService.generateEmbedding("test query")).thenReturn(queryVector);
        when(vectorStoreService.search(eq("lecture_knowledge"), eq(queryVector), eq(5)))
                .thenReturn(List.of(buildQdrantResult(0.95f)));

        List<SearchResult> results = knowledgeSearchService.search("test query", 5);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(0.95f, results.get(0).score(), 0.01f);
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatch() {
        when(embeddingService.generateEmbedding("no match query")).thenReturn(queryVector);
        when(vectorStoreService.search(any(), any(), anyInt())).thenReturn(List.of());

        List<SearchResult> results = knowledgeSearchService.search("no match query", 5);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void search_shouldThrowException_whenEmbeddingFails() {
        when(embeddingService.generateEmbedding(any()))
                .thenThrow(new RuntimeException("Embedding error"));

        assertThrows(RuntimeException.class, () -> knowledgeSearchService.search("bad query", 5));
    }

    @Test
    void searchInLecture_shouldReturnFilteredResults_whenLectureIdProvided() {
        ReflectionTestUtils.setField(knowledgeSearchService, "collectionName", "lecture_knowledge");
        when(embeddingService.generateEmbedding("lecture query")).thenReturn(queryVector);
        when(vectorStoreService.searchWithFilter(eq("lecture_knowledge"), eq(queryVector), anyMap(), eq(5)))
                .thenReturn(List.of(buildQdrantResult(0.85f)));

        List<SearchResult> results = knowledgeSearchService.searchInLecture(lectureId, "lecture query", 5);
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void searchInLecture_shouldThrowException_whenSearchFails() {
        when(embeddingService.generateEmbedding(any()))
                .thenThrow(new RuntimeException("Search failed"));

        assertThrows(RuntimeException.class,
                () -> knowledgeSearchService.searchInLecture(lectureId, "bad query", 5));
    }

    @Test
    void searchInChapter_shouldReturnFilteredResults_whenChapterIdProvided() {
        ReflectionTestUtils.setField(knowledgeSearchService, "collectionName", "lecture_knowledge");
        when(embeddingService.generateEmbedding("chapter query")).thenReturn(queryVector);
        when(vectorStoreService.searchWithFilter(eq("lecture_knowledge"), eq(queryVector), anyMap(), eq(3)))
                .thenReturn(List.of(buildQdrantResult(0.75f)));

        List<SearchResult> results = knowledgeSearchService.searchInChapter(chapterId, "chapter query", 3);
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void searchInChapter_shouldThrowException_whenSearchFails() {
        when(embeddingService.generateEmbedding(any()))
                .thenThrow(new RuntimeException("Chapter search failed"));

        assertThrows(RuntimeException.class,
                () -> knowledgeSearchService.searchInChapter(chapterId, "bad query", 3));
    }

    @Test
    void search_shouldHandleNullChunkIdInPayload() {
        QdrantVectorStoreService.SearchResult qdrantResult = new QdrantVectorStoreService.SearchResult(
                chunkId, 0.5f, Map.of(
                "lectureKnowledgeId", lectureId.toString(),
                "content", "Some content",
                "chunkIndex", 0));

        when(embeddingService.generateEmbedding("q")).thenReturn(queryVector);
        when(vectorStoreService.search(any(), any(), anyInt())).thenReturn(List.of(qdrantResult));

        List<SearchResult> results = knowledgeSearchService.search("q", 1);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertNull(results.get(0).chunkId());
    }

    @Test
    void search_shouldHandleNonNumberChunkIndex() {
        QdrantVectorStoreService.SearchResult qdrantResult = new QdrantVectorStoreService.SearchResult(
                chunkId, 0.6f, Map.of(
                "chunkId", chunkId.toString(),
                "lectureKnowledgeId", lectureId.toString(),
                "content", "Content",
                "chunkIndex", "not_a_number"));

        when(embeddingService.generateEmbedding("q2")).thenReturn(queryVector);
        when(vectorStoreService.search(any(), any(), anyInt())).thenReturn(List.of(qdrantResult));

        List<SearchResult> results = knowledgeSearchService.search("q2", 1);
        assertNotNull(results);
        assertEquals(0, results.get(0).chunkIndex());
    }

    @Test
    void search_shouldHandleNumberChunkIndexAndIntOrNullPaths() {
        QdrantVectorStoreService.SearchResult qdrantResult = new QdrantVectorStoreService.SearchResult(
                chunkId, 0.7f, Map.of(
                "chunkId", chunkId.toString(),
                "lectureKnowledgeId", lectureId.toString(),
                "content", "Content",
                "chunkIndex", 3,
                "startTimeSeconds", 120,
                "endTimeSeconds", "180",
                "pageNumber", "not_a_number"));

        when(embeddingService.generateEmbedding("q3")).thenReturn(queryVector);
        when(vectorStoreService.search(any(), any(), anyInt())).thenReturn(List.of(qdrantResult));

        List<SearchResult> results = knowledgeSearchService.search("q3", 1);
        assertNotNull(results);
        assertEquals(3, results.get(0).chunkIndex());
        assertEquals(120, results.get(0).startTimeSeconds());
        assertEquals(180, results.get(0).endTimeSeconds());
        assertNull(results.get(0).pageNumber());
    }

    @Test
    void search_shouldHandleInvalidUuidAndNullContent() {
        QdrantVectorStoreService.SearchResult qdrantResult = new QdrantVectorStoreService.SearchResult(
                chunkId, 0.4f, Map.of(
                "chunkId", "not-a-valid-uuid",
                "lectureKnowledgeId", "also-invalid"));

        when(embeddingService.generateEmbedding("q4")).thenReturn(queryVector);
        when(vectorStoreService.search(any(), any(), anyInt())).thenReturn(List.of(qdrantResult));

        List<SearchResult> results = knowledgeSearchService.search("q4", 1);
        assertNotNull(results);
        assertNull(results.get(0).chunkId());
        assertNull(results.get(0).lectureKnowledgeId());
        assertNull(results.get(0).content());
        assertEquals(0, results.get(0).chunkIndex());
    }
}
