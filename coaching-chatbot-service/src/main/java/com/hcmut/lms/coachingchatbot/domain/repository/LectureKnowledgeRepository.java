package com.hcmut.lms.coachingchatbot.domain.repository;

import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.LectureKnowledge;
import com.hcmut.lms.coachingchatbot.domain.entity.lectureKnowledge.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LectureKnowledgeRepository extends JpaRepository<LectureKnowledge, UUID> {

    /**
     * Find by lecture knowledge ID (which is same as lecture ID - 1:1 relationship)
     */
    LectureKnowledge findByLectureKnowledgeId(UUID lectureKnowledgeId);

    /**
     * Check if a lecture knowledge exists for the given lecture ID
     */
    boolean existsByLectureKnowledgeId(UUID lectureKnowledgeId);

    /**
     * Find all by sync status
     */
    List<LectureKnowledge> findBySyncStatus(SyncStatus syncStatus);

    /**
     * Find all pending or failed lectures for reprocessing
     */
    List<LectureKnowledge> findBySyncStatusIn(List<SyncStatus> statuses);
}
