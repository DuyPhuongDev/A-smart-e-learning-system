package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.dto.internal.ExtractedContent;
import com.hcmut.lms.coachingchatbot.application.dto.internal.ProcessingContext;
import com.hcmut.lms.coachingchatbot.application.dto.request.ProcessLectureRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.ProcessingStatusResponse;
import com.hcmut.lms.coachingchatbot.application.service.*;
import com.hcmut.lms.coachingchatbot.application.strategy.content_process.ContentProcessor;
import com.hcmut.lms.coachingchatbot.application.strategy.content_process.ContentProcessorFactory;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.dto.LectureResponse;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.ContentType;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeChunkRepository;
import com.hcmut.lms.coachingchatbot.domain.repository.LectureKnowledgeRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main orchestrator for document processing pipeline
 * Coordinates extraction, enhancement, chunking, embedding, and storage
 */
@Service
@RequiredArgsConstructor
@Slf4j

public class DocumentProcessingServiceImpl implements DocumentProcessingService {

    private final CourseManagementClient courseManagementClient;
    private final ContentProcessorFactory contentProcessorFactory;
    private final TextEnhancementService textEnhancementService;
    private final TextChunkingService textChunkingService;
    private final EmbeddingService embeddingService;
    private final QdrantVectorStoreService vectorStoreService;
    private final LectureKnowledgeRepository lectureKnowledgeRepository;
    private final LectureKnowledgeChunkRepository lectureKnowledgeChunkRepository;

    @Value("${qdrant.collection-name:lecture_knowledge}")
    private String collectionName;

    private static final boolean enhanceText = true;

    @Getter
    private static final String embeddingModelName = "gemini-text-embedding-004";

    @Override
    @Transactional
    public ProcessingStatusResponse processLecture(ProcessLectureRequest request) {
        UUID lectureId = request.getLectureId();
        log.info("Starting document processing for lecture: {}", lectureId);

        // Check if already processed
        LectureKnowledge existingKnowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
        if (existingKnowledge != null && !request.isForceReprocess()) {
            if (existingKnowledge.getSyncStatus() == SyncStatus.COMPLETED) {
                log.info("Lecture {} already processed, skipping", lectureId);
                return buildStatusResponse(existingKnowledge, "Already processed");
            }
        }

        // If force reprocess, delete existing
        if (request.isForceReprocess() && existingKnowledge != null) {
            deleteExistingKnowledge(existingKnowledge);
        }

        // Fetch lecture info from course management service
        LectureResponse lecture;
        try {
            lecture = courseManagementClient.getLectureById(lectureId);
        } catch (Exception e) {
            log.error("Failed to fetch lecture info: {}", e.getMessage());
            return buildErrorResponse(lectureId, "Failed to fetch lecture info: " + e.getMessage());
        }

        // Create LectureKnowledge entity (using lectureId as primary key for 1:1
        // relationship)
        LectureKnowledge lectureKnowledge = createLectureKnowledge(lecture);

        // Start async processing
        processLectureAsync(lectureKnowledge, lecture);

        return buildStatusResponse(lectureKnowledge, "Processing started");
    }

    @Async
    protected void processLectureAsync(LectureKnowledge lectureKnowledge, LectureResponse lecture) {
        long startTime = System.currentTimeMillis();
        UUID lectureKnowledgeId = lectureKnowledge.getLectureKnowledgeId();

        try {
            // Step 1: Build processing context
            ProcessingContext context = buildProcessingContext(lecture, lectureKnowledge);
            updateSyncStatus(lectureKnowledgeId, SyncStatus.PROCESSING, null);

            // Step 2: Get appropriate content processor
            ContentType contentType = ContentType.fromLectureType(
                    lecture.getLectureType(),
                    lecture.getVideoUrl(),
                    lecture.getFileFormat());
            ContentProcessor processor = contentProcessorFactory.getProcessor(contentType);
            log.info("Using processor: {} for content type: {}", processor.getProcessorName(), contentType);

            // Step 3: Extract content
            ExtractedContent extractedContent = processor.extractContent(context);
            log.info("Content extracted, raw length: {} characters",
                    extractedContent.getRawContent().length());

            // Step 4: Enhance text using LLM (optional)
            String processedText = extractedContent.getRawContent();
            if (enhanceText && !processedText.isEmpty()) {
                log.info("Enhancing text...");
                processedText = textEnhancementService.enhanceText(
                        processedText,
                        lecture.getTitle());
                extractedContent.setEnhancedContent(processedText);
            }

            // Step 5: Chunk text
            log.info("Chunking text...");
            List<TextChunkingService.TextChunk> chunks = textChunkingService.chunkTextSemantic(processedText);
            log.info("Created {} chunks", chunks.size());

            // Step 6: Generate embeddings
            log.info("Generating embeddings...");
            List<String> chunkTexts = chunks.stream()
                    .map(TextChunkingService.TextChunk::content)
                    .collect(Collectors.toList());
            List<List<Float>> embeddings = embeddingService.generateEmbeddings(chunkTexts);

            // Step 7: Save chunks to database and Qdrant
            log.info("Saving chunks to database and vector store...");
            saveChunks(lectureKnowledge, chunks, embeddings, extractedContent.getMetadata(), contentType);

            // Step 8: Update status to completed
            updateSyncStatusCompleted(lectureKnowledgeId);

            log.info("Document processing completed for lecture {} in {} ms",
                    lecture.getId(), System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("Document processing failed for lecture {}: {}",
                    lectureKnowledgeId, e.getMessage(), e);
            updateSyncStatus(lectureKnowledgeId, SyncStatus.FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<ProcessingStatusResponse> processLectures(List<UUID> lectureIds) {
        return lectureIds.stream()
                .map(id -> processLecture(ProcessLectureRequest.builder()
                        .lectureId(id)
                        .forceReprocess(false)
                        .build()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ProcessingStatusResponse> processChapterLectures(UUID chapterId) {
        log.info("Processing all lectures in chapter: {}", chapterId);

        List<LectureResponse> lectures = courseManagementClient.getLecturesByChapterId(chapterId);
        List<UUID> lectureIds = lectures.stream()
                .map(LectureResponse::getId)
                .collect(Collectors.toList());

        return processLectures(lectureIds);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessingStatusResponse getProcessingStatus(UUID lectureId) {
        LectureKnowledge knowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
        if (knowledge != null) {
            return buildStatusResponse(knowledge, null);
        }
        return buildErrorResponse(lectureId, "No processing record found");
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessingStatusResponse getProcessingStatusByKnowledgeId(UUID lectureKnowledgeId) {
        return getProcessingStatus(lectureKnowledgeId);
    }

    @Override
    @Transactional
    public ProcessingStatusResponse retryProcessing(UUID lectureId) {
        return processLecture(ProcessLectureRequest.builder()
                .lectureId(lectureId)
                .forceReprocess(true)
                .build());
    }

    @Override
    @Transactional
    public void cancelProcessing(UUID lectureId) {
        LectureKnowledge knowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
        if (knowledge != null) {
            knowledge.setSyncStatus(SyncStatus.FAILED);
            knowledge.setErrorMessage("Processing cancelled by user");
            lectureKnowledgeRepository.save(knowledge);
        }
    }

    @Override
    @Transactional
    public ProcessingStatusResponse reprocessLecture(UUID lectureId) {
        LectureKnowledge knowledge = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureId);
        if (knowledge != null) {
            knowledge.setSyncStatus(SyncStatus.OUTDATED);
            lectureKnowledgeRepository.save(knowledge);
        }

        return processLecture(ProcessLectureRequest.builder()
                .lectureId(lectureId)
                .forceReprocess(true)
                .build());
    }

    // ==================== Private Helper Methods ====================

    private ProcessingContext buildProcessingContext(LectureResponse lecture, LectureKnowledge knowledge) {
        return ProcessingContext.builder()
                .lectureId(lecture.getId())
                .lectureKnowledgeId(knowledge.getLectureKnowledgeId())
                .lectureTitle(lecture.getTitle())
                .lectureDescription(lecture.getDescription())
                .lectureType(lecture.getLectureType())
                .videoUrl(lecture.getVideoUrl())
                .videoDuration(lecture.getDuration())
                .fileUrl(lecture.getFileUrl())
                .fileFormat(lecture.getFileFormat())
                .numPages(lecture.getNumPages())
                .textContent(lecture.getContent())
                .formatType(lecture.getFormatType())
                .startTime(System.currentTimeMillis())
                .build();
    }

    private LectureKnowledge createLectureKnowledge(LectureResponse lecture) {
        // Determine content type from lecture info
        // For TEXT lectures, use formatType; for DOCUMENT lectures, use fileFormat
        String formatOrType = "TEXT".equalsIgnoreCase(lecture.getLectureType())
                ? lecture.getFormatType()
                : lecture.getFileFormat();

        ContentType contentType = ContentType.fromLectureType(
                lecture.getLectureType(),
                lecture.getVideoUrl(),
                formatOrType);

        LectureKnowledge knowledge = LectureKnowledge.builder()
                .lectureKnowledgeId(lecture.getId())
                .syncStatus(SyncStatus.PENDING)
                .embeddingModel(embeddingModelName)
                .contentType(contentType.getValue())
                .build();

        return lectureKnowledgeRepository.save(knowledge);
    }

    private void deleteExistingKnowledge(LectureKnowledge knowledge) {
        UUID lectureKnowledgeId = knowledge.getLectureKnowledgeId();

        List<UUID> qdrantPointIds = lectureKnowledgeChunkRepository
                .findQdrantPointIdsByLectureKnowledgeId(lectureKnowledgeId);

        if (!qdrantPointIds.isEmpty()) {
            vectorStoreService.deletePoints(collectionName, qdrantPointIds);
        }

        lectureKnowledgeChunkRepository.deleteByLectureKnowledgeLectureKnowledgeId(lectureKnowledgeId);
        lectureKnowledgeRepository.delete(knowledge);
    }

    private void saveChunks(LectureKnowledge lectureKnowledge,
            List<TextChunkingService.TextChunk> chunks,
            List<List<Float>> embeddings,
            Map<String, Object> metadata,
            ContentType contentType) {

        vectorStoreService.initializeCollection(collectionName, embeddingService.getEmbeddingDimension());

        List<LectureKnowledgeChunk> dbChunks = new ArrayList<>();
        List<QdrantVectorStoreService.VectorPoint> vectorPoints = new ArrayList<>();

        boolean isVideo = contentType == ContentType.VIDEO_YOUTUBE || contentType == ContentType.VIDEO_S3;
        boolean isDocument = contentType == ContentType.DOCUMENT_PDF ||
                contentType == ContentType.DOCUMENT_DOCX ||
                contentType == ContentType.DOCUMENT_PPTX;
        boolean isText = contentType == ContentType.TEXT_CONTENT ||
                contentType == ContentType.TEXT_HTML ||
                contentType == ContentType.TEXT_MARKDOWN;

        for (int i = 0; i < chunks.size(); i++) {
            TextChunkingService.TextChunk chunk = chunks.get(i);
            List<Float> embedding = embeddings.get(i);
            UUID qdrantPointId = UUID.randomUUID();
            UUID chunkId = UUID.randomUUID();

            LectureKnowledgeChunk.LectureKnowledgeChunkBuilder<?, ?> chunkBuilder = LectureKnowledgeChunk.builder()
                    .id(chunkId)
                    .lectureKnowledge(lectureKnowledge)
                    .chunkIndex(i)
                    .chunkContent(chunk.content())
                    .qdrantPointId(qdrantPointId)
                    .tokenCount(chunk.tokenCount());

            if (isVideo && metadata != null) {
                Object startTime = metadata.get("startTime_" + i);
                Object endTime = metadata.get("endTime_" + i);
                if (startTime != null) {
                    chunkBuilder.startTimeSeconds(((Number) startTime).intValue());
                }
                if (endTime != null) {
                    chunkBuilder.endTimeSeconds(((Number) endTime).intValue());
                }
            } else if (isDocument && metadata != null) {
                Object pageNum = metadata.get("pageNumber_" + i);
                if (pageNum != null) {
                    chunkBuilder.pageNumber(((Number) pageNum).intValue());
                }
            } else if (isText && metadata != null) {
                // Text lectures also use page number (logical page/section number)
                Object pageNum = metadata.get("pageNumber_" + i);
                if (pageNum != null) {
                    chunkBuilder.pageNumber(((Number) pageNum).intValue());
                }
            }

            dbChunks.add(chunkBuilder.build());

            Map<String, Object> payload = new HashMap<>();
            payload.put("lectureKnowledgeId", lectureKnowledge.getLectureKnowledgeId().toString());
            payload.put("chunkId", chunkId.toString());
            payload.put("content", chunk.content());
            payload.put("chunkIndex", i);
            if (metadata != null) {
                payload.putAll(metadata);
            }

            vectorPoints.add(new QdrantVectorStoreService.VectorPoint(qdrantPointId, embedding, payload));
        }

        // Save chunks to database
        lectureKnowledgeChunkRepository.saveAll(dbChunks);

        // Update totalChunks on lectureKnowledge
        lectureKnowledge.setTotalChunks(chunks.size());
        lectureKnowledgeRepository.save(lectureKnowledge);

        // Save vectors to Qdrant
        vectorStoreService.upsertPoints(collectionName, vectorPoints);
    }

    private void updateSyncStatus(UUID lectureKnowledgeId, SyncStatus status, String errorMessage) {
        LectureKnowledge lk = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId);
        if (lk != null) {
            lk.setSyncStatus(status);
            if (errorMessage != null) {
                lk.setErrorMessage(errorMessage);
            }
            lectureKnowledgeRepository.save(lk);
        }
    }

    private void updateSyncStatusCompleted(UUID lectureKnowledgeId) {
        LectureKnowledge lk = lectureKnowledgeRepository.findByLectureKnowledgeId(lectureKnowledgeId);
        if (lk != null) {
            lk.setSyncStatus(SyncStatus.COMPLETED);
            lk.setLastSyncedAt(Instant.now());
            lk.setErrorMessage(null);
            lectureKnowledgeRepository.save(lk);
        }
    }

    private ProcessingStatusResponse buildStatusResponse(LectureKnowledge lk, String currentStep) {
        return ProcessingStatusResponse.builder()
                .lectureKnowledgeId(lk.getLectureKnowledgeId())
                .status(lk.getSyncStatus().name())
                .startedAt(lk.getCreatedAt())
                .errorMessage(lk.getErrorMessage())
                .embeddingModel(lk.getEmbeddingModel())
                .collectionName("lecture_knowledge")
                .totalChunks(lk.getTotalChunks())
                .currentStep(currentStep)
                .build();
    }

    private ProcessingStatusResponse buildErrorResponse(UUID lectureId, String errorMessage) {
        return ProcessingStatusResponse.builder()
                .lectureKnowledgeId(lectureId)
                .status("FAILED")
                .errorMessage(errorMessage)
                .build();
    }
}
