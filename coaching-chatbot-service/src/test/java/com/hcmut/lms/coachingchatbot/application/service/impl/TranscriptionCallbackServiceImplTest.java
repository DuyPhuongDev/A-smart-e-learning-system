package com.hcmut.lms.coachingchatbot.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.coachingchatbot.application.mapper.TranscriptionCallbackMapper;
import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.request.TranscriptionCallbackBatchRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.TranscriptionCallbackResponse;
import com.hcmut.lms.coachingchatbot.application.service.TranscriptEnrichmentService;
import com.hcmut.lms.coachingchatbot.client.CourseManagementClient;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptRequest;
import com.hcmut.lms.coachingchatbot.client.dto.VideoTranscriptResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TranscriptionCallbackServiceImplTest {

    @Mock private CourseManagementClient courseManagementClient;
    @Mock private TranscriptEnrichmentService transcriptEnrichmentService;
    @Mock private TranscriptionCallbackMapper transcriptionCallbackMapper;

    @InjectMocks
    private TranscriptionCallbackServiceImpl transcriptionCallbackService;

    private static final UUID lectureId = UUID.randomUUID();
    private static final UUID transcriptId = UUID.randomUUID();

    @Test
    void processTranscriptionCallback_shouldReturnResponse_whenValidRequest() {
        TranscriptionCallbackRequest request = TranscriptionCallbackRequest.builder()
                .videoLectureId(lectureId).segmentIndex(0)
                .startTimeSeconds(0).endTimeSeconds(30).transcriptText("Hello").build();
        VideoTranscriptRequest transcriptRequest = new VideoTranscriptRequest();
        VideoTranscriptResponse transcriptResponse = VideoTranscriptResponse.builder()
                .id(transcriptId).build();

        when(transcriptionCallbackMapper.toVideoTranscriptRequest(request)).thenReturn(transcriptRequest);
        when(courseManagementClient.createVideoTranscript(transcriptRequest)).thenReturn(transcriptResponse);
        when(transcriptEnrichmentService.enrichTranscriptsForLectureAsync(lectureId))
                .thenReturn(CompletableFuture.completedFuture(null));

        TranscriptionCallbackResponse result = transcriptionCallbackService.processTranscriptionCallback(request);
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void processTranscriptionCallback_shouldHandleFailure_whenTranscriptionFails() {
        TranscriptionCallbackRequest request = TranscriptionCallbackRequest.builder()
                .videoLectureId(lectureId).segmentIndex(0)
                .startTimeSeconds(0).endTimeSeconds(30).transcriptText("Hello").build();

        when(transcriptionCallbackMapper.toVideoTranscriptRequest(request)).thenReturn(new VideoTranscriptRequest());
        when(courseManagementClient.createVideoTranscript(any()))
                .thenThrow(new RuntimeException("Service unavailable"));

        TranscriptionCallbackResponse result = transcriptionCallbackService.processTranscriptionCallback(request);
        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
    }

    @Test
    void processTranscriptionCallbackBatch_shouldReturnResponse_whenValidRequest() {
        TranscriptionCallbackBatchRequest request = TranscriptionCallbackBatchRequest.builder()
                .videoLectureId(lectureId).segments(List.of()).build();

        when(courseManagementClient.createVideoTranscripts(any())).thenReturn(List.of());
        when(transcriptEnrichmentService.enrichTranscriptsForLectureAsync(lectureId))
                .thenReturn(CompletableFuture.completedFuture(null));

        TranscriptionCallbackResponse result = transcriptionCallbackService.processTranscriptionCallbackBatch(request);
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void processTranscriptionCallbackBatch_shouldHandleFailure() {
        TranscriptionCallbackBatchRequest request = TranscriptionCallbackBatchRequest.builder()
                .videoLectureId(lectureId).segments(List.of()).build();

        when(courseManagementClient.createVideoTranscripts(any()))
                .thenThrow(new RuntimeException("Batch failed"));

        TranscriptionCallbackResponse result = transcriptionCallbackService.processTranscriptionCallbackBatch(request);
        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
    }
}
