package com.hcmut.lms.coachingchatbot.domain.entity.messageKnowledgeSource;

import com.hcmut.lms.coachingchatbot.domain.entity.chatMessage.ChatMessage;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a knowledge source reference for an assistant message.
 * Stores chunkId and position info to help users trace back to original content.
 */
@Entity
@Table(name = "message_knowledge_sources", schema = "coaching_chatbot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageKnowledgeSource {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @Column(name = "chunk_id", nullable = false)
    private UUID chunkId;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "start_time_seconds")
    private Integer startTimeSeconds;

    @Column(name = "end_time_seconds")
    private Integer endTimeSeconds;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
