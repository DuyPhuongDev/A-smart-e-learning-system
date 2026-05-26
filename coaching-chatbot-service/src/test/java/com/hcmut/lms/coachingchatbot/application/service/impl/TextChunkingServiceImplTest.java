package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.hcmut.lms.coachingchatbot.application.service.TextChunkingService.TextChunk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TextChunkingServiceImplTest {

    @InjectMocks
    private TextChunkingServiceImpl textChunkingService;

    // === chunkText with custom size/overlap ===

    @Test
    void chunkText_shouldReturnChunks_whenValidText() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 1000);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 200);

        String text = "Hello world this is a test sentence another sentence here without punctuation.";
        List<TextChunk> chunks = textChunkingService.chunkText(text, 20, 5);

        assertFalse(chunks.isEmpty());
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = chunks.get(i);
            assertEquals(i, chunk.index());
            assertNotNull(chunk.content());
            assertFalse(chunk.content().isBlank());
            assertTrue(chunk.tokenCount() > 0);
        }
    }

    @Test
    void chunkText_shouldReturnSingleChunk_whenTextShorterThanChunkSize() {
        String text = "Short text.";
        List<TextChunk> chunks = textChunkingService.chunkText(text, 100, 10);

        assertEquals(1, chunks.size());
        assertEquals(0, chunks.get(0).index());
        assertEquals("Short text.", chunks.get(0).content());
    }

    @Test
    void chunkText_shouldReturnEmptyList_whenTextIsNull() {
        List<TextChunk> chunks = textChunkingService.chunkText(null, 100, 10);
        assertTrue(chunks.isEmpty());
    }

    @Test
    void chunkText_shouldReturnEmptyList_whenTextIsBlank() {
        List<TextChunk> chunks = textChunkingService.chunkText("   ", 100, 10);
        assertTrue(chunks.isEmpty());
    }

    @Test
    void chunkText_shouldReturnEmptyList_whenTextIsEmpty() {
        List<TextChunk> chunks = textChunkingService.chunkText("", 100, 10);
        assertTrue(chunks.isEmpty());
    }

    @Test
    void chunkText_shouldUseDefaultChunkSize_whenNotSpecified() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 100);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 20);

        String text = "Short.";
        List<TextChunk> chunks = textChunkingService.chunkText(text);

        assertEquals(1, chunks.size());
        assertEquals("Short.", chunks.get(0).content());
    }

    @Test
    void chunkText_shouldNotExceedTextLength() {
        String text = "Hello world, this is a test.";
        List<TextChunk> chunks = textChunkingService.chunkText(text, 10, 2);

        for (TextChunk chunk : chunks) {
            assertTrue(chunk.endPosition() <= text.length());
            assertTrue(chunk.startPosition() < chunk.endPosition());
        }
    }

    @Test
    void chunkText_shouldHandleTextEqualToChunkSize() {
        String text = "1234567890"; // 10 chars = chunkSize
        List<TextChunk> chunks = textChunkingService.chunkText(text, 10, 2);

        assertEquals(1, chunks.size());
        assertEquals(text, chunks.get(0).content());
    }

    // === chunkTextSemantic ===

    @Test
    void chunkTextSemantic_shouldReturnChunks_whenValidText() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 500);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 200);

        String paragraph1 = "First paragraph with some content. It has multiple sentences to make it longer.";
        String paragraph2 = "Second paragraph also has content. More sentences here for testing.";
        String text = paragraph1 + "\n\n" + paragraph2;

        List<TextChunk> chunks = textChunkingService.chunkTextSemantic(text);
        assertFalse(chunks.isEmpty());
    }

    @Test
    void chunkTextSemantic_shouldReturnEmptyList_whenTextIsBlank() {
        List<TextChunk> chunks = textChunkingService.chunkTextSemantic("   ");
        assertTrue(chunks.isEmpty());
    }

    @Test
    void chunkTextSemantic_shouldReturnEmptyList_whenTextIsNull() {
        List<TextChunk> chunks = textChunkingService.chunkTextSemantic(null);
        assertTrue(chunks.isEmpty());
    }

    @Test
    void chunkTextSemantic_shouldReturnChunks_whenSingleParagraph() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 500);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 200);

        String text = "A single paragraph with content that is long enough.";
        List<TextChunk> chunks = textChunkingService.chunkTextSemantic(text);

        assertFalse(chunks.isEmpty());
        assertEquals(1, chunks.size());
    }

    @Test
    void chunkTextSemantic_shouldFallbackToRegularChunking_whenTextLongAndFewChunks() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 20);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 5);

        // Single long paragraph without \n\n → produces 1 semantic chunk, falls back to regular
        String text = "A ".repeat(100);
        List<TextChunk> chunks = textChunkingService.chunkTextSemantic(text);

        // Should produce multiple chunks due to fallback
        assertTrue(chunks.size() > 1);
    }

    @Test
    void chunkTextSemantic_shouldSplitWhenCombinedExceedsChunkSize() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 20);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 5);

        String text = "AAAA BBBB CCCC DDDD EEEE\n\nFFFF GGGG HHHH IIII JJJJ";
        List<TextChunk> chunks = textChunkingService.chunkTextSemantic(text);

        assertFalse(chunks.isEmpty());
        assertTrue(chunks.size() >= 2);
    }

    @Test
    void chunkTextSemantic_shouldSkipLeadingBlankParagraph() {
        ReflectionTestUtils.setField(textChunkingService, "defaultChunkSize", 500);
        ReflectionTestUtils.setField(textChunkingService, "defaultOverlap", 200);

        String text = "\n\nFirst paragraph.\n\nSecond paragraph.";
        List<TextChunk> chunks = textChunkingService.chunkTextSemantic(text);

        assertFalse(chunks.isEmpty());
    }

    @Test
    void chunkText_shouldHandleTextWithNoPunctuation() {
        // Text with no sentence boundaries — findSentenceBoundary returns maxEnd
        String text = "AAAAAAAAAA";
        List<TextChunk> chunks = textChunkingService.chunkText(text, 5, 0);

        assertEquals(2, chunks.size());
        assertEquals("AAAAA", chunks.get(0).content());
        assertEquals("AAAAA", chunks.get(1).content());
    }

    @Test
    void chunkText_shouldHandleOverlapExceedingStart() {
        // Text shorter than overlap after first chunk
        String text = "AAAAABBBBB";
        List<TextChunk> chunks = textChunkingService.chunkText(text, 6, 4);

        assertFalse(chunks.isEmpty());
    }

    @Test
    void chunkText_shouldCoverEstimateTokenCountNullPath() throws Exception {
        java.lang.reflect.Method method = TextChunkingServiceImpl.class
                .getDeclaredMethod("estimateTokenCount", String.class);
        method.setAccessible(true);

        assertEquals(0, (int) method.invoke(textChunkingService, (String) null));
        assertEquals(0, (int) method.invoke(textChunkingService, ""));
    }

}
