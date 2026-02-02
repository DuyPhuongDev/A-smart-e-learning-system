package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.DocumentDownloadUrlResponse;
import com.hcmut.lms.coursemanagement.application.service.DocumentLectureService;
import com.hcmut.lms.coursemanagement.application.service.FileService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.DocumentLecture;
import com.hcmut.lms.coursemanagement.repository.DocumentLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of DocumentLectureService
 * Handles document download URL generation for S3-stored documents
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentLectureServiceImpl implements DocumentLectureService {

    private final DocumentLectureRepository documentLectureRepository;
    private final FileService fileService;

    @Value("${aws.cloud-front.domain}")
    private String cloudFrontDomain;

    @Value("${aws.s3.expiration.hours:1}")
    private int expirationHours;

    @Override
    @Transactional(readOnly = true)
    public DocumentDownloadUrlResponse getDownloadUrl(UUID lectureId) {
        log.info("Getting download URL for document lecture: {}", lectureId);

        DocumentLecture documentLecture = documentLectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Document lecture not found with id: " + lectureId));

        String fileUrl = documentLecture.getFileUrl();
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new RuntimeException("File URL is empty for lecture: " + lectureId);
        }

        // Determine source type and generate appropriate URL
        if (isCloudFrontUrl(fileUrl)) {
            return buildS3PresignedResponse(documentLecture);
        } else {
            // For other URLs, return as-is (direct URL)
            return buildDirectUrlResponse(documentLecture);
        }
    }

    /**
     * Build response for S3/CloudFront documents with pre-signed URL
     */
    private DocumentDownloadUrlResponse buildS3PresignedResponse(DocumentLecture documentLecture) {
        String fileUrl = documentLecture.getFileUrl();
        log.info("S3/CloudFront URL detected for document lecture: {}, generating pre-signed URL",
                documentLecture.getId());

        // Extract folder path and file name from CloudFront URL
        String key = extractKeyFromCloudFrontUrl(fileUrl);
        if (key == null || key.isBlank()) {
            throw new RuntimeException("Invalid CloudFront URL format: " + fileUrl);
        }

        // Split key into folder path and file name
        int lastSlashIndex = key.lastIndexOf('/');
        String folderPath = lastSlashIndex > 0 ? key.substring(0, lastSlashIndex) : "";
        String fileName = lastSlashIndex >= 0 ? key.substring(lastSlashIndex + 1) : key;

        // Generate pre-signed download URL
        String presignedUrl = fileService.generateDownloadUrl(folderPath, fileName);
        int expirationSeconds = expirationHours * 3600;

        return DocumentDownloadUrlResponse.builder()
                .lectureId(documentLecture.getId())
                .downloadUrl(presignedUrl)
                .originalUrl(fileUrl)
                .sourceType("s3")
                .fileFormat(documentLecture.getFileFormat())
                .numPages(documentLecture.getNumPages())
                .requiresPresigning(true)
                .expirationSeconds(expirationSeconds)
                .build();
    }

    /**
     * Build response for direct URLs (not S3)
     */
    private DocumentDownloadUrlResponse buildDirectUrlResponse(DocumentLecture documentLecture) {
        log.info("Direct URL detected for document lecture: {}", documentLecture.getId());
        return DocumentDownloadUrlResponse.builder()
                .lectureId(documentLecture.getId())
                .downloadUrl(documentLecture.getFileUrl())
                .originalUrl(documentLecture.getFileUrl())
                .sourceType("direct")
                .fileFormat(documentLecture.getFileFormat())
                .numPages(documentLecture.getNumPages())
                .requiresPresigning(false)
                .expirationSeconds(null)
                .build();
    }

    /**
     * Check if URL is a CloudFront/S3 URL
     */
    private boolean isCloudFrontUrl(String url) {
        return url.contains(cloudFrontDomain) ||
               url.contains("s3.amazonaws.com") ||
               url.contains(".s3.") ||
               url.matches(".*\\.s3\\.[a-z0-9-]+\\.amazonaws\\.com.*");
    }

    /**
     * Extract S3 key from CloudFront URL
     */
    private String extractKeyFromCloudFrontUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        // Remove CloudFront domain prefix
        String key = url.replace("https://" + cloudFrontDomain + "/", "");
        // Also handle http
        key = key.replace("http://" + cloudFrontDomain + "/", "");
        return key;
    }
}
