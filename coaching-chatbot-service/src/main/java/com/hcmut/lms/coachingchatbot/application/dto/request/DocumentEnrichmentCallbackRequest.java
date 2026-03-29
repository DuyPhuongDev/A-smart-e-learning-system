package com.hcmut.lms.coachingchatbot.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for document/text enrichment callback from AWS Fargate worker
 * Contains all enriched chunks in a single request
 *
 * Payload format:
 * {
 *   "lectureId": "uuid",
 *   "contentType": "DOCUMENT" | "TEXT",
 *   "lectureTitle": "...",
 *   "totalChunks": 10,
 *   "chunks": [
 *     {
 *       "chunkIndex": 0,
 *       "chunkContent": "enriched content...",
 *       "originalText": "...",
 *       "summary": "...",
 *       "questions": ["Q1", "Q2", "Q3", "Q4"],
 *       "pageNumber": 1,
 *       "tokenCount": 450
 *     }
 *   ]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentEnrichmentCallbackRequest {

    @NotNull(message = "Lecture ID is required")
    private UUID lectureId;

    @NotNull(message = "Content type is required")
    private String contentType; // "DOCUMENT" or "TEXT"

    private String lectureTitle;

    private Integer totalChunks;

    @NotEmpty(message = "Chunks list cannot be empty")
    @Valid
    private List<EnrichedChunk> chunks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EnrichedChunk {

        @NotNull(message = "Chunk index is required")
        private Integer chunkIndex;

        @NotNull(message = "Chunk content is required")
        private String chunkContent;

        private String originalText;

        private String summary;

        private List<String> questions;

        /**
         * Page number for DOCUMENT type lectures (null for TEXT)
         */
        private Integer pageNumber;

        /**
         * Start page for multi-page chunks
         */
        private Integer startPage;

        /**
         * End page for multi-page chunks
         */
        private Integer endPage;

        /**
         * Token count of the chunk
         */
        private Integer tokenCount;

        /**
         * Whether this chunk was successfully enriched by LLM
         */
        private Boolean isEnriched;
    }
}
