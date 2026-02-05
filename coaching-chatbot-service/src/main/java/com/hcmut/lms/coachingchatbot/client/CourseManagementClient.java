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
     * Get all transcript segments for a video lecture, ordered by segment index
     * Used by learning-service to fetch transcripts for enrichment processing
     */
    @GetMapping("/video-transcripts/video-lecture/{videoLectureId}/segments")
    List<VideoTranscriptResponse> getTranscriptSegments(@PathVariable("videoLectureId") UUID videoLectureId);
}
