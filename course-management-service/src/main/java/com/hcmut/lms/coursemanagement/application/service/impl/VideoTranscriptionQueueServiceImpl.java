package com.hcmut.lms.coursemanagement.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.coursemanagement.application.dto.request.VideoTranscriptionJobRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.VideoTranscriptionJobResponse;
import com.hcmut.lms.coursemanagement.application.service.VideoTranscriptionQueueService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import com.hcmut.lms.coursemanagement.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.*;

/**
 * Implementation of VideoTranscriptionQueueService
 * Sends video transcription jobs to SQS queue for processing by AWS Fargate workers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoTranscriptionQueueServiceImpl implements VideoTranscriptionQueueService {

    private final SqsClient sqsClient;
    private final VideoLectureRepository videoLectureRepository;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.video-transcription-queue-url}")
    private String queueUrl;

    @Value("${aws.cloud-front.domain}")
    private String cloudFrontDomain;

    @Value("${transcription.callback-url}")
    private String callbackUrl;

    @Override
    public VideoTranscriptionJobResponse queueTranscriptionJobs(VideoTranscriptionJobRequest request) {
        log.info("Queueing {} video transcription jobs", request.getLectureIds().size());

        List<VideoTranscriptionJobResponse.QueuedJob> queuedJobs = new ArrayList<>();
        List<VideoTranscriptionJobResponse.FailedJob> failedJobs = new ArrayList<>();

        for (UUID lectureId : request.getLectureIds()) {
            try {
                // Get video lecture
                Optional<VideoLecture> optionalLecture = videoLectureRepository.findById(lectureId);
                if (optionalLecture.isEmpty()) {
                    failedJobs.add(VideoTranscriptionJobResponse.FailedJob.builder()
                            .lectureId(lectureId)
                            .reason("Video lecture not found")
                            .build());
                    continue;
                }

                VideoLecture lecture = optionalLecture.get();
                String videoUrl = lecture.getVideoUrl();

                if (videoUrl == null || videoUrl.isBlank()) {
                    failedJobs.add(VideoTranscriptionJobResponse.FailedJob.builder()
                            .lectureId(lectureId)
                            .reason("Video URL is empty")
                            .build());
                    continue;
                }

                // Determine source type
                String sourceType = determineSourceType(videoUrl);

                // Build SQS message body
                Map<String, Object> messageBody = new HashMap<>();
                messageBody.put("lectureId", lectureId.toString());
                messageBody.put("videoUrl", videoUrl);  // CloudFront URL directly
                messageBody.put("sourceType", sourceType);
                messageBody.put("callbackUrl", callbackUrl);
                messageBody.put("lectureTitle", lecture.getTitle());

                String messageJson = objectMapper.writeValueAsString(messageBody);

                // Send to SQS
                SendMessageRequest sendRequest = SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(messageJson)
                        .build();

                SendMessageResponse response = sqsClient.sendMessage(sendRequest);

                log.info("Queued transcription job for lecture {}: messageId={}", lectureId, response.messageId());

                queuedJobs.add(VideoTranscriptionJobResponse.QueuedJob.builder()
                        .lectureId(lectureId)
                        .messageId(response.messageId())
                        .sourceType(sourceType)
                        .build());

            } catch (JsonProcessingException e) {
                log.error("Failed to serialize message for lecture {}: {}", lectureId, e.getMessage());
                failedJobs.add(VideoTranscriptionJobResponse.FailedJob.builder()
                        .lectureId(lectureId)
                        .reason("Failed to serialize message: " + e.getMessage())
                        .build());
            } catch (Exception e) {
                log.error("Failed to queue job for lecture {}: {}", lectureId, e.getMessage());
                failedJobs.add(VideoTranscriptionJobResponse.FailedJob.builder()
                        .lectureId(lectureId)
                        .reason("Failed to queue: " + e.getMessage())
                        .build());
            }
        }

        log.info("Queued {} jobs, failed {} jobs", queuedJobs.size(), failedJobs.size());

        return VideoTranscriptionJobResponse.builder()
                .totalRequested(request.getLectureIds().size())
                .successfullyQueued(queuedJobs.size())
                .failed(failedJobs.size())
                .jobs(queuedJobs)
                .failures(failedJobs)
                .build();
    }

    /**
     * Determine source type based on video URL
     */
    private String determineSourceType(String videoUrl) {
        if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
            return "youtube";
        } else if (videoUrl.contains(cloudFrontDomain) || videoUrl.contains("cloudfront.net")) {
            return "cloudfront";
        } else if (videoUrl.contains("s3.amazonaws.com") || videoUrl.contains(".s3.")) {
            return "s3";
        } else {
            return "direct";
        }
    }
}
