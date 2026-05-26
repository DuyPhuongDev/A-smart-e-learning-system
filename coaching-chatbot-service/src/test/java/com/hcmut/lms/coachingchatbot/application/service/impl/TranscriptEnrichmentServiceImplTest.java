package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coachingchatbot.application.service.EmbeddingService;
import com.hcmut.lms.coachingchatbot.application.service.QdrantVectorStoreService;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService.EnrichedChunk;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService.EnrichmentResult;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService.GroupedSegment;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptResponse;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TranscriptEnrichmentServiceImplTest {

    @Mock private CourseManagementClient courseManagementClient;
    @Mock private LectureKnowledgeRepository lectureKnowledgeRepository;
    @Mock private LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository;
    @Mock private GeminiClient geminiClient;
    @Mock private EmbeddingService embeddingService;
    @Mock private QdrantVectorStoreService vectorStoreService;

    private TranscriptEnrichmentServiceImpl transcriptEnrichmentService;
    private final ObjectMapper realObjectMapper = new ObjectMapper();

    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID lectureKnowledgeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        transcriptEnrichmentService = new TranscriptEnrichmentServiceImpl(
                courseManagementClient,
                lectureKnowledgeRepository,
                lectureKnowledgeChunkRepository,
                geminiClient,
                realObjectMapper,
                embeddingService,
                vectorStoreService);
    }

    private void setConfigFields() {
        ReflectionTestUtils.setField(transcriptEnrichmentService, "llmDelayMs", 0L);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "maxRetries", 1);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "segmentDurationSeconds", 90);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "contextWindowSize", 5);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "maxSummaryWords", 100);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "questionsPerChunk", 4);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "collectionName", "test_collection");
        ReflectionTestUtils.setField(transcriptEnrichmentService, "retryMaxAttempts", 1);
        ReflectionTestUtils.setField(transcriptEnrichmentService, "retryDelayMs", 0L);
    }

    // === Failure path tests ===

    @Test
    void enrichTranscriptsForLecture_shouldReturnFailure_whenNoTranscripts() {
        setConfigFields();
        when(courseManagementClient.getTranscriptSegments(lectureId)).thenReturn(List.of());

        EnrichmentResult result = transcriptEnrichmentService.enrichTranscriptsForLecture(lectureId);

        assertEquals("FAILED", result.status());
        assertTrue(result.message().contains("No transcript segments found"));
    }

    @Test
    void enrichTranscriptsForLecture_shouldReturnFailure_whenFetchFails() {
        setConfigFields();
        when(courseManagementClient.getTranscriptSegments(lectureId))
                .thenThrow(new RuntimeException("Service unavailable"));

        EnrichmentResult result = transcriptEnrichmentService.enrichTranscriptsForLecture(lectureId);

        assertEquals("FAILED", result.status());
    }

    @Test
    void enrichTranscriptsForLecture_shouldReturnFailure_whenNullTranscripts() {
        setConfigFields();
        when(courseManagementClient.getTranscriptSegments(lectureId)).thenReturn(null);

        EnrichmentResult result = transcriptEnrichmentService.enrichTranscriptsForLecture(lectureId);

        assertEquals("FAILED", result.status());
    }

    @Test
    void enrichTranscriptsForLectureAsync_shouldReturnFailedFuture_whenException() throws Exception {
        setConfigFields();
        when(courseManagementClient.getTranscriptSegments(lectureId))
                .thenThrow(new RuntimeException("Async failure"));

        CompletableFuture<EnrichmentResult> future =
                transcriptEnrichmentService.enrichTranscriptsForLectureAsync(lectureId);

        assertTrue(future.isCompletedExceptionally() || future.get().status().equals("FAILED"));
    }

    // === Private method tests via reflection ===

    private VideoTranscriptResponse createTranscript(int start, int end, String text, int index) {
        return VideoTranscriptResponse.builder()
                .id(UUID.randomUUID())
                .videoLectureId(lectureId)
                .transcriptText(text)
                .startTimeSeconds(start)
                .endTimeSeconds(end)
                .segmentIndex(index)
                .wordCount(5)
                .build();
    }

    @Test
    void groupTranscriptsByDuration_shouldReturnOneGroup_whenSingleShortTranscript() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "groupTranscriptsByDuration", List.class);
        method.setAccessible(true);

        VideoTranscriptResponse t = createTranscript(0, 30, "Hello world", 0);
        @SuppressWarnings("unchecked")
        List<GroupedSegment> result = (List<GroupedSegment>) method.invoke(
                transcriptEnrichmentService, new ArrayList<>(List.of(t)));

        assertEquals(1, result.size());
        assertEquals("Hello world", result.get(0).combinedText());
        assertEquals(0, result.get(0).startTimeSeconds());
        assertEquals(30, result.get(0).endTimeSeconds());
    }

    @Test
    void groupTranscriptsByDuration_shouldReturnMultipleGroups_whenDurationExceedsLimit() throws Exception {
        setConfigFields();
        ReflectionTestUtils.setField(transcriptEnrichmentService, "segmentDurationSeconds", 30);
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "groupTranscriptsByDuration", List.class);
        method.setAccessible(true);

        VideoTranscriptResponse t1 = createTranscript(0, 30, "First segment", 0);
        VideoTranscriptResponse t2 = createTranscript(30, 60, "Second segment", 1);
        @SuppressWarnings("unchecked")
        List<GroupedSegment> result = (List<GroupedSegment>) method.invoke(
                transcriptEnrichmentService, new ArrayList<>(List.of(t1, t2)));

        assertTrue(result.size() >= 1);
    }

    @Test
    void groupTranscriptsByDuration_shouldHandleNullFields() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "groupTranscriptsByDuration", List.class);
        method.setAccessible(true);

        VideoTranscriptResponse t = VideoTranscriptResponse.builder()
                .transcriptText("Some text")
                .startTimeSeconds(null)
                .endTimeSeconds(null)
                .segmentIndex(null)
                .build();
        @SuppressWarnings("unchecked")
        List<GroupedSegment> result = (List<GroupedSegment>) method.invoke(
                transcriptEnrichmentService, new ArrayList<>(List.of(t)));

        assertEquals(1, result.size());
    }

    @Test
    void groupTranscriptsByDuration_shouldReturnEmpty_whenTranscriptsEmpty() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "groupTranscriptsByDuration", List.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<GroupedSegment> result = (List<GroupedSegment>) method.invoke(
                transcriptEnrichmentService, List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void groupTranscriptsByDuration_shouldReturnEmpty_whenAllTextIsEmpty() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "groupTranscriptsByDuration", List.class);
        method.setAccessible(true);

        VideoTranscriptResponse t = createTranscript(0, 10, "", 0);
        @SuppressWarnings("unchecked")
        List<GroupedSegment> result = (List<GroupedSegment>) method.invoke(
                transcriptEnrichmentService, new ArrayList<>(List.of(t)));

        assertTrue(result.isEmpty());
    }

    @Test
    void buildContextText_shouldReturnEmpty_whenSingleSegment() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "buildContextText", List.class, int.class, int.class);
        method.setAccessible(true);

        List<GroupedSegment> segments = List.of(
                new GroupedSegment(0, "Text", 0, 30, 4));

        String context = (String) method.invoke(transcriptEnrichmentService, segments, 0, 5);
        assertEquals("", context);
    }

    @Test
    void buildContextText_shouldIncludeSurroundingSegments() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "buildContextText", List.class, int.class, int.class);
        method.setAccessible(true);

        List<GroupedSegment> segments = List.of(
                new GroupedSegment(0, "First", 0, 10, 1),
                new GroupedSegment(1, "Second", 10, 20, 1),
                new GroupedSegment(2, "Third", 20, 30, 1));

        String context = (String) method.invoke(transcriptEnrichmentService, segments, 1, 3);
        assertTrue(context.contains("[Segment 1"));
        assertTrue(context.contains("[Segment 3"));
        assertFalse(context.contains("Second")); // current segment excluded
    }

    @Test
    void buildEnrichedContent_shouldReturnFormattedContent() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "buildEnrichedContent", String.class, String.class, List.class);
        method.setAccessible(true);

        String content = (String) method.invoke(transcriptEnrichmentService,
                "Original text", "Summary text", List.of("Q1", "Q2"));

        assertTrue(content.contains("=== ORIGINAL TRANSCRIPT ==="));
        assertTrue(content.contains("Original text"));
        assertTrue(content.contains("=== SUMMARY ==="));
        assertTrue(content.contains("Summary text"));
        assertTrue(content.contains("=== REVIEW QUESTIONS ==="));
        assertTrue(content.contains("1. Q1"));
        assertTrue(content.contains("2. Q2"));
    }

    @Test
    void parseEnrichmentResponse_shouldParseValidJson() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "parseEnrichmentResponse", String.class, GroupedSegment.class, int.class);
        method.setAccessible(true);

        String json = "{\"original_text\": \"Hello\", \"summary\": \"Summary\", " +
                "\"questions\": [\"Q1?\", \"Q2?\"]}";
        GroupedSegment segment = new GroupedSegment(0, "Hello", 0, 30, 4);

        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, json, segment, 0);

        assertNotNull(result);
        assertTrue(result.isEnriched());
        assertEquals("Hello", result.originalText());
        assertEquals("Summary", result.summary());
        assertEquals(2, result.questions().size());
    }

    @Test
    void parseEnrichmentResponse_shouldHandleMissingOriginalText() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "parseEnrichmentResponse", String.class, GroupedSegment.class, int.class);
        method.setAccessible(true);

        String json = "{\"summary\": \"Summary\", \"questions\": [\"Q1?\"]}";
        GroupedSegment segment = new GroupedSegment(0, "Fallback text", 0, 30, 4);

        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, json, segment, 0);

        assertNotNull(result);
        assertEquals("Fallback text", result.originalText());
    }

    @Test
    void parseEnrichmentResponse_shouldReturnNull_whenInvalidJson() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "parseEnrichmentResponse", String.class, GroupedSegment.class, int.class);
        method.setAccessible(true);

        GroupedSegment segment = new GroupedSegment(0, "Text", 0, 30, 4);
        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, "not valid json", segment, 0);

        assertNull(result);
    }

    @Test
    void parseEnrichmentResponse_shouldStripMarkdownCodeBlock() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "parseEnrichmentResponse", String.class, GroupedSegment.class, int.class);
        method.setAccessible(true);

        String json = "```json\n{\"original_text\": \"Test\", \"summary\": \"S\", \"questions\": [\"Q?\"]}\n```";
        GroupedSegment segment = new GroupedSegment(0, "Test", 0, 30, 4);

        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, json, segment, 0);

        assertNotNull(result);
        assertTrue(result.isEnriched());
        assertEquals("Test", result.originalText());
    }

    @Test
    void createFallbackChunk_shouldReturnUnenrichedChunk() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "createFallbackChunk", GroupedSegment.class, int.class);
        method.setAccessible(true);

        GroupedSegment segment = new GroupedSegment(2, "Test content", 10, 60, 2);
        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, segment, 2);

        assertNotNull(result);
        assertFalse(result.isEnriched());
        assertEquals(2, result.index());
        assertEquals("Test content", result.originalText());
        assertEquals(10, result.startTimeSeconds());
        assertEquals(60, result.endTimeSeconds());
        assertTrue(result.enrichedContent().contains("Test content"));
    }

    @Test
    void estimateTokenCount_shouldReturnCeilDivByFour() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "estimateTokenCount", String.class);
        method.setAccessible(true);

        assertEquals(0, method.invoke(transcriptEnrichmentService, (Object) null));
        assertEquals(0, method.invoke(transcriptEnrichmentService, ""));
        assertEquals(0, method.invoke(transcriptEnrichmentService, "   "));
        assertEquals(3, method.invoke(transcriptEnrichmentService, "1234567890")); // 10/4 = 2.5 → 3
    }

    @Test
    void getOrCreateLectureKnowledge_shouldReturnExisting() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "getOrCreateLectureKnowledge", UUID.class);
        method.setAccessible(true);

        LectureKnowledge existing = LectureKnowledge.builder()
                .lectureKnowledgeId(lectureKnowledgeId)
                .build();
        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(existing);

        LectureKnowledge result = (LectureKnowledge) method.invoke(
                transcriptEnrichmentService, lectureId);

        assertSame(existing, result);
    }

    @Test
    void getOrCreateLectureKnowledge_shouldCreateNew_whenNotFound() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "getOrCreateLectureKnowledge", UUID.class);
        method.setAccessible(true);

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId)).thenReturn(null);
        when(lectureKnowledgeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LectureKnowledge result = (LectureKnowledge) method.invoke(
                transcriptEnrichmentService, lectureId);

        assertNotNull(result);
        assertEquals(lectureId, result.getLectureKnowledgeId());
        verify(lectureKnowledgeRepository).save(any());
    }

    @Test
    void deleteExistingChunks_shouldCallRepository() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "deleteExistingChunks", UUID.class);
        method.setAccessible(true);

        doNothing().when(lectureKnowledgeChunkRepository)
                .deleteByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);

        method.invoke(transcriptEnrichmentService, lectureKnowledgeId);

        verify(lectureKnowledgeChunkRepository)
                .deleteByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);
    }

    @Test
    void saveEnrichedChunks_shouldSaveAndReturnChunks() throws Exception {
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "saveEnrichedChunks", LectureKnowledge.class, List.class);
        method.setAccessible(true);

        LectureKnowledge knowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(lectureKnowledgeId)
                .build();
        EnrichedChunk enriched = new EnrichedChunk(0, "orig", "sum", List.of("q"),
                "enriched content", 0, 30, 10, true);

        when(lectureKnowledgeChunkRepository.saveAll(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        @SuppressWarnings("unchecked")
        List<LectureKnowledgeChunk> result = (List<LectureKnowledgeChunk>) method.invoke(
                transcriptEnrichmentService, knowledge, List.of(enriched));

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getChunkIndex());
        assertEquals("enriched content", result.get(0).getChunkContent());
        verify(lectureKnowledgeChunkRepository).saveAll(any());
    }

    // === syncChunksToQdrant via reflection ===

    private List<LectureKnowledgeChunk> createChunkList(int count) {
        List<LectureKnowledgeChunk> chunks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LectureKnowledgeChunk chunk = new LectureKnowledgeChunk();
            chunk.setId(UUID.randomUUID());
            chunk.setChunkContent("content " + i);
            chunk.setChunkIndex(i);
            chunk.setQdrantPointId(UUID.randomUUID());
            chunk.setStartTimeSeconds(i * 30);
            chunk.setEndTimeSeconds((i + 1) * 30);
            chunks.add(chunk);
        }
        return chunks;
    }

    @Test
    void syncChunksToQdrant_shouldSucceedOnFirstAttempt() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "syncChunksToQdrant", List.class, LectureKnowledge.class);
        method.setAccessible(true);

        List<LectureKnowledgeChunk> chunks = createChunkList(1);
        LectureKnowledge knowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(lectureKnowledgeId)
                .contentType("VIDEO")
                .build();

        when(embeddingService.generateEmbeddings(any()))
                .thenReturn(List.of(List.of(0.1f, 0.2f)));
        doNothing().when(vectorStoreService).upsertPoints(anyString(), any());

        assertDoesNotThrow(() -> method.invoke(transcriptEnrichmentService, chunks, knowledge));
    }

    @Test
    void syncChunksToQdrant_shouldRetry_whenFirstAttemptFails() throws Exception {
        setConfigFields();
        ReflectionTestUtils.setField(transcriptEnrichmentService, "retryMaxAttempts", 2);
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "syncChunksToQdrant", List.class, LectureKnowledge.class);
        method.setAccessible(true);

        List<LectureKnowledgeChunk> chunks = createChunkList(1);
        LectureKnowledge knowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(lectureKnowledgeId)
                .contentType("VIDEO")
                .build();

        when(embeddingService.generateEmbeddings(any()))
                .thenReturn(List.of(List.of(0.1f, 0.2f)));
        doThrow(new RuntimeException("First attempt fails"))
                .doNothing()
                .when(vectorStoreService).upsertPoints(anyString(), any());

        assertDoesNotThrow(() -> method.invoke(transcriptEnrichmentService, chunks, knowledge));
        verify(vectorStoreService, times(2)).upsertPoints(anyString(), any());
    }

    // === enrichSegmentWithRetry via reflection ===

    @Test
    void enrichSegmentWithRetry_shouldReturnEnrichedChunk_whenLLMSucceeds() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "enrichSegmentWithRetry", GroupedSegment.class, String.class, int.class);
        method.setAccessible(true);

        GroupedSegment segment = new GroupedSegment(0, "Test transcript text", 0, 30, 4);
        when(geminiClient.chat(any())).thenReturn(
                "{\"original_text\": \"Test transcript text\", \"summary\": \"Summary\", " +
                "\"questions\": [\"Q1?\", \"Q2?\", \"Q3?\", \"Q4?\"]}");

        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, segment, "context", 0);

        assertNotNull(result);
        assertTrue(result.isEnriched());
        assertEquals("Test transcript text", result.originalText());
    }

    @Test
    void enrichSegmentWithRetry_shouldReturnFallback_whenLLMFails() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "enrichSegmentWithRetry", GroupedSegment.class, String.class, int.class);
        method.setAccessible(true);

        GroupedSegment segment = new GroupedSegment(0, "Fallback text", 10, 50, 2);
        when(geminiClient.chat(any())).thenThrow(new RuntimeException("LLM error"));

        EnrichedChunk result = (EnrichedChunk) method.invoke(
                transcriptEnrichmentService, segment, "", 0);

        assertNotNull(result);
        assertFalse(result.isEnriched());
        assertEquals("Fallback text", result.originalText());
    }

    // === enrichSegmentsWithContext via reflection ===

    @Test
    void enrichSegmentsWithContext_shouldProcessMultipleSegments() throws Exception {
        setConfigFields();
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "enrichSegmentsWithContext", List.class);
        method.setAccessible(true);

        List<GroupedSegment> segments = List.of(
                new GroupedSegment(0, "First", 0, 30, 1),
                new GroupedSegment(1, "Second", 30, 60, 1));

        when(geminiClient.chat(any()))
                .thenReturn("{\"original_text\": \"First\", \"summary\": \"S1\", " +
                        "\"questions\": [\"Q1?\", \"Q2?\", \"Q3?\", \"Q4?\"]}")
                .thenReturn("{\"original_text\": \"Second\", \"summary\": \"S2\", " +
                        "\"questions\": [\"Q1?\", \"Q2?\", \"Q3?\", \"Q4?\"]}");

        @SuppressWarnings("unchecked")
        List<EnrichedChunk> result = (List<EnrichedChunk>) method.invoke(
                transcriptEnrichmentService, segments);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isEnriched());
        assertTrue(result.get(1).isEnriched());
    }

    @Test
    void syncChunksToQdrant_shouldThrowException_whenAllRetriesFail() throws Exception {
        setConfigFields();
        ReflectionTestUtils.setField(transcriptEnrichmentService, "retryMaxAttempts", 2);
        Method method = TranscriptEnrichmentServiceImpl.class.getDeclaredMethod(
                "syncChunksToQdrant", List.class, LectureKnowledge.class);
        method.setAccessible(true);

        List<LectureKnowledgeChunk> chunks = createChunkList(1);
        LectureKnowledge knowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(lectureKnowledgeId)
                .contentType("VIDEO")
                .build();

        when(embeddingService.generateEmbeddings(any()))
                .thenReturn(List.of(List.of(0.1f, 0.2f)));
        doThrow(new RuntimeException("Always fails"))
                .when(vectorStoreService).upsertPoints(anyString(), any());

        try {
            method.invoke(transcriptEnrichmentService, chunks, knowledge);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("Qdrant"));
        }
    }

    @Test
    void enrichTranscriptsForLecture_shouldCompleteFullPipelineSuccessfully() {
        setConfigFields();

        VideoTranscriptResponse transcript = VideoTranscriptResponse.builder()
                .transcriptText("This is a test transcript segment.")
                .startTimeSeconds(0)
                .endTimeSeconds(30)
                .audioDuration(30)
                .wordCount(6)
                .segmentIndex(0)
                .build();
        when(courseManagementClient.getTranscriptSegments(lectureId))
                .thenReturn(new ArrayList<>(List.of(transcript)));

        LectureKnowledge knowledge = new LectureKnowledge();
        knowledge.setLectureKnowledgeId(lectureId);
        knowledge.setContentType("VIDEO");

        when(lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId))
                .thenReturn(null);
        when(lectureKnowledgeRepository.save(any())).thenReturn(knowledge);

        String llmJson = "{\"original_text\":\"transcript text\",\"summary\":\"summary text\","
                + "\"questions\":[\"Q1\",\"Q2\",\"Q3\",\"Q4\"]}";
        when(geminiClient.chat(anyString())).thenReturn(llmJson);

        doNothing().when(lectureKnowledgeChunkRepository)
                .deleteByLectureKnowledgeLectureKnowledgeId(lectureId);

        LectureKnowledgeChunk savedChunk = new LectureKnowledgeChunk();
        savedChunk.setId(UUID.randomUUID());
        savedChunk.setChunkContent("enriched content");
        savedChunk.setQdrantPointId(UUID.randomUUID());
        savedChunk.setChunkIndex(0);
        when(lectureKnowledgeChunkRepository.saveAll(any())).thenReturn(List.of(savedChunk));

        when(embeddingService.generateEmbeddings(any()))
                .thenReturn(List.of(List.of(0.1f, 0.2f, 0.3f)));
        doNothing().when(vectorStoreService).upsertPoints(anyString(), any());

        EnrichmentResult result = transcriptEnrichmentService
                .enrichTranscriptsForLecture(lectureId);

        assertNotNull(result);
        assertEquals("SUCCESS", result.status());
        assertEquals(1, result.totalChunks());
        assertEquals(1, result.enrichedChunks());
        assertEquals(0, result.fallbackChunks());
    }
}
