package com.hcmut.lms.learning.application.strategy.document_parser.impl;

import com.hcmut.lms.learning.application.strategy.document_parser.DocumentPageExtractor;
import com.hcmut.lms.learning.application.strategy.document_parser.DocumentParsingException;
import com.hcmut.lms.learning.application.strategy.document_parser.PageContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.cos.COSName;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * PDF page extractor using Apache PDFBox 3.x
 * Extracts text and images from each page of a PDF document
 */
@Component
@Slf4j
public class PdfPageExtractor implements DocumentPageExtractor {

    private static final int MIN_TEXT_LENGTH = 50; // Minimum text length to consider extraction successful
    private static final float IMAGE_DPI = 150f; // DPI for rendering pages as images

    @Override
    public String[] getSupportedFormats() {
        return new String[]{"pdf"};
    }

    @Override
    public String getExtractorName() {
        return "PdfPageExtractor";
    }

    @Override
    public List<PageContent> extractPages(Path filePath) throws DocumentParsingException {
        log.info("[{}] Extracting pages from PDF: {}", getExtractorName(), filePath);
        List<PageContent> pages = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            int totalPages = document.getNumberOfPages();
            log.info("[{}] PDF has {} pages", getExtractorName(), totalPages);

            PDFTextStripper textStripper = new PDFTextStripper();
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                PageContent pageContent = extractSinglePage(document, textStripper, pdfRenderer, pageNum);
                pages.add(pageContent);
                log.debug("[{}] Extracted page {}/{}: {} chars, textSuccess={}",
                        getExtractorName(), pageNum, totalPages,
                        pageContent.getTextContent() != null ? pageContent.getTextContent().length() : 0,
                        pageContent.isTextExtractionSuccessful());
            }

            log.info("[{}] Successfully extracted {} pages from PDF", getExtractorName(), pages.size());
            return pages;

        } catch (IOException e) {
            log.error("[{}] Failed to extract pages from PDF: {}", getExtractorName(), e.getMessage(), e);
            throw new DocumentParsingException(getExtractorName(), "extraction",
                    "Failed to extract pages from PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPageCount(Path filePath) throws DocumentParsingException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new DocumentParsingException(getExtractorName(), "page-count",
                    "Failed to get page count: " + e.getMessage(), e);
        }
    }

    /**
     * Extract content from a single page
     */
    private PageContent extractSinglePage(PDDocument document, PDFTextStripper textStripper,
                                          PDFRenderer pdfRenderer, int pageNum) throws IOException {
        // Extract text from the page
        textStripper.setStartPage(pageNum);
        textStripper.setEndPage(pageNum);
        String text = textStripper.getText(document);
        text = cleanText(text);

        boolean textExtractionSuccessful = text != null && text.length() >= MIN_TEXT_LENGTH;
        List<String> imagesBase64 = new ArrayList<>();

        // If text extraction failed or text is too short, try to extract images
        if (!textExtractionSuccessful) {
            log.debug("[{}] Text extraction insufficient for page {}, extracting images",
                    getExtractorName(), pageNum);

            // First try to extract embedded images
            imagesBase64 = extractEmbeddedImages(document, pageNum);

            // If no embedded images, render the whole page as an image
            if (imagesBase64.isEmpty()) {
                String pageImage = renderPageAsImage(pdfRenderer, pageNum - 1); // 0-based index for renderer
                if (pageImage != null) {
                    imagesBase64.add(pageImage);
                }
            }
        }

        int tokenCount = estimateTokenCount(text);

        return PageContent.builder()
                .pageNumber(pageNum)
                .textContent(text)
                .imagesBase64(imagesBase64)
                .textExtractionSuccessful(textExtractionSuccessful)
                .estimatedTokenCount(tokenCount)
                .build();
    }

    /**
     * Extract embedded images from a page
     */
    private List<String> extractEmbeddedImages(PDDocument document, int pageNum) {
        List<String> images = new ArrayList<>();
        try {
            PDPage page = document.getPage(pageNum - 1); // 0-based index
            PDResources resources = page.getResources();

            if (resources == null) return images;

            for (COSName name : resources.getXObjectNames()) {
                try {
                    if (resources.isImageXObject(name)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(name);
                        BufferedImage bufferedImage = image.getImage();

                        if (bufferedImage != null && bufferedImage.getWidth() > 100 && bufferedImage.getHeight() > 100) {
                            String base64 = bufferedImageToBase64(bufferedImage);
                            if (base64 != null) {
                                images.add(base64);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("[{}] Failed to extract image {} from page {}: {}",
                            getExtractorName(), name, pageNum, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("[{}] Failed to extract embedded images from page {}: {}",
                    getExtractorName(), pageNum, e.getMessage());
        }
        return images;
    }

    /**
     * Render the entire page as an image (fallback when text extraction fails)
     */
    private String renderPageAsImage(PDFRenderer pdfRenderer, int pageIndex) {
        try {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, IMAGE_DPI);
            return bufferedImageToBase64(image);
        } catch (Exception e) {
            log.warn("[{}] Failed to render page {} as image: {}",
                    getExtractorName(), pageIndex + 1, e.getMessage());
            return null;
        }
    }

    /**
     * Convert BufferedImage to base64 string
     */
    private String bufferedImageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.warn("[{}] Failed to convert image to base64: {}", getExtractorName(), e.getMessage());
            return null;
        }
    }

    /**
     * Clean extracted text
     */
    private String cleanText(String text) {
        if (text == null) return "";

        // Remove excessive whitespace
        text = text.replaceAll("\\s+", " ");

        // Remove control characters
        text = text.replaceAll("[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");

        return text.trim();
    }

    /**
     * Estimate token count (approximately 4 characters per token)
     */
    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank()) return 0;
        return (int) Math.ceil(text.length() / 4.0);
    }
}
