package com.hcmut.lms.coachingchatbot.application.strategy.document_parser.impl;

import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.DocumentPageExtractor;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.DocumentParsingException;
import com.hcmut.lms.coachingchatbot.application.strategy.document_parser.PageContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * PPTX slide extractor using Apache POI
 * Each slide is treated as a page
 * For slides with minimal text, renders the slide as an image for Vision API
 */
@Component
@Slf4j
public class PptxPageExtractor implements DocumentPageExtractor {

    private static final int MIN_TEXT_LENGTH = 30; // Slides typically have less text
    private static final int SLIDE_WIDTH = 1280;
    private static final int SLIDE_HEIGHT = 720;

    @Override
    public String[] getSupportedFormats() {
        return new String[] { "pptx", "ppt" };
    }

    @Override
    public String getExtractorName() {
        return "PptxPageExtractor";
    }

    @Override
    public List<PageContent> extractPages(Path filePath) throws DocumentParsingException {
        log.info("[{}] Extracting slides from PPTX: {}", getExtractorName(), filePath);
        List<PageContent> pages = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
                XMLSlideShow ppt = new XMLSlideShow(fis)) {

            List<XSLFSlide> slides = ppt.getSlides();
            log.info("[{}] PPTX has {} slides", getExtractorName(), slides.size());

            Dimension pageSize = ppt.getPageSize();
            double scaleX = (double) SLIDE_WIDTH / pageSize.width;
            double scaleY = (double) SLIDE_HEIGHT / pageSize.height;

            for (int i = 0; i < slides.size(); i++) {
                XSLFSlide slide = slides.get(i);
                PageContent pageContent = extractSlide(slide, i + 1, scaleX, scaleY);
                pages.add(pageContent);
                log.debug("[{}] Extracted slide {}/{}: {} chars, textSuccess={}",
                        getExtractorName(), i + 1, slides.size(),
                        pageContent.getTextContent() != null ? pageContent.getTextContent().length() : 0,
                        pageContent.isTextExtractionSuccessful());
            }

            log.info("[{}] Successfully extracted {} slides from PPTX", getExtractorName(), pages.size());
            return pages;

        } catch (IOException e) {
            log.error("[{}] Failed to extract slides from PPTX: {}", getExtractorName(), e.getMessage(), e);
            throw new DocumentParsingException(getExtractorName(), "extraction",
                    "Failed to extract slides from PPTX: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPageCount(Path filePath) throws DocumentParsingException {
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
                XMLSlideShow ppt = new XMLSlideShow(fis)) {
            return ppt.getSlides().size();
        } catch (IOException e) {
            throw new DocumentParsingException(getExtractorName(), "page-count",
                    "Failed to get slide count: " + e.getMessage(), e);
        }
    }

    /**
     * Extract content from a single slide
     */
    private PageContent extractSlide(XSLFSlide slide, int slideNumber,
            double scaleX, double scaleY) {
        // Extract text from slide
        StringBuilder textBuilder = new StringBuilder();
        extractTextFromShapes(slide.getShapes(), textBuilder);
        String text = cleanText(textBuilder.toString());

        boolean textExtractionSuccessful = text.length() >= MIN_TEXT_LENGTH;
        List<String> imagesBase64 = new ArrayList<>();

        // For slides, we almost always want to capture the visual representation
        // because layout and diagrams are important
        if (!textExtractionSuccessful || shouldCaptureSlideImage(slide)) {
            String slideImage = renderSlideAsImage(slide, scaleX, scaleY);
            if (slideImage != null) {
                imagesBase64.add(slideImage);
            }
        }

        int tokenCount = estimateTokenCount(text);

        return PageContent.builder()
                .pageNumber(slideNumber)
                .textContent(text)
                .imagesBase64(imagesBase64)
                .textExtractionSuccessful(textExtractionSuccessful)
                .estimatedTokenCount(tokenCount)
                .build();
    }

    /**
     * Recursively extract text from shapes
     */
    private void extractTextFromShapes(List<XSLFShape> shapes, StringBuilder textBuilder) {
        for (XSLFShape shape : shapes) {
            if (shape instanceof XSLFTextShape textShape) {
                String text = textShape.getText();
                if (text != null && !text.isBlank()) {
                    if (textBuilder.length() > 0) {
                        textBuilder.append("\n");
                    }
                    textBuilder.append(text);
                }
            } else if (shape instanceof XSLFGroupShape groupShape) {
                // Recursively handle grouped shapes
                extractTextFromShapes(groupShape.getShapes(), textBuilder);
            } else if (shape instanceof XSLFTable table) {
                // Extract text from tables
                for (XSLFTableRow row : table.getRows()) {
                    for (XSLFTableCell cell : row.getCells()) {
                        String cellText = cell.getText();
                        if (cellText != null && !cellText.isBlank()) {
                            if (textBuilder.length() > 0) {
                                textBuilder.append(" | ");
                            }
                            textBuilder.append(cellText);
                        }
                    }
                    textBuilder.append("\n");
                }
            }
        }
    }

    /**
     * Check if we should capture the slide as an image
     * (when there are diagrams, charts, or complex visuals)
     */
    private boolean shouldCaptureSlideImage(XSLFSlide slide) {
        for (XSLFShape shape : slide.getShapes()) {
            // Check for pictures, charts, diagrams
            if (shape instanceof XSLFPictureShape ||
                    shape instanceof XSLFGraphicFrame ||
                    shape instanceof XSLFGroupShape) {
                return true;
            }
        }
        return false;
    }

    /**
     * Render slide as an image
     */
    private String renderSlideAsImage(XSLFSlide slide, double scaleX, double scaleY) {
        try {
            BufferedImage image = new BufferedImage(SLIDE_WIDTH, SLIDE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();

            // Set white background
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, SLIDE_WIDTH, SLIDE_HEIGHT);

            // Set rendering hints for quality
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Scale graphics
            graphics.scale(scaleX, scaleY);

            // Draw slide
            slide.draw(graphics);
            graphics.dispose();

            // Convert to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            log.warn("[{}] Failed to render slide as image: {}", getExtractorName(), e.getMessage());
            return null;
        }
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

    /**
     * Estimate token count
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank())
            return 0;
        return (int) Math.ceil(text.length() / 4.0);
    }
}
