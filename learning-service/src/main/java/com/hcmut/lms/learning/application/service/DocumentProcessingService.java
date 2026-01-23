package com.hcmut.lms.learning.application.service;

import com.hcmut.lms.learning.application.dto.request.ProcessLectureRequest;
import com.hcmut.lms.learning.application.dto.response.ProcessingStatusResponse;

import java.util.List;
import java.util.UUID;

/**
 * Main service for orchestrating document processing pipeline
 * Coordinates extraction, enhancement, chunking, embedding, and storage
 */
public interface DocumentProcessingService {

    /**
     * Process a single lecture asynchronously
     *
     * @param request Process request containing lecture ID
     * @return Processing status response
     */
    ProcessingStatusResponse processLecture(ProcessLectureRequest request);

    /**
     * Process multiple lectures in batch
     *
     * @param lectureIds List of lecture IDs to process
     * @return List of processing status responses
     */
    List<ProcessingStatusResponse> processLectures(List<UUID> lectureIds);

    /**
     * Process all lectures in a chapter
     *
     * @param chapterId Chapter ID
     * @return List of processing status responses
     */
    List<ProcessingStatusResponse> processChapterLectures(UUID chapterId);

    /**
     * Get processing status for a lecture
     *
     * @param lectureId Lecture ID
     * @return Current processing status
     */
    ProcessingStatusResponse getProcessingStatus(UUID lectureId);

    /**
     * Get processing status by lecture knowledge ID
     *
     * @param lectureKnowledgeId Lecture knowledge ID
     * @return Current processing status
     */
    ProcessingStatusResponse getProcessingStatusByKnowledgeId(UUID lectureKnowledgeId);

    /**
     * Retry failed processing for a lecture
     *
     * @param lectureId Lecture ID
     * @return Processing status response
     */
    ProcessingStatusResponse retryProcessing(UUID lectureId);

    /**
     * Cancel ongoing processing for a lecture
     *
     * @param lectureId Lecture ID
     */
    void cancelProcessing(UUID lectureId);

    /**
     * Reprocess a lecture (delete existing and process again)
     *
     * @param lectureId Lecture ID
     * @return Processing status response
     */
    ProcessingStatusResponse reprocessLecture(UUID lectureId);
}
