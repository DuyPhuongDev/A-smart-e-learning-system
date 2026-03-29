package com.hcmut.lms.coachingchatbot.config;

import com.hcmut.lms.coachingchatbot.application.service.EmbeddingService;
import com.hcmut.lms.coachingchatbot.application.service.QdrantVectorStoreService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to initialize Qdrant collections on application startup
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class QdrantCollectionInitializer {

    private final QdrantVectorStoreService vectorStoreService;
    private final EmbeddingService embeddingService;

    @Value("${qdrant.collection-name:lecture_knowledge}")
    private String collectionName;

    /**
     * Initialize Qdrant collection on application startup
     * If initialization fails, log warning but allow application to start
     */
    @PostConstruct
    public void initializeCollections() {
        log.info("Initializing Qdrant collections on startup...");

        try {
            int vectorDimension = embeddingService.getEmbeddingDimension();
            log.info("Detected embedding dimension: {}", vectorDimension);

            // Check if collection already exists
            if (vectorStoreService.collectionExists(collectionName)) {
                log.info("Collection '{}' already exists, skipping initialization", collectionName);
                return;
            }

            // Create collection
            vectorStoreService.initializeCollection(collectionName, vectorDimension);
            log.info("Successfully initialized collection '{}'", collectionName);

        } catch (Exception e) {
            log.warn("Failed to initialize Qdrant collection '{}': {}. " +
                    "The service will continue to run, but vector operations may fail until Qdrant is available.",
                    collectionName, e.getMessage());
            log.debug("Qdrant initialization error details:", e);
        }
    }
}
