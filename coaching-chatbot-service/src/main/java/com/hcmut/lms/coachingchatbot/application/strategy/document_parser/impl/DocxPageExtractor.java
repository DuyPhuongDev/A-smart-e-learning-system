package com.hcmut.lms.coachingchatbot.application.strategy.document_parser.impl;

import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.DocumentPageExtractor;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.DocumentParsingException;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.PageContent;
import com.hcmut.lms.coachingchatbot.config.DocumentProcessingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * DOCX page extractor using Apache POI
 * Since DOCX doesn't have native page concept, we split by logical sections
 * (page breaks, sections, or by token count ~500 tokens per "page")
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocxPageExtractor implements DocumentPageExtractor {

    private final DocumentProcessingConfig documentProcessingConfig;

    private static final int MIN_TEXT_LENGTH = 50;

    @Override
    public String[] getSupportedFormats() {
        return new String[] { "docx", "doc" };
    }

    @Override
    public String getExtractorName() {
        return "DocxPageExtractor";
    }

    @Override
    public List<PageContent> extractPages(Path filePath) throws DocumentParsingException {
        log.info("[{}] Extracting pages from DOCX: {}", getExtractorName(), filePath);
        List<PageContent> pages;

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
                XWPFDocument document = new XWPFDocument(fis)) {

            // Extract all content first
            List<ContentBlock> contentBlocks = extractContentBlocks(document);

            // Split into logical pages based on page breaks or token count
            pages = splitIntoPages(contentBlocks);

            log.info("[{}] Successfully extracted {} logical pages from DOCX",
                    getExtractorName(), pages.size());
            return pages;

        } catch (IOException e) {
            log.error("[{}] Failed to extract pages from DOCX: {}", getExtractorName(), e.getMessage(), e);
            throw new DocumentParsingException(getExtractorName(), "extraction",
                    "Failed to extract pages from DOCX: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPageCount(Path filePath) throws DocumentParsingException {
        // For DOCX, we estimate page count based on content
        List<PageContent> pages = extractPages(filePath);
        return pages.size();
    }

    /**
     * Content block representing a paragraph or table with associated images
     */
    private static class ContentBlock {
        String text;
        List<String> images = new ArrayList<>();
        boolean hasPageBreak;
        int tokenCount;

        ContentBlock(String text, boolean hasPageBreak) {
            this.text = text != null ? text : "";
            this.hasPageBreak = hasPageBreak;
            this.tokenCount = estimateTokens(this.text);
        }

        private static int estimateTokens(String text) {
            if (text == null || text.isBlank())
                return 0;
            return (int) Math.ceil(text.length() / 4.0);
        }
    }

    /**
     * Extract content blocks from the document
     */
    private List<ContentBlock> extractContentBlocks(XWPFDocument document) {
        List<ContentBlock> blocks = new ArrayList<>();

        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph paragraph) {
                ContentBlock block = extractParagraphBlock(paragraph);
                if (!block.text.isEmpty() || !block.images.isEmpty() || block.hasPageBreak) {
                    blocks.add(block);
                }
            } else if (element instanceof XWPFTable table) {
                ContentBlock block = extractTableBlock(table);
                if (!block.text.isEmpty()) {
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    /**
     * Extract content from a paragraph
     */
    private ContentBlock extractParagraphBlock(XWPFParagraph paragraph) {
        StringBuilder text = new StringBuilder();
        boolean hasPageBreak = false;

        // Check for page break
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getCTR() != null && run.getCTR().getBrList() != null) {
                for (var br : run.getCTR().getBrList()) {
                    if (br.getType() != null &&
                            br.getType().toString().contains("page")) {
                        hasPageBreak = true;
                    }
                }
            }
        }

        text.append(paragraph.getText());

        ContentBlock block = new ContentBlock(cleanText(text.toString()), hasPageBreak);

        // Extract embedded images
        for (XWPFRun run : paragraph.getRuns()) {
            for (XWPFPicture picture : run.getEmbeddedPictures()) {
                try {
                    XWPFPictureData pictureData = picture.getPictureData();
                    if (pictureData != null) {
                        String base64 = Base64.getEncoder().encodeToString(pictureData.getData());
                        block.images.add(base64);
                    }
                } catch (Exception e) {
                    log.warn("[{}] Failed to extract image from paragraph: {}",
                            getExtractorName(), e.getMessage());
                }
            }
        }

        return block;
    }

    /**
     * Extract content from a table
     */
    private ContentBlock extractTableBlock(XWPFTable table) {
        StringBuilder text = new StringBuilder();

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                String cellText = cell.getText();
                if (cellText != null && !cellText.isBlank()) {
                    text.append(cellText).append(" | ");
                }
            }
            text.append("\n");
        }

        return new ContentBlock(cleanText(text.toString()), false);
    }

    /**
     * Split content blocks into logical pages
     */
    private List<PageContent> splitIntoPages(List<ContentBlock> blocks) {
        List<PageContent> pages = new ArrayList<>();
        int targetTokens = documentProcessingConfig.getTargetTokensPerSegment();
        StringBuilder currentPageText = new StringBuilder();
        List<String> currentPageImages = new ArrayList<>();
        int currentTokenCount = 0;
        int pageNumber = 1;

        for (ContentBlock block : blocks) {
            // Check if we should start a new page
            boolean shouldStartNewPage = block.hasPageBreak ||
                    (currentTokenCount > 0 && currentTokenCount + block.tokenCount > targetTokens);

            if (shouldStartNewPage && !currentPageText.isEmpty()) {
                // Save current page
                pages.add(buildPageContent(pageNumber++, currentPageText.toString(), currentPageImages));
                currentPageText = new StringBuilder();
                currentPageImages = new ArrayList<>();
                currentTokenCount = 0;
            }

            // Add block content to the current page
            if (!block.text.isBlank()) {
                if (!currentPageText.isEmpty()) {
                    currentPageText.append("\n\n");
                }
                currentPageText.append(block.text);
                currentTokenCount += block.tokenCount;
            }
            currentPageImages.addAll(block.images);
        }

        // Remember the last page
        if (!currentPageText.isEmpty() || !currentPageImages.isEmpty()) {
            pages.add(buildPageContent(pageNumber, currentPageText.toString(), currentPageImages));
        }

        // Ensure at least one page
        if (pages.isEmpty()) {
            pages.add(PageContent.builder()
                    .pageNumber(1)
                    .textContent("")
                    .imagesBase64(new ArrayList<>())
                    .textExtractionSuccessful(false)
                    .estimatedTokenCount(0)
                    .build());
        }

        return pages;
    }

    /**
     * Build PageContent from accumulated content
     */
    private PageContent buildPageContent(int pageNumber, String text, List<String> images) {
        boolean textExtractionSuccessful = text != null && text.length() >= MIN_TEXT_LENGTH;
        int tokenCount = text != null ? (int) Math.ceil(text.length() / 4.0) : 0;

        return PageContent.builder()
                .pageNumber(pageNumber)
                .textContent(text)
                .imagesBase64(images)
                .textExtractionSuccessful(textExtractionSuccessful)
                .estimatedTokenCount(tokenCount)
                .build();
    }

    /**
     * Clean extracted text
     */
    private String cleanText(String text) {
        if (text == null)
            return "";
        text = text.replaceAll("\\s+", " ");
        text = text.replaceAll("[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        return text.trim();
    }
}
