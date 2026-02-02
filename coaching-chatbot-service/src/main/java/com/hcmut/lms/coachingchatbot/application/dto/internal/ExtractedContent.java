package com.hcmut.lms.coachingchatbot.application.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Internal DTO for holding extracted content during processing pipeline
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedContent {

    private UUID lectureId;
    private String sourceType;
    private String rawContent;
    private String enhancedContent;
    private List<ContentChunk> chunks;
    private Map<String, Object> metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentChunk {
        private Integer index;
        private String content;
        private String chunkType;
        private Integer tokenCount;
        private List<Float> embedding;
        private Map<String, Object> metadata;
    }
}
