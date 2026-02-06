package com.hcmut.lms.coachingchatbot.domain.repository;

import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ChatSession entity
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    /**
     * Find chat session by student ID and lecture ID
     * @param studentId the student ID
     * @param lectureId the lecture ID
     * @return Optional containing the session if found
     */
    Optional<ChatSession> findByStudentIdAndLectureId(UUID studentId, UUID lectureId);

    /**
     * Check if a session exists for student and lecture
     * @param studentId the student ID
     * @param lectureId the lecture ID
     * @return true if session exists
     */
    boolean existsByStudentIdAndLectureId(UUID studentId, UUID lectureId);
}
