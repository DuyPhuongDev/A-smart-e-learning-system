package com.hcmut.lms.coachingchatbot.application.service.impl;

import com.hcmut.lms.coachingchatbot.application.service.DocumentParsingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of DocumentParsingService using Apache Tika
 */
@Service
@Slf4j
public class DocumentParsingServiceImpl implements DocumentParsingService {

    private final Parser parser;
    private final HttpClient httpClient;

    public DocumentParsingServiceImpl() {
        this.parser = new AutoDetectParser();
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    @Override
    public String parseDocument(String documentUrl) {
        log.info("Parsing document from URL: {}", documentUrl);

        try {
            // Download document
            byte[] content = downloadDocument(documentUrl);

            // Extract filename from URL
            String filename = extractFilename(documentUrl);

            // Parse document
            return parseDocument(content, filename);

        } catch (Exception e) {
            log.error("Failed to parse document from URL: {}", e.getMessage(), e);
            throw new RuntimeException("Document parsing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String parseDocument(byte[] content, String filename) {
        log.info("Parsing document: {}, size: {} bytes", filename, content.length);

        try {
            // Use Tika to detect content type and parse
            BodyContentHandler handler = new BodyContentHandler(-1); // -1 for unlimited content
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filename);

            ParseContext context = new ParseContext();
            context.set(Parser.class, parser);

            try (InputStream stream = new ByteArrayInputStream(content)) {
                parser.parse(stream, handler, metadata, context);
            }

            String extractedText = handler.toString();
            log.info("Document parsed successfully, extracted {} characters", extractedText.length());

            // Cleanup extracted text
            return cleanExtractedText(extractedText);

        } catch (Exception e) {
            log.error("Failed to parse document content: {}", e.getMessage(), e);
            throw new RuntimeException("Document content parsing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public DocumentMetadata getMetadata(String documentUrl) {
        log.info("Extracting metadata from document: {}", documentUrl);

        try {
            byte[] content = downloadDocument(documentUrl);
            String filename = extractFilename(documentUrl);

            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filename);

            BodyContentHandler handler = new BodyContentHandler(0); // 0 = don't extract content, just metadata
            ParseContext context = new ParseContext();

            try (InputStream stream = new ByteArrayInputStream(content)) {
                parser.parse(stream, handler, metadata, context);
            }

            // Extract metadata fields
            Map<String, String> additionalMetadata = new HashMap<>();
            for (String name : metadata.names()) {
                additionalMetadata.put(name, metadata.get(name));
            }

            // Parse page count if available
            int pageCount = 0;
            String pageCountStr = metadata.get("xmpTPg:NPages");
            if (pageCountStr != null) {
                try {
                    pageCount = Integer.parseInt(pageCountStr);
                } catch (NumberFormatException ignored) {
                }
            }

            // Parse dates
            Instant creationDate = parseDate(metadata.get("dcterms:created"));
            Instant modificationDate = parseDate(metadata.get("dcterms:modified"));

            return new DocumentMetadata(
                    metadata.get("dc:title"),
                    metadata.get("dc:creator"),
                    metadata.get(Metadata.CONTENT_TYPE),
                    pageCount,
                    creationDate,
                    modificationDate,
                    additionalMetadata);

        } catch (Exception e) {
            log.error("Failed to extract metadata: {}", e.getMessage(), e);
            throw new RuntimeException("Metadata extraction failed: " + e.getMessage(), e);
        }
    }

    /**
     * Download document from URL
     */
    private byte[] downloadDocument(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download document, status: " + response.statusCode());
        }

        return response.body();
    }

    /**
     * Extract filename from URL
     */
    private String extractFilename(String url) {
        if (url == null)
            return "document";

        int lastSlash = url.lastIndexOf('/');
        int queryStart = url.indexOf('?');

        if (lastSlash >= 0) {
            String filename = queryStart > lastSlash
                    ? url.substring(lastSlash + 1, queryStart)
                    : url.substring(lastSlash + 1);
            return filename.isBlank() ? "document" : filename;
        }

        return "document";
    }

    /**
     * Clean up extracted text
     */
    private String cleanExtractedText(String text) {
        if (text == null)
            return "";

        // Remove excessive whitespace
        text = text.replaceAll("\\s+", " ");

        // Remove control characters except newlines
        text = text.replaceAll("[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");

        // Normalize line endings
        text = text.replaceAll("\\r\\n|\\r", "\n");

        // Remove excessive blank lines
        text = text.replaceAll("\\n{3,}", "\n\n");

        return text.trim();
    }

    /**
     * Parse date string to Instant
     */
    private Instant parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
