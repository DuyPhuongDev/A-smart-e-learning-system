package com.hcmut.lms.coachingchatbot.domain.repository;

import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import com.hcmut.lms.coachingchatbot.domain.entity.messageKnowledgeSource.MessageKnowledgeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for MessageKnowledgeSource entity
 */
@Repository
public interface MessageKnowledgeSourceRepository extends JpaRepository<MessageKnowledgeSource, UUID> {

    /**
     * Find all knowledge sources for a specific message
     * @param message the chat message
     * @return List of knowledge sources
     */
    List<MessageKnowledgeSource> findByMessage(ChatMessage message);

    /**
     * Find all knowledge sources for multiple messages
     * @param messages list of chat messages
     * @return List of knowledge sources
     */
    List<MessageKnowledgeSource> findByMessageIn(List<ChatMessage> messages);

    /**
     * Delete all knowledge sources for a message
     * @param message the chat message
     */
    void deleteByMessage(ChatMessage message);

    /**
     * Delete all knowledge sources for multiple messages
     * @param messages list of chat messages
     */
    void deleteByMessageIn(List<ChatMessage> messages);
}
