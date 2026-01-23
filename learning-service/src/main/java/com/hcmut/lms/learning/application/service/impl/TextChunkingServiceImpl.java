package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.TextChunkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of TextChunkingService
 * Splits text into manageable chunks for embedding
 */
@Service
@Slf4j
public class TextChunkingServiceImpl implements TextChunkingService {

    @Value("${chunking.default-size:1000}")
    private int defaultChunkSize;

    @Value("${chunking.default-overlap:200}")
    private int defaultOverlap;

    // Average characters per token (approximate)
    private static final double CHARS_PER_TOKEN = 4.0;

    @Override
    public List<TextChunk> chunkText(String text, int chunkSize, int overlap) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        log.info("Chunking text of {} characters with chunkSize={}, overlap={}",
                text.length(), chunkSize, overlap);

        List<TextChunk> chunks = new ArrayList<>();
        int start = 0;
        int index = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());

            // Try to end at a sentence boundary
            if (end < text.length()) {
                int boundaryEnd = findSentenceBoundary(text, start, end);
                if (boundaryEnd > start) {
                    end = boundaryEnd;
                }
            }

            String chunkContent = text.substring(start, end).trim();

            if (!chunkContent.isBlank()) {
                int tokenCount = estimateTokenCount(chunkContent);

                chunks.add(new TextChunk(
                        index++,
                        chunkContent,
                        start,
                        end,
                        tokenCount
                ));
            }

            // Move start position with overlap
            start = end - overlap;
            if (start >= text.length()) break;

            // Ensure we make progress
            if (end == text.length()) break;
        }

        log.info("Created {} chunks from text", chunks.size());
        return chunks;
    }

    @Override
    public List<TextChunk> chunkText(String text) {
        return chunkText(text, defaultChunkSize, defaultOverlap);
    }

    @Override
    public List<TextChunk> chunkTextSemantic(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        log.info("Creating semantic chunks from text of {} characters", text.length());

        List<TextChunk> chunks = new ArrayList<>();

        // Split by paragraphs first
        String[] paragraphs = text.split("\\n\\n+");

        StringBuilder currentChunk = new StringBuilder();
        int currentStart = 0;
        int index = 0;
        int position = 0;

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isBlank()) {
                position += 2; // for \n\n
                continue;
            }

            // Check if adding this paragraph would exceed chunk size
            if (currentChunk.length() + paragraph.length() + 2 > defaultChunkSize && !currentChunk.isEmpty()) {
                // Save current chunk
                String chunkContent = currentChunk.toString().trim();
                if (!chunkContent.isBlank()) {
                    chunks.add(new TextChunk(
                            index++,
                            chunkContent,
                            currentStart,
                            position,
                            estimateTokenCount(chunkContent)
                    ));
                }

                // Start new chunk
                currentChunk = new StringBuilder();
                currentStart = position;
            }

            if (!currentChunk.isEmpty()) {
                currentChunk.append("\n\n");
            }
            currentChunk.append(paragraph);
            position += paragraph.length() + 2;
        }

        // Don't forget the last chunk
        if (!currentChunk.isEmpty()) {
            String chunkContent = currentChunk.toString().trim();
            if (!chunkContent.isBlank()) {
                chunks.add(new TextChunk(
                        index,
                        chunkContent,
                        currentStart,
                        text.length(),
                        estimateTokenCount(chunkContent)
                ));
            }
        }

        // If we ended up with very few chunks, try regular chunking
        if (chunks.size() <= 1 && text.length() > defaultChunkSize) {
            log.info("Semantic chunking produced few results, falling back to regular chunking");
            return chunkText(text);
        }

        log.info("Created {} semantic chunks from text", chunks.size());
        return chunks;
    }

    /**
     * Find the best sentence boundary within the range
     */
    private int findSentenceBoundary(String text, int start, int maxEnd) {
        // Look for sentence endings: . ! ?
        Pattern sentenceEnd = Pattern.compile("[.!?]\\s+");
        Matcher matcher = sentenceEnd.matcher(text);

        int lastBoundary = -1;
        int searchStart = start + (maxEnd - start) / 2; // Start searching from middle of chunk

        while (matcher.find(searchStart)) {
            int boundaryPos = matcher.end();
            if (boundaryPos <= maxEnd) {
                lastBoundary = boundaryPos;
            } else {
                break;
            }
        }

        // If no good boundary found in second half, search from start
        if (lastBoundary == -1) {
            matcher.reset();
            while (matcher.find(start)) {
                int boundaryPos = matcher.end();
                if (boundaryPos <= maxEnd) {
                    lastBoundary = boundaryPos;
                } else {
                    break;
                }
            }
        }

        return lastBoundary > 0 ? lastBoundary : maxEnd;
    }

    /**
     * Estimate token count from character count
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank()) return 0;
        return (int) Math.ceil(text.length() / CHARS_PER_TOKEN);
    }
}
