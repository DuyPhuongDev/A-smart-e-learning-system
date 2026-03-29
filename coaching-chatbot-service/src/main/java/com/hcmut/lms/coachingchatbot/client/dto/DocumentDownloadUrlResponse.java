package com.hcmut.lms.coachingchatbot.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for receiving document download URL from Course Management Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
