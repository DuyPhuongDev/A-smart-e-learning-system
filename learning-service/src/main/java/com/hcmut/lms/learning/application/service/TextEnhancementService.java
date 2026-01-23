package com.hcmut.lms.learning.application.service;

/**
 * Service for enhancing text content using LLM (Gemini)
 * Cleans up transcripts, improves readability, fixes errors
 */
public interface TextEnhancementService {

    /**
     * Enhance raw text content using LLM
     * - Fix transcription errors
     * - Add proper punctuation
     * - Improve readability
     *
     * @param rawText Raw text content
     * @return Enhanced text content
     */
    String enhanceText(String rawText);

    /**
     * Enhance text with specific context
     *
     * @param rawText Raw text content
     * @param context Additional context (e.g., lecture title, subject)
     * @return Enhanced text content
     */
    String enhanceText(String rawText, String context);

    /**
     * Generate summary of the content
     *
     * @param text Text content to summarize
     * @param maxLength Maximum summary length
     * @return Summary text
     */
    String generateSummary(String text, int maxLength);

    /**
     * Extract key concepts from text
     *
     * @param text Text content
     * @return List of key concepts
     */
    java.util.List<String> extractKeyConcepts(String text);
}
