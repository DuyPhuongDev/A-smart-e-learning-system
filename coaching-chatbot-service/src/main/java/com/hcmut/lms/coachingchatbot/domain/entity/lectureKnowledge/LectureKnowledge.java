package com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge;

import com.hcmut.lms.coachingchatbot.domain.entity.BaseEntity;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity mapping to learning.lecture_knowledge table
 * Primary key = lecture_id (1-1 relationship with lectures in
 * course-management)
 */
@Entity
@Table(name = "lecture_knowledge", schema = "learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LectureKnowledge extends BaseEntity {

    /**
     * Uses lecture_id as primary-key to ensure 1-1 relationship with lectures
     */
    @Id
    @Column(name = "lecture_knowledge_id", nullable = false)
    private UUID lectureKnowledgeId;

    /**
     * Sync status: PENDING, PROCESSING, COMPLETED, FAILED, OUTDATED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false)
    @Builder.Default
    private SyncStatus syncStatus = SyncStatus.PENDING;

    /**
     * Timestamp when vectors were last synced to Qdrant
     */
    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    /**
     * Error message if sync_status = FAILED
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Embedding model used for vectorization (e.g., 'text-embedding-004')
     */
    @Column(name = "embedding_model")
    private String embeddingModel;

    /**
     * Content type of the lecture (e.g., VIDEO, DOCUMENT, TEXT)
     * Stored as string value from ContentType enum
     */
    @Column(name = "content_type")
    private String contentType;

    /**
     * Total number of chunks created for this lecture
     */
    @Column(name = "total_chunks", nullable = false)
    @Builder.Default
    private Integer totalChunks = 0;

    @OneToMany(mappedBy = "lectureKnowledge", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LectureKnowledgeChunk> chunks = new ArrayList<>();
}
