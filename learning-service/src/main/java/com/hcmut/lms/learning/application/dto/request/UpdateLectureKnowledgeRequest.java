package com.hcmut.lms.learning.application.dto.request;

/**
 * Request DTO for updating lecture knowledge
 * Note: Most fields are managed by the processing pipeline
 */
public record UpdateLectureKnowledgeRequest(
    String title,        // Not stored in DB, but triggers OUTDATED status
    String description,  // Not stored in DB, but triggers OUTDATED status
    String status,       // SyncStatus value: PENDING, PROCESSING, COMPLETED, FAILED, OUTDATED
    String embeddingModel
) {}
