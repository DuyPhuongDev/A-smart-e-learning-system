package com.hcmut.lms.coursemanagement.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coursemanagement.application.dto.request.LectureEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureEnrichmentJobResponse;
import com.hcmut.lms.coursemanagement.application.service.LectureEnrichmentQueueService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.*;
import com.hcmut.lms.coursemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.*;

/**
 * Unified implementation that routes lectures to appropriate SQS queues
 * based on lecture type (VIDEO, DOCUMENT, TEXT)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LectureEnrichmentQueueServiceImpl implements LectureEnrichmentQueueService {

    private final SqsClient sqsClient;
    private final LectureRepository lectureRepository;
    private final VideoLectureRepository videoLectureRepository;
    private final DocumentLectureRepository documentLectureRepository;
    private final TextLectureRepository textLectureRepository;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.video-transcription-queue-url}")
    private String videoTranscriptionQueueUrl;

    @Value("${aws.sqs.document-enrichment-queue-url}")
    private String documentEnrichmentQueueUrl;

    @Value("${aws.cloud-front.domain}")
    private String cloudFrontDomain;

    @Value("${transcription.callback-url}")
    private String transcriptionCallbackUrl;

    @Value("${document-enrichment.callback-url}")
    private String documentEnrichmentCallbackUrl;

    private static final String LECTURE_TYPE_VIDEO = "VIDEO";
    private static final String LECTURE_TYPE_DOCUMENT = "DOCUMENT";
    private static final String LECTURE_TYPE_TEXT = "TEXT";

    @Override
    public LectureEnrichmentJobResponse queueEnrichmentJobs(LectureEnrichmentJobRequest request) {
        log.info("Queueing {} lecture enrichment jobs, lectureType filter: {}",
                request.getLectureIds().size(), request.getLectureType());

        List<LectureEnrichmentJobResponse.QueuedJob> allQueuedJobs = new ArrayList<>();
        List<LectureEnrichmentJobResponse.FailedJob> allFailedJobs = new ArrayList<>();

        // Counters for breakdown
        int videoRequested = 0, videoQueued = 0, videoFailed = 0;
        int documentRequested = 0, documentQueued = 0, documentFailed = 0;
        int textRequested = 0, textQueued = 0, textFailed = 0;

        for (UUID lectureId : request.getLectureIds()) {
            try {
                // Get lecture to determine type
                Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
                if (optionalLecture.isEmpty()) {
                    allFailedJobs.add(buildFailedJob(lectureId, "UNKNOWN", "Lecture not found"));
                    continue;
                }

                Lecture lecture = optionalLecture.get();
                String lectureType = lecture.getLectureType().name();

                // Filter by lecture type if specified
                if (request.getLectureType() != null && !request.getLectureType().equalsIgnoreCase(lectureType)) {
                    allFailedJobs.add(buildFailedJob(lectureId, lectureType,
                            "Lecture type mismatch: expected " + request.getLectureType() + " but got " + lectureType));
                    continue;
                }

                // Route to appropriate queue based on type
                boolean success = false;
                switch (lectureType) {
                    case LECTURE_TYPE_VIDEO:
                        videoRequested++;
                        success = processVideoLecture(lectureId, lecture.getTitle(),
                                allQueuedJobs, allFailedJobs, request.getCallbackUrl());
                        if (success) videoQueued++; else videoFailed++;
                        break;

                    case LECTURE_TYPE_DOCUMENT:
                        documentRequested++;
                        success = processDocumentLecture(lectureId, lecture.getTitle(),
                                allQueuedJobs, allFailedJobs, request.getCallbackUrl());
                        if (success) documentQueued++; else documentFailed++;
                        break;

                    case LECTURE_TYPE_TEXT:
                        textRequested++;
                        success = processTextLecture(lectureId, lecture.getTitle(),
                                allQueuedJobs, allFailedJobs, request.getCallbackUrl());
                        if (success) textQueued++; else textFailed++;
                        break;

                    default:
                        allFailedJobs.add(buildFailedJob(lectureId, lectureType,
                                "Unsupported lecture type: " + lectureType));
                }

            } catch (Exception e) {
                log.error("Failed to queue job for lecture {}: {}", lectureId, e.getMessage());
                allFailedJobs.add(buildFailedJob(lectureId, "UNKNOWN", "Failed to queue: " + e.getMessage()));
            }
        }

        log.info("Queued {} jobs, failed {} jobs (VIDEO: {}/{}, DOCUMENT: {}/{}, TEXT: {}/{})",
                allQueuedJobs.size(), allFailedJobs.size(),
                videoQueued, videoRequested, documentQueued, documentRequested, textQueued, textRequested);

        return LectureEnrichmentJobResponse.builder()
                .totalRequested(request.getLectureIds().size())
                .successfullyQueued(allQueuedJobs.size())
                .failed(allFailedJobs.size())
                .videoJobs(LectureEnrichmentJobResponse.TypeBreakdown.builder()
                        .requested(videoRequested)
                        .queued(videoQueued)
                        .failed(videoFailed)
                        .build())
                .documentJobs(LectureEnrichmentJobResponse.TypeBreakdown.builder()
                        .requested(documentRequested)
                        .queued(documentQueued)
                        .failed(documentFailed)
                        .build())
                .textJobs(LectureEnrichmentJobResponse.TypeBreakdown.builder()
                        .requested(textRequested)
                        .queued(textQueued)
                        .failed(textFailed)
                        .build())
                .jobs(allQueuedJobs)
                .failures(allFailedJobs)
                .build();
    }

    /**
     * Process VIDEO lecture - send to video-transcription-queue
     */
    private boolean processVideoLecture(UUID lectureId, String lectureTitle,
                                        List<LectureEnrichmentJobResponse.QueuedJob> queuedJobs,
                                        List<LectureEnrichmentJobResponse.FailedJob> failedJobs,
                                        String customCallbackUrl) {
        Optional<VideoLecture> optionalVideo = videoLectureRepository.findById(lectureId);
        if (optionalVideo.isEmpty()) {
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_VIDEO, "Video lecture not found"));
            return false;
        }

        VideoLecture videoLecture = optionalVideo.get();
        String videoUrl = videoLecture.getVideoUrl();

        if (videoUrl == null || videoUrl.isBlank()) {
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_VIDEO, "Video URL is empty"));
            return false;
        }

        String sourceType = determineSourceType(videoUrl);

        try {
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("lectureId", lectureId.toString());
            messageBody.put("videoUrl", videoUrl);
            messageBody.put("sourceType", sourceType);
            messageBody.put("callbackUrl", customCallbackUrl != null ? customCallbackUrl : transcriptionCallbackUrl);
            messageBody.put("lectureTitle", lectureTitle);

            SendMessageResponse response = sendToSqs(videoTranscriptionQueueUrl, messageBody);

            log.info("Queued video transcription job for lecture {}: messageId={}", lectureId, response.messageId());

            queuedJobs.add(buildQueuedJob(lectureId, response.messageId(), LECTURE_TYPE_VIDEO, "transcription", sourceType));
            return true;

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for video lecture {}: {}", lectureId, e.getMessage());
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_VIDEO, "Failed to serialize message: " + e.getMessage()));
            return false;
        }
    }

    /**
     * Process DOCUMENT lecture - send to document-enrichment-queue
     */
    private boolean processDocumentLecture(UUID lectureId, String lectureTitle,
                                           List<LectureEnrichmentJobResponse.QueuedJob> queuedJobs,
                                           List<LectureEnrichmentJobResponse.FailedJob> failedJobs,
                                           String customCallbackUrl) {
        Optional<DocumentLecture> optionalDoc = documentLectureRepository.findById(lectureId);
        if (optionalDoc.isEmpty()) {
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_DOCUMENT, "Document lecture not found"));
            return false;
        }

        DocumentLecture docLecture = optionalDoc.get();
        String fileUrl = docLecture.getFileUrl();

        if (fileUrl == null || fileUrl.isBlank()) {
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_DOCUMENT, "Document file URL is empty"));
            return false;
        }

        String sourceType = determineSourceType(fileUrl);

        try {
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("lectureId", lectureId.toString());
            messageBody.put("lectureType", LECTURE_TYPE_DOCUMENT);
            messageBody.put("downloadUrl", fileUrl);
            messageBody.put("sourceType", sourceType);
            messageBody.put("fileFormat", docLecture.getFileFormat());
            messageBody.put("numPages", docLecture.getNumPages());
            messageBody.put("callbackUrl", customCallbackUrl != null ? customCallbackUrl : documentEnrichmentCallbackUrl);
            messageBody.put("lectureTitle", lectureTitle);

            SendMessageResponse response = sendToSqs(documentEnrichmentQueueUrl, messageBody);

            log.info("Queued document enrichment job for lecture {}: messageId={}", lectureId, response.messageId());

            queuedJobs.add(buildQueuedJob(lectureId, response.messageId(), LECTURE_TYPE_DOCUMENT, "enrichment", sourceType));
            return true;

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for document lecture {}: {}", lectureId, e.getMessage());
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_DOCUMENT, "Failed to serialize message: " + e.getMessage()));
            return false;
        }
    }

    /**
     * Process TEXT lecture - send to document-enrichment-queue
     */
    private boolean processTextLecture(UUID lectureId, String lectureTitle,
                                       List<LectureEnrichmentJobResponse.QueuedJob> queuedJobs,
                                       List<LectureEnrichmentJobResponse.FailedJob> failedJobs,
                                       String customCallbackUrl) {
        Optional<TextLecture> optionalText = textLectureRepository.findById(lectureId);
        if (optionalText.isEmpty()) {
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_TEXT, "Text lecture not found"));
            return false;
        }

        TextLecture textLecture = optionalText.get();
        String content = textLecture.getContent();

        if (content == null || content.isBlank()) {
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_TEXT, "Text content is empty"));
            return false;
        }

        try {
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("lectureId", lectureId.toString());
            messageBody.put("lectureType", LECTURE_TYPE_TEXT);
            messageBody.put("textContent", content);
            messageBody.put("formatType", textLecture.getFormatType());
            messageBody.put("wordCount", textLecture.getWordCount());
            messageBody.put("callbackUrl", customCallbackUrl != null ? customCallbackUrl : documentEnrichmentCallbackUrl);
            messageBody.put("lectureTitle", lectureTitle);

            SendMessageResponse response = sendToSqs(documentEnrichmentQueueUrl, messageBody);

            log.info("Queued text enrichment job for lecture {}: messageId={}", lectureId, response.messageId());

            queuedJobs.add(buildQueuedJob(lectureId, response.messageId(), LECTURE_TYPE_TEXT, "enrichment", "inline"));
            return true;

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for text lecture {}: {}", lectureId, e.getMessage());
            failedJobs.add(buildFailedJob(lectureId, LECTURE_TYPE_TEXT, "Failed to serialize message: " + e.getMessage()));
            return false;
        }
    }

    /**
     * Send message to SQS queue
     */
    private SendMessageResponse sendToSqs(String queueUrl, Map<String, Object> messageBody) throws JsonProcessingException {
        String messageJson = objectMapper.writeValueAsString(messageBody);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageJson)
                .build();

        return sqsClient.sendMessage(sendRequest);
    }

    /**
     * Determine source type based on URL
     */
    private String determineSourceType(String url) {
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            return "youtube";
        } else if (url.contains(cloudFrontDomain) || url.contains("cloudfront.net")) {
            return "cloudfront";
        } else if (url.contains("s3.amazonaws.com") || url.contains(".s3.")) {
            return "s3";
        } else {
            return "direct";
        }
    }

    /**
     * Helper method to build FailedJob response
     */
    private LectureEnrichmentJobResponse.FailedJob buildFailedJob(UUID lectureId, String lectureType, String reason) {
        return LectureEnrichmentJobResponse.FailedJob.builder()
                .lectureId(lectureId)
                .lectureType(lectureType)
                .reason(reason)
                .build();
    }

    /**
     * Helper method to build QueuedJob response
     */
    private LectureEnrichmentJobResponse.QueuedJob buildQueuedJob(UUID lectureId, String messageId,
                                                                  String lectureType, String queueType, String sourceType) {
        return LectureEnrichmentJobResponse.QueuedJob.builder()
                .lectureId(lectureId)
                .messageId(messageId)
                .lectureType(lectureType)
                .queueType(queueType)
                .sourceType(sourceType)
                .build();
    }
}
