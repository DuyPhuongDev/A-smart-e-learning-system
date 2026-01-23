package com.hcmut.lms.learning.domain.entity.lectureKnowledge;

/**
 * Enum representing the sync status of lecture knowledge
 * Maps to sync_status column in learning.lecture_knowledge table
 */
public enum SyncStatus {
    /**
     * Newly created, not yet processed
     */
    PENDING,

    /**
     * Currently running a chunking and embedding job
     */
    PROCESSING,

    /**
     * Substates of PROCESSING for better tracking
     */
    TRANSCRIBING,

    /**
     * Substates of PROCESSING for enhancing text (e.g., cleaning, formatting)
     */
    ENHANCING_TEXT,

    /**
     * Substates of PROCESSING for splitting text into smaller chunks
     */
    CHUNKING,

    /**
     * Substates of PROCESSING for generating embeddings for each chunk
     */
    EMBEDDING,

    /**
     * Substates of PROCESSING for storing embeddings into Qdrant
     */
    STORING,

    /**
     * Successfully loaded into Qdrant
     */
    COMPLETED,

    /**
     * Error occurred (file error, API error)
     */
    FAILED,

    /**
     * Teacher updated the original lecture, needs reprocessing
     */
    OUTDATED
}
