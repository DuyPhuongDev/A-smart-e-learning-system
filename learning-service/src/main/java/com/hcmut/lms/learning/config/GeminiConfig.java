package com.hcmut.lms.learning.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {

    /**
     * Gemini API Key
     * NÊN cấu hình qua biến môi trường (GEMINI_API_KEY)
     */
    private String apiKey;

    /**
     * Chat Model (LLM)
     * Ví dụ:
     * - gemini-1.5-flash (nhanh, rẻ)
     * - gemini-1.5-pro   (chất lượng cao)
     */
    private String chatModelName = "gemini-1.5-flash";

    /**
     * Temperature điều khiển độ sáng tạo (0.0 - 1.0)
     */
    private double temperature = 0.7;

    /**
     * Số token tối đa cho output
     */
    private int maxOutputTokens = 2048;

    /**
     * Embedding Model
     */
    private String embeddingModelName = "text-embedding-004";

    /**
     * Chat Model Bean (LLM)
     */
    @Bean
    public ChatModel geminiChatModel() {
        log.info("Initializing Gemini Chat Model [{}]", chatModelName);

        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(chatModelName)
                .temperature(temperature)
                .maxOutputTokens(maxOutputTokens)
                .logRequestsAndResponses(true) // Bật khi debug, tắt ở prod nếu cần
                .build();
    }

    /**
     * Embedding Model Bean
     * Dùng cho vector store (Qdrant, Pinecone, Weaviate...)
     */
    @Bean
    public EmbeddingModel geminiEmbeddingModel() {
        log.info("Initializing Gemini Embedding Model [{}]", embeddingModelName);

        return GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(embeddingModelName)
                .logRequestsAndResponses(false)
                .build();
    }
}
