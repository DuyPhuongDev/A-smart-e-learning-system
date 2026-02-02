package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.service.TextEnhancementService;
import com.hcmut.lms.coachingchatbot.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TextEnhancementService using Gemini LLM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TextEnhancementServiceImpl implements TextEnhancementService {

    private final GeminiClient geminiClient;

    private static final String ENHANCE_PROMPT = """
            You are an expert editor helping to clean up and enhance transcribed or extracted text from educational content.

            Your task is to:
            1. Fix any transcription or OCR errors
            2. Add proper punctuation and capitalization
            3. Fix grammar issues while preserving the original meaning
            4. Break long run-on sentences into readable chunks
            5. Preserve technical terms and proper nouns accurately
            6. Keep the content educational and professional

            Important:
            - Do NOT add new information
            - Do NOT change the meaning
            - Do NOT summarize the content
            - Keep ALL the original information
            - Output ONLY the enhanced text, no explanations

            Original text:
            %s

            Enhanced text:
            """;

    private static final String ENHANCE_WITH_CONTEXT_PROMPT = """
            You are an expert editor helping to clean up and enhance transcribed or extracted text from educational content.

            Context: This text is from a lecture titled "%s"

            Your task is to:
            1. Fix any transcription or OCR errors
            2. Add proper punctuation and capitalization
            3. Fix grammar issues while preserving the original meaning
            4. Break long run-on sentences into readable chunks
            5. Preserve technical terms and proper nouns accurately
            6. Keep the content educational and professional

            Important:
            - Do NOT add new information
            - Do NOT change the meaning
            - Do NOT summarize the content
            - Keep ALL the original information
            - Output ONLY the enhanced text, no explanations

            Original text:
            %s
            Enhanced text:
            """;

    private static final String SUMMARY_PROMPT = """
            Summarize the following educational content in a concise manner.
            Maximum length: %d characters.
            Focus on the key learning points and main concepts.

            Content:
            %s

            Summary:
            """;

    private static final String KEY_CONCEPTS_PROMPT = """
            Extract the key concepts and important terms from the following educational content.
            Return only a comma-separated list of key concepts (maximum 10).

            Content:
            %s

            Key concepts:
            """;

    @Override
    public String enhanceText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return rawText;
        }

        log.info("Enhancing text, length: {} characters", rawText.length());

        try {
            // For very long texts, process in chunks
            if (rawText.length() > 10000) {
                return enhanceTextInChunks(rawText);
            }

            String prompt = String.format(ENHANCE_PROMPT, rawText);
            String enhanced = geminiClient.chat(prompt);

            log.info("Text enhanced successfully, new length: {} characters", enhanced.length());
            return enhanced.trim();

        } catch (Exception e) {
            log.error("Failed to enhance text: {}", e.getMessage(), e);
            // Return original text if enhancement fails
            return rawText;
        }
    }

    @Override
    public String enhanceText(String rawText, String context) {
        if (rawText == null || rawText.isBlank()) {
            return rawText;
        }

        log.info("Enhancing text with context: {}, length: {} characters", context, rawText.length());

        try {
            if (rawText.length() > 10000) {
                return enhanceTextInChunks(rawText);
            }

            String prompt = String.format(ENHANCE_WITH_CONTEXT_PROMPT, context, rawText);
            String enhanced = geminiClient.chat(prompt);

            log.info("Text enhanced with context successfully");
            return enhanced.trim();

        } catch (Exception e) {
            log.error("Failed to enhance text with context: {}", e.getMessage(), e);
            return rawText;
        }
    }

    @Override
    public String generateSummary(String text, int maxLength) {
        if (text == null || text.isBlank()) {
            return "";
        }

        log.info("Generating summary, max length: {} characters", maxLength);

        try {
            String prompt = String.format(SUMMARY_PROMPT, maxLength, text);
            String summary = geminiClient.chat(prompt);

            // Ensure summary doesn't exceed max length
            if (summary.length() > maxLength) {
                summary = summary.substring(0, maxLength - 3) + "...";
            }

            log.info("Summary generated, length: {} characters", summary.length());
            return summary.trim();

        } catch (Exception e) {
            log.error("Failed to generate summary: {}", e.getMessage(), e);
            return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
        }
    }

    @Override
    public List<String> extractKeyConcepts(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        log.info("Extracting key concepts from text");

        try {
            String prompt = String.format(KEY_CONCEPTS_PROMPT, text);
            String response = geminiClient.chat(prompt);

            // Parse comma-separated concepts
            List<String> concepts = Arrays.stream(response.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .limit(10)
                    .collect(Collectors.toList());

            log.info("Extracted {} key concepts", concepts.size());
            return concepts;

        } catch (Exception e) {
            log.error("Failed to extract key concepts: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Enhance long text by processing in chunks
     */
    private String enhanceTextInChunks(String text) {
        log.info("Processing long text in chunks");

        int chunkSize = 8000;
        StringBuilder result = new StringBuilder();

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());

            // Try to end at a sentence boundary
            if (end < text.length()) {
                int lastPeriod = text.lastIndexOf(". ", end);
                if (lastPeriod > start + chunkSize / 2) {
                    end = lastPeriod + 2;
                }
            }

            String chunk = text.substring(start, end);
            String prompt = String.format(ENHANCE_PROMPT, chunk);

            try {
                String enhancedChunk = geminiClient.chat(prompt);
                result.append(enhancedChunk.trim());
                if (end < text.length()) {
                    result.append(" ");
                }
            } catch (Exception e) {
                log.warn("Failed to enhance chunk, using original: {}", e.getMessage());
                result.append(chunk);
            }

            start = end;
        }

        return result.toString().trim();
    }
}
