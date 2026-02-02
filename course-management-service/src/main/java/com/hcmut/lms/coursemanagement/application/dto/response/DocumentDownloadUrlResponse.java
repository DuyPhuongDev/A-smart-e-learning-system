package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for document download URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDownloadUrlResponse {

    private UUID lectureId;

    /**
     * The download URL
     * For S3: pre-signed URL with expiration
     */
    private String downloadUrl;

    /**
     * Original file URL stored in database
     */
    private String originalUrl;

    /**
     * Source type: "s3" or "direct"
     */
    private String sourceType;

    /**
     * File format: "pdf", "docx", "pptx", etc.
     */
    private String fileFormat;

    /**
     * Number of pages in the document
     */
    private Integer numPages;

    /**
     * Whether the URL requires authentication/signing
     */
    private boolean requiresPresigning;

    /**
     * URL expiration time in seconds (only for S3 pre-signed URLs)
     */
    private Integer expirationSeconds;
}
