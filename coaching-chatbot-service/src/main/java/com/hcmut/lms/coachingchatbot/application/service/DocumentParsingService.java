package com.hcmut.lms.coachingchatbot.application.service;

/**
 * Service for parsing documents (PDF, DOCX, PPTX) using Apache Tika
 */
public interface DocumentParsingService {

    /**
     * Parse document from URL and extract text content
     *
     * @param documentUrl URL of the document
     * @return Extracted text content
     */
    String parseDocument(String documentUrl);

    /**
     * Parse document from a byte array
     *
     * @param content  Document content as bytes
     * @param filename Filename for format detection
     * @return Extracted text content
     */
    String parseDocument(byte[] content, String filename);

    /**
     * Get document metadata
     *
     * @param documentUrl URL of the document
     * @return Document metadata
     */
    DocumentMetadata getMetadata(String documentUrl);

    /**
     * Document metadata record
     */
    record DocumentMetadata(
            String title,
            String author,
            String contentType,
            int pageCount,
            java.time.Instant creationDate,
            java.time.Instant modificationDate,
            java.util.Map<String, String> additionalMetadata) {
    }
}
