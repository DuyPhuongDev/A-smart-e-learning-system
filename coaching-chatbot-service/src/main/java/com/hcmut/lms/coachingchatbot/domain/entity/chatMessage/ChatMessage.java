package com.hcmut.lms.coachingchatbot.domain.entity.chatMessage;

import com.hcmut.lms.coachingchatbot.domain.entity.chatSession.ChatSession;
import com.hcmut.lms.coachingchatbot.domain.entity.messageKnowledgeSource.MessageKnowledgeSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a single message in a chat conversation.
 * Can be from 'user' or 'assistant' (AI).
 * Messages with is_error=true are excluded from context window.
 */
@Entity
@Table(name = "chat_messages", schema = "coaching_chatbot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MessageRole role;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "language_code", length = 10)
    private String languageCode;

    @Column(name = "is_error", nullable = false)
    @Builder.Default
    private Boolean isError = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    /**
     * Knowledge sources used to generate this assistant response.
     * Empty for user messages.
     */
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MessageKnowledgeSource> knowledgeSources = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (messageId == null) {
            messageId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (isError == null) {
            isError = false;
        }
    }
}
