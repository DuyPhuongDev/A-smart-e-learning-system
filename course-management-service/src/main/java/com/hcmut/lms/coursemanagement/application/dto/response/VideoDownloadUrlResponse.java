package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for video download URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDownloadUrlResponse {

    private UUID lectureId;

    /**
     * The download URL
     * For S3: pre-signed URL with expiration
     * For YouTube: original YouTube URL
     */
    private String downloadUrl;

    /**
     * Original video URL stored in database
     */
    private String originalUrl;

    /**
     * Source type: "s3" or "youtube"
     */
    private String sourceType;

    /**
     * Whether the URL requires authentication/signing
     */
    private boolean requiresPresigning;

    /**
     * URL expiration time in seconds (only for S3 pre-signed URLs)
     */
    private Integer expirationSeconds;
}
