package com.hcmut.lms.coachingchatbot.domain.repository;

import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for ChatMessage entity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * Find recent non-error messages for building context window (3 Q&A pairs = 6 messages)
     * @param session the chat session
     * @return List of recent messages excluding errors, ordered by newest first
     */
    List<ChatMessage> findTop6BySessionAndIsErrorFalseOrderByCreatedAtDesc(ChatSession session);

    /**
     * Find all messages for a session ordered by creation time (with knowledge sources eagerly loaded)
     * @param session the chat session
     * @return List of all messages ordered by oldest first
     */
    @Query("SELECT DISTINCT m FROM ChatMessage m " +
           "LEFT JOIN FETCH m.knowledgeSources " +
           "WHERE m.session = :session " +
           "ORDER BY m.createdAt ASC")
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(@Param("session") ChatSession session);

    /**
     * Find messages for a session with pagination, ordered by creation time descending (with knowledge sources)
     * Uses EntityGraph for pagination support
     * @param session the chat session
     * @param pageable pagination info
     * @return Page of messages ordered by newest first
     */
    @EntityGraph(attributePaths = {"knowledgeSources"})
    @Query("SELECT m FROM ChatMessage m WHERE m.session = :session ORDER BY m.createdAt DESC")
    Page<ChatMessage> findBySessionOrderByCreatedAtDesc(@Param("session") ChatSession session, Pageable pageable);

    /**
     * Count total messages for a session
     * @param session the chat session
     * @return total message count
     */
    long countBySession(ChatSession session);

    /**
     * Delete all messages for a session
     * @param session the chat session
     */
    void deleteBySession(ChatSession session);
}
