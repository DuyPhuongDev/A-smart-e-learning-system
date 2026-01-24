package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.VideoDownloadUrlResponse;
import com.hcmut.lms.coursemanagement.application.service.FileService;
import com.hcmut.lms.coursemanagement.application.service.VideoLectureService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.domain.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of VideoLectureService
 * Handles video download URL generation for S3 and YouTube videos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoLectureServiceImpl implements VideoLectureService {

    private final VideoLectureRepository videoLectureRepository;
    private final FileService fileService;

    @Value("${aws.cloud-front.domain}")
    private String cloudFrontDomain;

    @Value("${aws.s3.expiration.hours:1}")
    private int expirationHours;

    @Override
    @Transactional(readOnly = true)
    public VideoDownloadUrlResponse getDownloadUrl(UUID lectureId) {
        log.info("Getting download URL for video lecture: {}", lectureId);

        VideoLecture videoLecture = videoLectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Video lecture not found with id: " + lectureId));

        String videoUrl = videoLecture.getVideoUrl();
        if (videoUrl == null || videoUrl.isBlank()) {
            throw new RuntimeException("Video URL is empty for lecture: " + lectureId);
        }

        // Determine source type and generate appropriate URL
        if (isYouTubeUrl(videoUrl)) {
            return buildYouTubeResponse(lectureId, videoUrl);
        } else if (isCloudFrontUrl(videoUrl)) {
            return buildS3PresignedResponse(lectureId, videoUrl);
        } else {
            // For other URLs, return as-is (direct URL)
            return buildDirectUrlResponse(lectureId, videoUrl);
        }
    }

    /**
     * Build response for YouTube videos
     */
    private VideoDownloadUrlResponse buildYouTubeResponse(UUID lectureId, String videoUrl) {
        log.info("YouTube URL detected for lecture: {}", lectureId);
        return VideoDownloadUrlResponse.builder()
                .lectureId(lectureId)
                .downloadUrl(videoUrl)
                .originalUrl(videoUrl)
                .sourceType("youtube")
                .requiresPresigning(false)
                .expirationSeconds(null)
                .build();
    }

    /**
     * Build response for S3/CloudFront videos with pre-signed URL
     */
    private VideoDownloadUrlResponse buildS3PresignedResponse(UUID lectureId, String videoUrl) {
        log.info("S3/CloudFront URL detected for lecture: {}, generating pre-signed URL", lectureId);

        // Extract folder path and file name from CloudFront URL
        String key = extractKeyFromCloudFrontUrl(videoUrl);
        if (key == null || key.isBlank()) {
            throw new RuntimeException("Invalid CloudFront URL format: " + videoUrl);
        }

        // Split key into folder path and file name
        int lastSlashIndex = key.lastIndexOf('/');
        String folderPath = lastSlashIndex > 0 ? key.substring(0, lastSlashIndex) : "";
        String fileName = lastSlashIndex >= 0 ? key.substring(lastSlashIndex + 1) : key;

        // Generate pre-signed download URL
        String presignedUrl = fileService.generateDownloadUrl(folderPath, fileName);
        int expirationSeconds = expirationHours * 3600;

        return VideoDownloadUrlResponse.builder()
                .lectureId(lectureId)
                .downloadUrl(presignedUrl)
                .originalUrl(videoUrl)
                .sourceType("s3")
                .requiresPresigning(true)
                .expirationSeconds(expirationSeconds)
                .build();
    }

    /**
     * Build response for direct URLs (not YouTube or S3)
     */
    private VideoDownloadUrlResponse buildDirectUrlResponse(UUID lectureId, String videoUrl) {
        log.info("Direct URL detected for lecture: {}", lectureId);
        return VideoDownloadUrlResponse.builder()
                .lectureId(lectureId)
                .downloadUrl(videoUrl)
                .originalUrl(videoUrl)
                .sourceType("direct")
                .requiresPresigning(false)
                .expirationSeconds(null)
                .build();
    }

    /**
     * Check if URL is a YouTube URL
     */
    private boolean isYouTubeUrl(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
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
