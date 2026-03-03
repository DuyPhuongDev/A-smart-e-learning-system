package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.request.DocumentEnrichmentCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.DocumentEnrichmentCallbackResponse;
import com.hcmut.lms.coachingchatbot.application.mapper.DocumentEnrichmentCallbackMapper;
import com.hcmut.lms.coachingchatbot.application.service.DocumentEnrichmentCallbackService;
import com.hcmut.lms.coachingchatbot.application.service.EmbeddingService;
import com.hcmut.lms.coachingchatbot.application.service.QdrantVectorStoreService;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentEnrichmentCallbackService
 * Handles callbacks from AWS Fargate worker after document/text enrichment is complete.
 *
 * Flow:
 * 1. Receive enriched chunks from Fargate worker
 * 2. Create/update LectureKnowledge record with status COMPLETED
 * 3. Delete existing chunks if any
 * 4. Save new LectureKnowledgeChunk records
 */
@Service
@Slf4j
public class DocumentEnrichmentCallbackServiceImpl implements DocumentEnrichmentCallbackService {

    private final LectureKnowledgeRepository lectureKnowledgeRepository;
    private final LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository;
    private final DocumentEnrichmentCallbackMapper documentEnrichmentCallbackMapper;
    private final EmbeddingService embeddingService;
    private final QdrantVectorStoreService vectorStoreService;

    @Value("${qdrant.collection-name:lecture_knowledge}")
    private String collectionName;

    @Value("${chatbot.retry-max-attempts:3}")
    private int retryMaxAttempts;

    @Value("${chatbot.retry-delay-ms:2000}")
    private long retryDelayMs;

    private static final String EMBEDDING_MODEL = "gemini-text-embedding-004";

    public DocumentEnrichmentCallbackServiceImpl(
            LectureKnowledgeRepository lectureKnowledgeRepository,
            LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository,
            DocumentEnrichmentCallbackMapper documentEnrichmentCallbackMapper,
            EmbeddingService embeddingService,
            QdrantVectorStoreService vectorStoreService) {
        this.lectureKnowledgeRepository = lectureKnowledgeRepository;
        this.lectureKnowledgeChunkRepository = lectureKnowledgeChunkRepository;
        this.documentEnrichmentCallbackMapper = documentEnrichmentCallbackMapper;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<DocumentEnrichmentCallbackResponse> processEnrichmentCallback(DocumentEnrichmentCallbackRequest request) {
        UUID lectureId = request.getLectureId();
        String contentType = request.getContentType();
        int chunkCount = request.getChunks() != null ? request.getChunks().size() : 0;

        log.info("Processing document enrichment callback for lecture: {}, contentType: {}, chunks: {}",
                lectureId, contentType, chunkCount);

        try {
            // Step 1: Create or get LectureKnowledge
            LectureKnowledge lectureKnowledge = getOrCreateLectureKnowledge(lectureId, contentType);

            // Step 2: Update status to PROCESSING
            lectureKnowledge.setSyncStatus(SyncStatus.PROCESSING);
            lectureKnowledgeRepository.save(lectureKnowledge);

            // Step 3: Delete existing chunks if any
            deleteExistingChunks(lectureId);

            // Step 4: Save new enriched chunks
            List<LectureKnowledgeChunk> savedChunks = saveEnrichedChunks(lectureKnowledge, request.getChunks());

            // Step 5: Sync chunks to Qdrant (synchronous with retry)
            syncChunksToQdrant(savedChunks, lectureKnowledge);

            // Step 6: Update LectureKnowledge status to COMPLETED
            lectureKnowledge.setSyncStatus(SyncStatus.COMPLETED);
            lectureKnowledge.setTotalChunks(savedChunks.size());
            lectureKnowledge.setLastSyncedAt(Instant.now());
            lectureKnowledge.setEmbeddingModel(EMBEDDING_MODEL);
            lectureKnowledge.setContentType(contentType);
            lectureKnowledgeRepository.save(lectureKnowledge);

            log.info("Successfully processed {} chunks for lecture: {}", savedChunks.size(), lectureId);

            return CompletableFuture.completedFuture(
                    buildSuccessResponse(lectureId, contentType, savedChunks.size(),
                            lectureKnowledge.getLectureKnowledgeId())
            );

        } catch (Exception e) {
            log.error("Failed to process document enrichment callback for lecture {}: {}", lectureId, e.getMessage(), e);

            // Update status to FAILED if LectureKnowledge exists
            updateStatusToFailed(lectureId, e.getMessage());

            return CompletableFuture.completedFuture(
                    buildFailureResponse(lectureId, contentType, e.getMessage())
            );
        }
    }

    /**
     * Update LectureKnowledge status to FAILED
     */
    private void updateStatusToFailed(UUID lectureId, String errorMessage) {
        try {
            LectureKnowledge lectureKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
            if (lectureKnowledge != null) {
                lectureKnowledge.setSyncStatus(SyncStatus.FAILED);
                lectureKnowledge.setErrorMessage(errorMessage);
                lectureKnowledgeRepository.save(lectureKnowledge);
            }
        } catch (Exception ex) {
            log.error("Failed to update LectureKnowledge status: {}", ex.getMessage());
        }
    }

    /**
     * Build success response
     */
    private DocumentEnrichmentCallbackResponse buildSuccessResponse(UUID lectureId, String contentType,
                                                                    int chunksSaved, UUID lectureKnowledgeId) {
        return DocumentEnrichmentCallbackResponse.builder()
                .lectureId(lectureId)
                .status("SUCCESS")
                .message(String.format("Successfully saved %d enriched chunks", chunksSaved))
                .lectureKnowledgeId(lectureKnowledgeId)
                .chunksSaved(chunksSaved)
                .contentType(contentType)
                .build();
    }

    /**
     * Build failure response
     */
    private DocumentEnrichmentCallbackResponse buildFailureResponse(UUID lectureId, String contentType,
                                                                    String errorMessage) {
        return DocumentEnrichmentCallbackResponse.builder()
                .lectureId(lectureId)
                .status("FAILED")
                .message("Failed to process enriched chunks: " + errorMessage)
                .lectureKnowledgeId(null)
                .chunksSaved(0)
                .contentType(contentType)
                .build();
    }

    /**
     * Get existing LectureKnowledge or create new one using mapper
     */
    private LectureKnowledge getOrCreateLectureKnowledge(UUID lectureId, String contentType) {
        LectureKnowledge existing = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);

        if (existing != null) {
            log.info("Found existing LectureKnowledge for lecture: {}", lectureId);
            return existing;
        }

        log.info("Creating new LectureKnowledge for lecture: {}", lectureId);
        LectureKnowledge newLectureKnowledge = documentEnrichmentCallbackMapper.toLectureKnowledge(lectureId, contentType);

        return lectureKnowledgeRepository.save(newLectureKnowledge);
    }

    /**
     * Delete existing chunks for a lecture
     */
    private void deleteExistingChunks(UUID lectureId) {
        long existingCount = lectureKnowledgeChunkRepository.countByLectureKnowledgeLectureKnowledgeId(lectureId);
        if (existingCount > 0) {
            lectureKnowledgeChunkRepository.deleteByLectureKnowledgeLectureKnowledgeId(lectureId);
            log.info("Deleted {} existing chunks for lecture: {}", existingCount, lectureId);
        }
    }

    /**
     * Save enriched chunks to database using mapper
     */
    private List<LectureKnowledgeChunk> saveEnrichedChunks(
            LectureKnowledge lectureKnowledge,
            List<DocumentEnrichmentCallbackRequest.EnrichedChunk> enrichedChunks) {

        List<LectureKnowledgeChunk> chunks = documentEnrichmentCallbackMapper.toChunkEntities(
                lectureKnowledge, enrichedChunks);

        List<LectureKnowledgeChunk> savedChunks = lectureKnowledgeChunkRepository.saveAll(chunks);
        log.info("Saved {} enriched chunks to database", savedChunks.size());

        return savedChunks;
    }

    /**
     * Sync chunks to Qdrant with retry mechanism
     */
    private void syncChunksToQdrant(List<LectureKnowledgeChunk> chunks, LectureKnowledge lectureKnowledge) {
        log.info("Starting vector sync to Qdrant for {} chunks", chunks.size());

        int attempt = 0;
        Exception lastException = null;

        while (attempt < retryMaxAttempts) {
            attempt++;
            try {
                // Generate embeddings for all chunks
                List<String> contents = chunks.stream()
                        .map(LectureKnowledgeChunk::getChunkContent)
                        .collect(Collectors.toList());

                log.debug("Generating embeddings for {} chunks (attempt {}/{})",
                        contents.size(), attempt, retryMaxAttempts);
                List<List<Float>> embeddings = embeddingService.generateEmbeddings(contents);

                // Build VectorPoints with metadata
                List<QdrantVectorStoreService.VectorPoint> vectorPoints = new ArrayList<>();
                for (int i = 0; i < chunks.size(); i++) {
                    LectureKnowledgeChunk chunk = chunks.get(i);
                    List<Float> embedding = embeddings.get(i);

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("chunkId", chunk.getId().toString());
                    payload.put("lectureKnowledgeId", lectureKnowledge.getLectureKnowledgeId().toString());
                    payload.put("content", chunk.getChunkContent());
                    payload.put("chunkIndex", chunk.getChunkIndex());
                    payload.put("contentType", lectureKnowledge.getContentType());

                    // Add location metadata
                    if (chunk.getPageNumber() != null) {
                        payload.put("pageNumber", chunk.getPageNumber());
                    }

                    vectorPoints.add(new QdrantVectorStoreService.VectorPoint(
                            chunk.getQdrantPointId(),
                            embedding,
                            payload
                    ));
                }

                // Upsert to Qdrant
                log.debug("Upserting {} vectors to Qdrant (attempt {}/{})",
                        vectorPoints.size(), attempt, retryMaxAttempts);
                vectorStoreService.upsertPoints(collectionName, vectorPoints);

                log.info("Successfully synced {} chunks to Qdrant on attempt {}", chunks.size(), attempt);
                return; // Success, exit retry loop

            } catch (Exception e) {
                lastException = e;
                log.warn("Qdrant sync attempt {}/{} failed: {}", attempt, retryMaxAttempts, e.getMessage());

                if (attempt < retryMaxAttempts) {
                    try {
                        log.debug("Waiting {}ms before retry...", retryDelayMs);
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Qdrant sync interrupted", ie);
                    }
                }
            }
        }

        // All retries failed
        String errorMsg = String.format("Failed to sync to Qdrant after %d attempts: %s",
                retryMaxAttempts, lastException != null ? lastException.getMessage() : "Unknown error");
        log.error(errorMsg, lastException);
        throw new RuntimeException(errorMsg, lastException);
    }
}
