package com.hcmut.lms.coursemanagement.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coursemanagement.application.dto.request.DocumentEnrichmentJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DocumentEnrichmentJobResponse;
import com.hcmut.lms.coursemanagement.application.service.DocumentEnrichmentQueueService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.DocumentLecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.TextLecture;
import com.hcmut.lms.coursemanagement.repository.DocumentLectureRepository;
import com.hcmut.lms.coursemanagement.repository.LectureRepository;
import com.hcmut.lms.coursemanagement.repository.TextLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.*;

/**
 * Implementation of DocumentEnrichmentQueueService
 * Sends document/text lecture enrichment jobs to SQS queue for processing by AWS Fargate workers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentEnrichmentQueueServiceImpl implements DocumentEnrichmentQueueService {

    private final SqsClient sqsClient;
    private final DocumentLectureRepository documentLectureRepository;
    private final TextLectureRepository textLectureRepository;
    private final LectureRepository lectureRepository;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.document-enrichment-queue-url}")
    private String queueUrl;

    @Value("${aws.cloud-front.domain}")
    private String cloudFrontDomain;

    @Value("${document-enrichment.callback-url}")
    private String callbackUrl;

    private static final String LECTURE_TYPE_DOCUMENT = "DOCUMENT";
    private static final String LECTURE_TYPE_TEXT = "TEXT";

    @Override
    public DocumentEnrichmentJobResponse queueEnrichmentJobs(DocumentEnrichmentJobRequest request) {
        log.info("Queueing {} document/text enrichment jobs", request.getLectureIds().size());

        List<DocumentEnrichmentJobResponse.QueuedJob> queuedJobs = new ArrayList<>();
        List<DocumentEnrichmentJobResponse.FailedJob> failedJobs = new ArrayList<>();

        for (UUID lectureId : request.getLectureIds()) {
            try {
                // First, get the base lecture to determine type
                Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
                if (optionalLecture.isEmpty()) {
                    failedJobs.add(buildFailedJob(lectureId, "Lecture not found"));
                    continue;
                }

                Lecture lecture = optionalLecture.get();
                String lectureType = lecture.getLectureType().name();

                // Filter by lecture type if specified
                if (request.getLectureType() != null && !request.getLectureType().equalsIgnoreCase(lectureType)) {
                    failedJobs.add(buildFailedJob(lectureId,
                            "Lecture type mismatch: expected " + request.getLectureType() + " but got " + lectureType));
                    continue;
                }

                // Process based on lecture type
                if (LECTURE_TYPE_DOCUMENT.equalsIgnoreCase(lectureType)) {
                    processDocumentLecture(lectureId, lecture.getTitle(), queuedJobs, failedJobs, request.getCallbackUrl());
                } else if (LECTURE_TYPE_TEXT.equalsIgnoreCase(lectureType)) {
                    processTextLecture(lectureId, lecture.getTitle(), queuedJobs, failedJobs, request.getCallbackUrl());
                } else {
                    failedJobs.add(buildFailedJob(lectureId,
                            "Unsupported lecture type: " + lectureType + ". Only DOCUMENT and TEXT are supported."));
                }

            } catch (Exception e) {
                log.error("Failed to queue job for lecture {}: {}", lectureId, e.getMessage());
                failedJobs.add(buildFailedJob(lectureId, "Failed to queue: " + e.getMessage()));
            }
        }

        log.info("Queued {} jobs, failed {} jobs", queuedJobs.size(), failedJobs.size());

        return DocumentEnrichmentJobResponse.builder()
                .totalRequested(request.getLectureIds().size())
                .successfullyQueued(queuedJobs.size())
                .failed(failedJobs.size())
                .jobs(queuedJobs)
                .failures(failedJobs)
                .build();
    }

    /**
     * Process document lecture - send to SQS with download URL
     */
    private void processDocumentLecture(UUID lectureId, String lectureTitle,
                                        List<DocumentEnrichmentJobResponse.QueuedJob> queuedJobs,
                                        List<DocumentEnrichmentJobResponse.FailedJob> failedJobs,
                                        String customCallbackUrl) {
        Optional<DocumentLecture> optionalDoc = documentLectureRepository.findById(lectureId);
        if (optionalDoc.isEmpty()) {
            failedJobs.add(buildFailedJob(lectureId, "Document lecture not found"));
            return;
        }

        DocumentLecture docLecture = optionalDoc.get();
        String fileUrl = docLecture.getFileUrl();

        if (fileUrl == null || fileUrl.isBlank()) {
            failedJobs.add(buildFailedJob(lectureId, "Document file URL is empty"));
            return;
        }

        String sourceType = determineSourceType(fileUrl);

        try {
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("lectureId", lectureId.toString());
            messageBody.put("lectureType", "DOCUMENT");
            messageBody.put("downloadUrl", fileUrl);
            messageBody.put("sourceType", sourceType);
            messageBody.put("fileFormat", docLecture.getFileFormat());
            messageBody.put("numPages", docLecture.getNumPages());
            messageBody.put("callbackUrl", customCallbackUrl != null ? customCallbackUrl : callbackUrl);
            messageBody.put("lectureTitle", lectureTitle);

            String messageJson = objectMapper.writeValueAsString(messageBody);

            SendMessageRequest sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageJson)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendRequest);

            log.info("Queued document enrichment job for lecture {}: messageId={}", lectureId, response.messageId());

            queuedJobs.add(buildQueuedJob(lectureId, response.messageId(), LECTURE_TYPE_DOCUMENT, sourceType));

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for document lecture {}: {}", lectureId, e.getMessage());
            failedJobs.add(buildFailedJob(lectureId, "Failed to serialize message: " + e.getMessage()));
        }
    }

    /**
     * Process text lecture - send to SQS with text content
     */
    private void processTextLecture(UUID lectureId, String lectureTitle,
                                    List<DocumentEnrichmentJobResponse.QueuedJob> queuedJobs,
                                    List<DocumentEnrichmentJobResponse.FailedJob> failedJobs,
                                    String customCallbackUrl) {
        Optional<TextLecture> optionalText = textLectureRepository.findById(lectureId);
        if (optionalText.isEmpty()) {
            failedJobs.add(buildFailedJob(lectureId, "Text lecture not found"));
            return;
        }

        TextLecture textLecture = optionalText.get();
        String content = textLecture.getContent();

        if (content == null || content.isBlank()) {
            failedJobs.add(buildFailedJob(lectureId, "Text content is empty"));
            return;
        }

        try {
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("lectureId", lectureId.toString());
            messageBody.put("lectureType", LECTURE_TYPE_TEXT);
            messageBody.put("textContent", content);
            messageBody.put("formatType", textLecture.getFormatType());
            messageBody.put("wordCount", textLecture.getWordCount());
            messageBody.put("callbackUrl", customCallbackUrl != null ? customCallbackUrl : callbackUrl);
            messageBody.put("lectureTitle", lectureTitle);

            String messageJson = objectMapper.writeValueAsString(messageBody);

            SendMessageRequest sendRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageJson)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendRequest);

            log.info("Queued text enrichment job for lecture {}: messageId={}", lectureId, response.messageId());

            queuedJobs.add(buildQueuedJob(lectureId, response.messageId(), LECTURE_TYPE_TEXT, "inline"));

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for text lecture {}: {}", lectureId, e.getMessage());
            failedJobs.add(buildFailedJob(lectureId, "Failed to serialize message: " + e.getMessage()));
        }
    }

    /**
     * Determine source type based on file URL
     */
    private String determineSourceType(String fileUrl) {
        if (fileUrl.contains(cloudFrontDomain) || fileUrl.contains("cloudfront.net")) {
            return "cloudfront";
        } else if (fileUrl.contains("s3.amazonaws.com") || fileUrl.contains(".s3.")) {
            return "s3";
        } else {
            return "direct";
        }
    }

    /**
     * Helper method to build FailedJob response
     */
    private DocumentEnrichmentJobResponse.FailedJob buildFailedJob(UUID lectureId, String reason) {
        return DocumentEnrichmentJobResponse.FailedJob.builder()
                .lectureId(lectureId)
                .reason(reason)
                .build();
    }

    /**
     * Helper method to build QueuedJob response
     */
    private DocumentEnrichmentJobResponse.QueuedJob buildQueuedJob(UUID lectureId, String messageId,
                                                                   String lectureType, String sourceType) {
        return DocumentEnrichmentJobResponse.QueuedJob.builder()
                .lectureId(lectureId)
                .messageId(messageId)
                .lectureType(lectureType)
                .sourceType(sourceType)
                .build();
    }
}
