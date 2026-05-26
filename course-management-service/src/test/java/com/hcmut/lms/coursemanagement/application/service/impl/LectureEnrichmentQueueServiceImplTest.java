package com.hcmut.lms.coursemanagement.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coursemanagement.application.dto.request.LectureEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureEnrichmentJobResponse;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.*;
import com.hcmut.lms.coursemanagement.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureEnrichmentQueueServiceImplTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private VideoLectureRepository videoLectureRepository;

    @Mock
    private DocumentLectureRepository documentLectureRepository;

    @Mock
    private TextLectureRepository textLectureRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LectureEnrichmentQueueServiceImpl lectureEnrichmentQueueService;

    private UUID videoLectureId;
    private UUID documentLectureId;
    private UUID textLectureId;
    private UUID unknownLectureId;

    @BeforeEach
    void setUp() {
        videoLectureId = UUID.randomUUID();
        documentLectureId = UUID.randomUUID();
        textLectureId = UUID.randomUUID();
        unknownLectureId = UUID.randomUUID();

        ReflectionTestUtils.setField(lectureEnrichmentQueueService,
                "videoTranscriptionQueueUrl", "https://sqs.test/video-queue");
        ReflectionTestUtils.setField(lectureEnrichmentQueueService,
                "documentEnrichmentQueueUrl", "https://sqs.test/document-queue");
        ReflectionTestUtils.setField(lectureEnrichmentQueueService,
                "cloudFrontDomain", "cloudfront.test");
        ReflectionTestUtils.setField(lectureEnrichmentQueueService,
                "transcriptionCallbackUrl", "https://callback.test/transcription");
        ReflectionTestUtils.setField(lectureEnrichmentQueueService,
                "documentEnrichmentCallbackUrl", "https://callback.test/document");
    }

    // -- lecture not found --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenLectureNotFound() {
        when(lectureRepository.findById(unknownLectureId)).thenReturn(Optional.empty());

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(unknownLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals(1, result.getFailures().size());
        assertEquals("Lecture not found", result.getFailures().get(0).getReason());
    }

    // -- type mismatch --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenTypeMismatch() {
        Lecture lecture = buildLecture(videoLectureId, LectureType.VIDEO, "Video Title");
        when(lectureRepository.findById(videoLectureId)).thenReturn(Optional.of(lecture));

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId))
                .lectureType("DOCUMENT") // requesting DOCUMENT but it's VIDEO
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertTrue(result.getFailures().get(0).getReason().contains("Lecture type mismatch"));
    }

    // -- video lecture: valid --

    @Test
    void queueEnrichmentJobs_shouldQueueVideo_whenValid() throws JsonProcessingException {
        Lecture lecture = buildLecture(videoLectureId, LectureType.VIDEO, "Video Title");
        VideoLecture videoLecture = new VideoLecture("Video Title", "https://www.youtube.com/watch?v=abc", 300);

        when(lectureRepository.findById(videoLectureId)).thenReturn(Optional.of(lecture));
        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));

        SendMessageResponse sqsResponse = SendMessageResponse.builder().messageId("msg-123").build();
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(sqsResponse);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"payload\"}");

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getSuccessfullyQueued());
        assertEquals(0, result.getFailed());
        assertEquals("youtube", result.getJobs().get(0).getSourceType());
        assertEquals("transcription", result.getJobs().get(0).getQueueType());
        assertEquals(1, result.getVideoJobs().getQueued());
    }

    // -- video lecture: video not found --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenVideoLectureNotFound() {
        Lecture lecture = buildLecture(videoLectureId, LectureType.VIDEO, "Video Title");
        when(lectureRepository.findById(videoLectureId)).thenReturn(Optional.of(lecture));
        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.empty());

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals("Video lecture not found", result.getFailures().get(0).getReason());
    }

    // -- video lecture: empty URL --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenVideoUrlEmpty() {
        Lecture lecture = buildLecture(videoLectureId, LectureType.VIDEO, "Video Title");
        VideoLecture videoLecture = new VideoLecture("Video Title", "", 300);

        when(lectureRepository.findById(videoLectureId)).thenReturn(Optional.of(lecture));
        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals("Video URL is empty", result.getFailures().get(0).getReason());
    }

    // -- video lecture: json exception --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenVideoJsonException() throws JsonProcessingException {
        Lecture lecture = buildLecture(videoLectureId, LectureType.VIDEO, "Video Title");
        VideoLecture videoLecture = new VideoLecture("Video Title", "https://youtube.com/watch?v=abc", 300);

        when(lectureRepository.findById(videoLectureId)).thenReturn(Optional.of(lecture));
        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertTrue(result.getFailures().get(0).getReason().contains("Failed to serialize message"));
    }

    // -- document lecture: valid --

    @Test
    void queueEnrichmentJobs_shouldQueueDocument_whenValid() throws JsonProcessingException {
        Lecture lecture = buildLecture(documentLectureId, LectureType.DOCUMENT, "Doc Title");
        DocumentLecture docLecture = new DocumentLecture("Doc Title", "https://cloudfront.test/doc.pdf");
        docLecture.setFileFormat("pdf");
        docLecture.setNumPages(10);

        when(lectureRepository.findById(documentLectureId)).thenReturn(Optional.of(lecture));
        when(documentLectureRepository.findById(documentLectureId)).thenReturn(Optional.of(docLecture));

        SendMessageResponse sqsResponse = SendMessageResponse.builder().messageId("msg-456").build();
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(sqsResponse);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"payload\"}");

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(documentLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getSuccessfullyQueued());
        assertEquals(0, result.getFailed());
        assertEquals("enrichment", result.getJobs().get(0).getQueueType());
        assertEquals(1, result.getDocumentJobs().getQueued());
    }

    // -- document lecture: not found --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenDocumentLectureNotFound() {
        Lecture lecture = buildLecture(documentLectureId, LectureType.DOCUMENT, "Doc Title");
        when(lectureRepository.findById(documentLectureId)).thenReturn(Optional.of(lecture));
        when(documentLectureRepository.findById(documentLectureId)).thenReturn(Optional.empty());

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(documentLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals("Document lecture not found", result.getFailures().get(0).getReason());
    }

    // -- document lecture: empty URL --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenDocumentUrlEmpty() {
        Lecture lecture = buildLecture(documentLectureId, LectureType.DOCUMENT, "Doc Title");
        DocumentLecture docLecture = new DocumentLecture("Doc Title", "");

        when(lectureRepository.findById(documentLectureId)).thenReturn(Optional.of(lecture));
        when(documentLectureRepository.findById(documentLectureId)).thenReturn(Optional.of(docLecture));

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(documentLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals("Document file URL is empty", result.getFailures().get(0).getReason());
    }

    // -- document lecture: json exception --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenDocumentJsonException() throws JsonProcessingException {
        Lecture lecture = buildLecture(documentLectureId, LectureType.DOCUMENT, "Doc Title");
        DocumentLecture docLecture = new DocumentLecture("Doc Title", "https://cloudfront.test/doc.pdf");

        when(lectureRepository.findById(documentLectureId)).thenReturn(Optional.of(lecture));
        when(documentLectureRepository.findById(documentLectureId)).thenReturn(Optional.of(docLecture));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(documentLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
    }

    // -- text lecture: valid --

    @Test
    void queueEnrichmentJobs_shouldQueueText_whenValid() throws JsonProcessingException {
        Lecture lecture = buildLecture(textLectureId, LectureType.TEXT, "Text Title");
        TextLecture textLecture = new TextLecture("Text Title", "Some content here", "plain");

        when(lectureRepository.findById(textLectureId)).thenReturn(Optional.of(lecture));
        when(textLectureRepository.findById(textLectureId)).thenReturn(Optional.of(textLecture));

        SendMessageResponse sqsResponse = SendMessageResponse.builder().messageId("msg-789").build();
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(sqsResponse);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"payload\"}");

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(textLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getSuccessfullyQueued());
        assertEquals(0, result.getFailed());
        assertEquals("inline", result.getJobs().get(0).getSourceType());
        assertEquals(1, result.getTextJobs().getQueued());
    }

    // -- text lecture: not found --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenTextLectureNotFound() {
        Lecture lecture = buildLecture(textLectureId, LectureType.TEXT, "Text Title");
        when(lectureRepository.findById(textLectureId)).thenReturn(Optional.of(lecture));
        when(textLectureRepository.findById(textLectureId)).thenReturn(Optional.empty());

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(textLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals("Text lecture not found", result.getFailures().get(0).getReason());
    }

    // -- text lecture: empty content --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenTextContentEmpty() {
        Lecture lecture = buildLecture(textLectureId, LectureType.TEXT, "Text Title");
        TextLecture textLecture = new TextLecture("Text Title", "", "plain");

        when(lectureRepository.findById(textLectureId)).thenReturn(Optional.of(lecture));
        when(textLectureRepository.findById(textLectureId)).thenReturn(Optional.of(textLecture));

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(textLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertEquals("Text content is empty", result.getFailures().get(0).getReason());
    }

    // -- text lecture: json exception --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenTextJsonException() throws JsonProcessingException {
        Lecture lecture = buildLecture(textLectureId, LectureType.TEXT, "Text Title");
        TextLecture textLecture = new TextLecture("Text Title", "Valid content", "markdown");

        when(lectureRepository.findById(textLectureId)).thenReturn(Optional.of(lecture));
        when(textLectureRepository.findById(textLectureId)).thenReturn(Optional.of(textLecture));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(textLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
    }

    // -- mixed types --

    @Test
    void queueEnrichmentJobs_shouldHandleMixedLectureTypes() throws JsonProcessingException {
        Lecture videoLecture = buildLecture(videoLectureId, LectureType.VIDEO, "V1");
        Lecture docLecture = buildLecture(documentLectureId, LectureType.DOCUMENT, "D1");

        VideoLecture vl = new VideoLecture("V1", "https://youtube.com/watch?v=abc", 300);
        DocumentLecture dl = new DocumentLecture("D1", "https://cloudfront.test/doc.pdf");

        when(lectureRepository.findById(videoLectureId)).thenReturn(Optional.of(videoLecture));
        when(lectureRepository.findById(documentLectureId)).thenReturn(Optional.of(docLecture));
        when(videoLectureRepository.findById(videoLectureId)).thenReturn(Optional.of(vl));
        when(documentLectureRepository.findById(documentLectureId)).thenReturn(Optional.of(dl));

        SendMessageResponse sqsResponse = SendMessageResponse.builder().messageId("msg").build();
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(sqsResponse);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"payload\"}");

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId, documentLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(2, result.getSuccessfullyQueued());
        assertEquals(0, result.getFailed());
        assertEquals(1, result.getVideoJobs().getQueued());
        assertEquals(1, result.getDocumentJobs().getQueued());
    }

    // -- exception in main loop --

    @Test
    void queueEnrichmentJobs_shouldReportFailed_whenExceptionInLoop() {
        when(lectureRepository.findById(videoLectureId)).thenThrow(new RuntimeException("DB error"));

        LectureEnrichmentJobRequest request = LectureEnrichmentJobRequest.builder()
                .lectureIds(List.of(videoLectureId))
                .build();

        LectureEnrichmentJobResponse result = lectureEnrichmentQueueService.queueEnrichmentJobs(request);

        assertEquals(1, result.getFailed());
        assertTrue(result.getFailures().get(0).getReason().contains("Failed to queue"));
    }

    // -- helper --

    private Lecture buildLecture(UUID id, LectureType type, String title) {
        // Using VideoLecture as concrete class for the abstract Lecture
        Lecture lecture = new VideoLecture(title, "http://example.com", 100);
        lecture.setId(id);
        lecture.setLectureType(type);
        return lecture;
    }
}
