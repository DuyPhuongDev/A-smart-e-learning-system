package com.hcmut.lms.coachingchatbot.application.service.impl;

import static io.qdrant.client.ValueFactory.nullValue;
import static io.qdrant.client.ValueFactory.value;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.service.QdrantVectorStoreService;
import com.google.common.util.concurrent.ListenableFuture;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Common;
import io.qdrant.client.grpc.Points;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class QdrantVectorStoreServiceImplTest {

    @Mock private QdrantClient qdrantClient;

    @InjectMocks
    private QdrantVectorStoreServiceImpl qdrantVectorStoreService;

    private static final UUID pointId = UUID.randomUUID();
    private static final List<Float> vector = List.of(0.1f, 0.2f, 0.3f);

    // === Null/Empty guards ===

    @Test
    void upsertPoints_shouldDoNothing_whenListIsNull() {
        assertDoesNotThrow(() -> qdrantVectorStoreService.upsertPoints("col", null));
        verifyNoInteractions(qdrantClient);
    }

    @Test
    void upsertPoints_shouldDoNothing_whenListIsEmpty() {
        assertDoesNotThrow(() -> qdrantVectorStoreService.upsertPoints("col", List.of()));
        verifyNoInteractions(qdrantClient);
    }

    @Test
    void deletePoints_shouldDoNothing_whenListIsNull() {
        assertDoesNotThrow(() -> qdrantVectorStoreService.deletePoints("col", null));
        verifyNoInteractions(qdrantClient);
    }

    @Test
    void deletePoints_shouldDoNothing_whenListIsEmpty() {
        assertDoesNotThrow(() -> qdrantVectorStoreService.deletePoints("col", List.of()));
        verifyNoInteractions(qdrantClient);
    }

    @Test
    void deleteByFilter_shouldDoNothing_whenFilterIsNull() {
        assertDoesNotThrow(() -> qdrantVectorStoreService.deleteByFilter("col", null));
        verifyNoInteractions(qdrantClient);
    }

    @Test
    void deleteByFilter_shouldDoNothing_whenFilterIsEmpty() {
        assertDoesNotThrow(() -> qdrantVectorStoreService.deleteByFilter("col", Map.of()));
        verifyNoInteractions(qdrantClient);
    }

    // === collectionExists ===

    @Test
    void collectionExists_shouldReturnTrue_whenCollectionExists() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(true);
        when(qdrantClient.collectionExistsAsync("existing_col")).thenReturn(future);

        assertTrue(qdrantVectorStoreService.collectionExists("existing_col"));
    }

    @Test
    void collectionExists_shouldReturnFalse_whenCollectionDoesNotExist() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(false);
        when(qdrantClient.collectionExistsAsync("missing_col")).thenReturn(future);

        assertFalse(qdrantVectorStoreService.collectionExists("missing_col"));
    }

    @Test
    void collectionExists_shouldReturnFalse_whenInterrupted() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> future = mock(ListenableFuture.class);
        when(future.get()).thenThrow(new InterruptedException("interrupted"));
        when(qdrantClient.collectionExistsAsync(anyString())).thenReturn(future);

        assertFalse(qdrantVectorStoreService.collectionExists("any_col"));
    }

    @Test
    void collectionExists_shouldReturnFalse_whenExecutionException() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> future = mock(ListenableFuture.class);
        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("fail")));
        when(qdrantClient.collectionExistsAsync(anyString())).thenReturn(future);

        assertFalse(qdrantVectorStoreService.collectionExists("any_col"));
    }

    // === upsertPoint ===

    @Test
    void upsertPoint_shouldThrowException_whenClientFails() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("qdrant down")));
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        assertThrows(RuntimeException.class, () ->
                qdrantVectorStoreService.upsertPoint("col", pointId, vector, Map.of("key", "value")));
    }

    @Test
    void upsertPoint_shouldSucceed_whenValidInput() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(null); // upsertAsync returns void
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        assertDoesNotThrow(() ->
                qdrantVectorStoreService.upsertPoint("col", pointId, vector,
                        Map.of("key", "value", "intKey", 42, "boolKey", true)));
    }

    @Test
    void upsertPoint_shouldWorkWithEmptyPayload() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(null);
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        assertDoesNotThrow(() ->
                qdrantVectorStoreService.upsertPoint("col", pointId, vector, Map.of()));
    }

    // === upsertPoints batch ===

    @Test
    void upsertPoints_shouldSucceedWithSinglePoint() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(null);
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        QdrantVectorStoreService.VectorPoint vp = new QdrantVectorStoreService.VectorPoint(
                pointId, vector, Map.of("key", "val"));

        assertDoesNotThrow(() -> qdrantVectorStoreService.upsertPoints("col", List.of(vp)));
    }

    @Test
    void upsertPoints_shouldSucceedWithMultiplePoints() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(null);
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        List<QdrantVectorStoreService.VectorPoint> points = List.of(
                new QdrantVectorStoreService.VectorPoint(UUID.randomUUID(), vector, Map.of("idx", "0")),
                new QdrantVectorStoreService.VectorPoint(UUID.randomUUID(), vector, Map.of("idx", "1")),
                new QdrantVectorStoreService.VectorPoint(UUID.randomUUID(), vector, Map.of("idx", "2"))
        );

        assertDoesNotThrow(() -> qdrantVectorStoreService.upsertPoints("col", points));
        verify(qdrantClient, times(1)).upsertAsync(anyString(), any());
    }

    @Test
    void upsertPoints_shouldThrowException_whenClientFails() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("batch fail")));
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        QdrantVectorStoreService.VectorPoint vp = new QdrantVectorStoreService.VectorPoint(
                pointId, vector, Map.of());

        assertThrows(RuntimeException.class, () ->
                qdrantVectorStoreService.upsertPoints("col", List.of(vp)));
    }

    // === search ===

    @Test
    void search_shouldThrowException_whenClientFails() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("search error")));
        when(qdrantClient.searchAsync(any())).thenReturn(future);

        assertThrows(RuntimeException.class, () ->
                qdrantVectorStoreService.search("col", vector, 5));
    }

    @Test
    void search_shouldReturnResults_whenMatchesFound() throws Exception {
        Points.ScoredPoint scoredPoint = Points.ScoredPoint.newBuilder()
                .setId(Common.PointId.newBuilder().setUuid(pointId.toString()).build())
                .setScore(0.95f)
                .putPayload("chunkId", value(pointId.toString()))
                .putPayload("content", value("test content"))
                .putPayload("chunkIndex", value(0))
                .build();

        @SuppressWarnings("unchecked")
        ListenableFuture<List<Points.ScoredPoint>> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(List.of(scoredPoint));
        when(qdrantClient.searchAsync(any())).thenReturn(future);

        List<QdrantVectorStoreService.SearchResult> results =
                qdrantVectorStoreService.search("col", vector, 5);

        assertEquals(1, results.size());
        assertEquals(pointId, results.get(0).id());
        assertEquals(0.95f, results.get(0).score(), 0.001f);
    }

    @Test
    void searchWithFilter_shouldAddFilterCondition() throws Exception {
        Points.ScoredPoint scoredPoint = Points.ScoredPoint.newBuilder()
                .setId(Common.PointId.newBuilder().setUuid(pointId.toString()).build())
                .setScore(0.88f)
                .putPayload("lectureKnowledgeId", value(pointId.toString()))
                .putPayload("content", value("filtered content"))
                .build();

        @SuppressWarnings("unchecked")
        ListenableFuture<List<Points.ScoredPoint>> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(List.of(scoredPoint));
        when(qdrantClient.searchAsync(any())).thenReturn(future);

        List<QdrantVectorStoreService.SearchResult> results =
                qdrantVectorStoreService.searchWithFilter("col", vector,
                        Map.of("lectureKnowledgeId", "some-id"), 5);

        assertEquals(1, results.size());
        assertEquals(0.88f, results.get(0).score(), 0.001f);
    }

    @Test
    void search_shouldReturnEmptyList_whenNoMatches() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<List<Points.ScoredPoint>> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(List.of());
        when(qdrantClient.searchAsync(any())).thenReturn(future);

        List<QdrantVectorStoreService.SearchResult> results =
                qdrantVectorStoreService.search("col", vector, 5);

        assertTrue(results.isEmpty());
    }

    // === deletePoints ===

    @Test
    void deletePoints_shouldCompleteSuccessfully_whenValidIds() {
        assertDoesNotThrow(() ->
                qdrantVectorStoreService.deletePoints("col", List.of(pointId)));
    }

    // === deleteByFilter ===

    @Test
    void deleteByFilter_shouldCompleteSuccessfully_whenValidFilter() {
        assertDoesNotThrow(() ->
                qdrantVectorStoreService.deleteByFilter("col", Map.of("lectureKnowledgeId", "some-id")));
    }

    // === initializeCollection ===

    @Test
    void initializeCollection_shouldSkip_whenCollectionExists() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> existsFuture = mock(ListenableFuture.class);
        when(existsFuture.get()).thenReturn(true);
        when(qdrantClient.collectionExistsAsync("existing")).thenReturn(existsFuture);

        assertDoesNotThrow(() -> qdrantVectorStoreService.initializeCollection("existing", 768));
        verify(qdrantClient, never()).createCollectionAsync(anyString(), any(Collections.VectorParams.class));
    }

    @Test
    void initializeCollection_shouldCreate_whenCollectionDoesNotExist() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> existsFuture = mock(ListenableFuture.class);
        when(existsFuture.get()).thenReturn(false);
        when(qdrantClient.collectionExistsAsync("new_col")).thenReturn(existsFuture);

        @SuppressWarnings("unchecked")
        ListenableFuture createFuture = mock(ListenableFuture.class);
        when(createFuture.get()).thenReturn(null);
        when(qdrantClient.createCollectionAsync(eq("new_col"), any(Collections.VectorParams.class)))
                .thenReturn(createFuture);

        @SuppressWarnings("unchecked")
        ListenableFuture indexFuture = mock(ListenableFuture.class);
        when(indexFuture.get()).thenReturn(null);
        when(qdrantClient.createPayloadIndexAsync(eq("new_col"), anyString(), any(), any(), any(), any(), any()))
                .thenReturn(indexFuture);

        assertDoesNotThrow(() -> qdrantVectorStoreService.initializeCollection("new_col", 768));
    }

    // === toValue / toPayloadMap coverage via upsertPoint ===

    @Test
    void upsertPoint_shouldHandleAllValueTypes() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(null);
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        Map<String, Object> payload = new HashMap<>();
        payload.put("longVal", 42L);
        payload.put("doubleVal", 3.14d);
        payload.put("floatVal", 1.5f);
        payload.put("boolVal", true);
        payload.put("nullVal", null);

        assertDoesNotThrow(() ->
                qdrantVectorStoreService.upsertPoint("col", pointId, vector, payload));
    }

    @Test
    void upsertPoint_shouldHandleNullPayload() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(null);
        when(qdrantClient.upsertAsync(anyString(), any())).thenReturn(future);

        assertDoesNotThrow(() ->
                qdrantVectorStoreService.upsertPoint("col", pointId, vector, null));
    }

    // === fromValue coverage via search ===

    @Test
    void search_shouldHandleDoubleBoolNullValues() throws Exception {
        Points.ScoredPoint scoredPoint = Points.ScoredPoint.newBuilder()
                .setId(Common.PointId.newBuilder().setUuid(pointId.toString()).build())
                .setScore(0.8f)
                .putPayload("doubleVal", value(3.14d))
                .putPayload("boolVal", value(true))
                .putPayload("nullVal", nullValue())
                .build();

        @SuppressWarnings("unchecked")
        ListenableFuture<List<Points.ScoredPoint>> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(List.of(scoredPoint));
        when(qdrantClient.searchAsync(any())).thenReturn(future);

        List<QdrantVectorStoreService.SearchResult> results =
                qdrantVectorStoreService.search("col", vector, 5);

        assertEquals(1, results.size());
    }

    @Test
    void searchWithFilter_shouldSkipNonStringFilterValues() throws Exception {
        Points.ScoredPoint scoredPoint = Points.ScoredPoint.newBuilder()
                .setId(Common.PointId.newBuilder().setUuid(pointId.toString()).build())
                .setScore(0.7f)
                .putPayload("lectureKnowledgeId", value(pointId.toString()))
                .build();

        @SuppressWarnings("unchecked")
        ListenableFuture<List<Points.ScoredPoint>> future = mock(ListenableFuture.class);
        when(future.get()).thenReturn(List.of(scoredPoint));
        when(qdrantClient.searchAsync(any())).thenReturn(future);

        // Non-String filter values should be skipped (no Condition added)
        List<QdrantVectorStoreService.SearchResult> results =
                qdrantVectorStoreService.searchWithFilter("col", vector,
                        Map.of("lectureKnowledgeId", 42), 5);

        assertEquals(1, results.size());
    }

    @Test
    void deleteByFilter_shouldSkipNonStringFilterValues() {
        // Filter with Integer value (not String) — the loop skips it but doesn't fail
        assertDoesNotThrow(() ->
                qdrantVectorStoreService.deleteByFilter("col",
                        Map.of("lectureKnowledgeId", 42)));
    }

    @Test
    void initializeCollection_shouldThrowException_whenCreationFails() throws Exception {
        @SuppressWarnings("unchecked")
        ListenableFuture<Boolean> existsFuture = mock(ListenableFuture.class);
        when(existsFuture.get()).thenReturn(false);
        when(qdrantClient.collectionExistsAsync("fail_col")).thenReturn(existsFuture);

        @SuppressWarnings("unchecked")
        ListenableFuture createFuture = mock(ListenableFuture.class);
        when(createFuture.get()).thenThrow(new ExecutionException(new RuntimeException("creation failed")));
        when(qdrantClient.createCollectionAsync(eq("fail_col"), any(Collections.VectorParams.class)))
                .thenReturn(createFuture);

        assertThrows(RuntimeException.class, () ->
                qdrantVectorStoreService.initializeCollection("fail_col", 768));
    }
}
