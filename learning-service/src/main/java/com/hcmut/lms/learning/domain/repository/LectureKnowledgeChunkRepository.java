package com.hcmut.lms.learning.domain.repository;

import com.hcmut.lms.learning.domain.entity.lectureKnowledgeChunk.LectureKnowledgeChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LectureKnowledgeChunkRepository extends JpaRepository<LectureKnowledgeChunk, UUID> {

    /**
     * Find all chunks for a lecture knowledge
     */
    List<LectureKnowledgeChunk> findByLectureKnowledgeLectureKnowledgeId(UUID lectureKnowledgeId);

    /**
     * Find all chunks ordered by chunk index
     */
    List<LectureKnowledgeChunk> findByLectureKnowledgeLectureKnowledgeIdOrderByChunkIndexAsc(UUID lectureKnowledgeId);

    /**
     * Delete all chunks for a lecture knowledge
     */
    void deleteByLectureKnowledgeLectureKnowledgeId(UUID lectureKnowledgeId);

    /**
     * Find chunk by Qdrant point ID
     */
    LectureKnowledgeChunk findByQdrantPointId(UUID qdrantPointId);

    /**
     * Get all Qdrant point IDs for lecture knowledge (for batch deletion from Qdrant)
     */
    @Query("SELECT c.qdrantPointId FROM LectureKnowledgeChunk c WHERE c.lectureKnowledge.lectureKnowledgeId = :lectureKnowledgeId")
    List<UUID> findQdrantPointIdsByLectureKnowledgeId(@Param("lectureKnowledgeId") UUID lectureKnowledgeId);

    /**
     * Count chunks for lecture knowledge
     */
    long countByLectureKnowledgeLectureKnowledgeId(UUID lectureKnowledgeId);
}
