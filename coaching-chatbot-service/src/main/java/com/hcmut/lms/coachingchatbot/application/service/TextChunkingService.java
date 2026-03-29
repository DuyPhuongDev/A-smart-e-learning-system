package com.hcmut.lms.coachingchatbot.application.service;

import java.util.List;

/**
 * Service for chunking text into smaller segments for embedding
 */
public interface TextChunkingService {

    /**
     * Split text into chunks with overlap
     *
     * @param text      Text to chunk
     * @param chunkSize Maximum chunk size in characters
     * @param overlap   Overlap between chunks
     * @return List of text chunks
     */
    List<TextChunk> chunkText(String text, int chunkSize, int overlap);

    /**
     * Split text into chunks using default settings
     *
     * @param text Text to chunk
     * @return List of text chunks
     */
    List<TextChunk> chunkText(String text);

    /**
     * Split text into semantic chunks (paragraph/section based)
     *
     * @param text Text to chunk
     * @return List of text chunks
     */
    List<TextChunk> chunkTextSemantic(String text);

    /**
     * Text chunk record
     */
    record TextChunk(
            int index,
            String content,
            int startPosition,
            int endPosition,
            int tokenCount) {
    }
}
