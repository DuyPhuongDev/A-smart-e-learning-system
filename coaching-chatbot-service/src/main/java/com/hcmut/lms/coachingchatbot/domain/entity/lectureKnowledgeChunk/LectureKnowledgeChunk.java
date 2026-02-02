package com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk;

import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity mapping to learning.lecture_knowledge_chunks table
 * Stores chunked text and its exact position in the original lecture
 */
@Entity
@Table(name = "lecture_knowledge_chunks", schema = "learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LectureKnowledgeChunk {

    @Id
    @Column(name = "lecture_knowledge_chunks_id", nullable = false)
    private UUID id;

    /**
     * Order/index of this chunk within the lecture (0, 1, 2, ...)
     */
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    /**
     * Text content used for search (Context)
     */
    @Column(name = "chunk_content", columnDefinition = "TEXT", nullable = false)
    private String chunkContent;

    /**
     * ID of the vector in Qdrant
     * Used to delete the corresponding vector when lecture is deleted
     */
    @Column(name = "qdrant_point_id", nullable = false)
    private UUID qdrantPointId;

    // ========== LOCATION TRACKING FIELDS ==========

    /**
     * For Video (Transcript): Start time in seconds (e.g., second 120)
     */
    @Column(name = "start_time_seconds")
    private Integer startTimeSeconds;

    /**
     * For Video (Transcript): End time in seconds (e.g., second 135)
     */
    @Column(name = "end_time_seconds")
    private Integer endTimeSeconds;

    /**
     * For Documents (PDF/Slide/Doc): Page number (e.g., page 5)
     */
    @Column(name = "page_number")
    private Integer pageNumber;

    /**
     * Token count of the chunk (for an optimizing GPT context window)
     */
    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_knowledge_id", nullable = false)
    private LectureKnowledge lectureKnowledge;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
