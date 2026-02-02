package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.DocumentDownloadUrlResponse;

import java.util.UUID;

/**
 * Service for document lecture operations
 * Provides download URL generation for S3-stored documents
 */
public interface DocumentLectureService {

    /**
     * Get download URL for a document lecture
     * Returns pre-signed URL for S3-stored documents
     *
     * @param lectureId The document lecture ID
     * @return DocumentDownloadUrlResponse containing the download URL and metadata
     */
    DocumentDownloadUrlResponse getDownloadUrl(UUID lectureId);
}
