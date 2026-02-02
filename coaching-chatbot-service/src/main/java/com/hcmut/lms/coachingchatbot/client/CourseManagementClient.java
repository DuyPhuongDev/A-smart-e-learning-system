package com.hcmut.lms.coachingchatbot.client;

import com.hcmut.lms.coachingchatbot.client.dto.ChapterResponse;
import com.hcmut.lms.coachingchatbot.client.dto.DocumentDownloadUrlResponse;
import com.hcmut.lms.coachingchatbot.client.dto.LectureResponse;
import com.hcmut.lms.coachingchatbot.client.dto.TextLectureContentResponse;
import com.hcmut.lms.coachingchatbot.client.dto.VideoDownloadUrlResponse;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptRequest;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for Course Management Service
 * Provides access to lecture and chapter information via internal endpoints
 * These endpoints are for service-to-service communication only
 */
@FeignClient(name = "course-management-service", path = "/api/courses/v1/internal")
public interface CourseManagementClient {

    /**
     * Get lecture by ID
     * Used by learning-service to get lecture information
     */
    @GetMapping("/lectures/{lectureId}")
    LectureResponse getLectureById(@PathVariable("lectureId") UUID lectureId);

    /**
     * Get all lectures by chapter ID
     * Used by learning-service to get all lectures in a chapter
     */
    @GetMapping("/lectures/chapter/{chapterId}")
    List<LectureResponse> getLecturesByChapterId(@PathVariable("chapterId") UUID chapterId);

    /**
     * Get chapter by ID
     * Used by learning-service to get chapter information
     */
    @GetMapping("/chapters/{chapterId}")
    ChapterResponse getChapterById(@PathVariable("chapterId") UUID chapterId);

    /**
     * Get download URL for a video lecture
     * For S3 videos: returns pre-signed URL
     * For YouTube videos: returns original URL
     * Used by learning-service to get video download URL
     */
    @GetMapping("/video-lectures/{lectureId}/download-url")
    VideoDownloadUrlResponse getVideoDownloadUrl(@PathVariable("lectureId") UUID lectureId);

    /**
     * Create video transcript
     * Used by learning-service to create transcript after AI processing
     */
    @PostMapping("/video-transcripts")
    VideoTranscriptResponse createVideoTranscript(@RequestBody VideoTranscriptRequest request);

    /**
     * Create multiple video transcripts (batch)
     * Used by learning-service to create transcript segments after AI processing
     */
    @PostMapping("/video-transcripts/batch")
    List<VideoTranscriptResponse> createVideoTranscripts(@RequestBody List<VideoTranscriptRequest> requests);

    /**
     * Check if transcript exists for a video lecture
     * Used by learning-service to check before processing
     */
    @GetMapping("/video-transcripts/video-lecture/{videoLectureId}/exists")
    Boolean existsTranscript(@PathVariable("videoLectureId") UUID videoLectureId);

    /**
     * Get all transcript segments for a video lecture, ordered by segment index
     * Used by learning-service to fetch transcripts for enrichment processing
     */
    @GetMapping("/video-transcripts/video-lecture/{videoLectureId}/segments")
    List<VideoTranscriptResponse> getTranscriptSegments(@PathVariable("videoLectureId") UUID videoLectureId);

    // ==================== Document Lecture Endpoints ====================

    /**
     * Get download URL for a document lecture
     * For S3 documents: returns pre-signed URL
     * Used by learning-service to download document for processing
     */
    @GetMapping("/document-lectures/{lectureId}/download-url")
    DocumentDownloadUrlResponse getDocumentDownloadUrl(@PathVariable("lectureId") UUID lectureId);

    // ==================== Text Lecture Endpoints ====================

    /**
     * Get content for a text lecture
     * Used by learning-service to get text content for processing
     */
    @GetMapping("/text-lectures/{lectureId}/content")
    TextLectureContentResponse getTextLectureContent(@PathVariable("lectureId") UUID lectureId);
}
