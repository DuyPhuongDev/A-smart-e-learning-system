package com.hcmut.lms.learning.client;

import com.hcmut.lms.learning.client.dto.assemblyai.AssemblyAiTranscriptRequest;
import com.hcmut.lms.learning.client.dto.assemblyai.AssemblyAiTranscriptResponse;
import com.hcmut.lms.learning.client.dto.assemblyai.AssemblyAiUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * REST client for AssemblyAI Speech-to-Text API
 * Handles file upload and transcription operations
 */
@Component
@Slf4j
public class AssemblyAiClient {

    private static final String BASE_URL = "https://api.assemblyai.com/v2";
    private static final String UPLOAD_ENDPOINT = "/upload";
    private static final String TRANSCRIPT_ENDPOINT = "/transcript";

    private static final int MAX_POLL_ATTEMPTS = 120; // 10 minutes max (120 * 5 seconds)
    private static final long POLL_INTERVAL_MS = 5000; // 5 seconds

    private final RestTemplate restTemplate;
    private final String apiKey;

    public AssemblyAiClient(
            RestTemplate restTemplate,
            @Value("${assemblyai.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * Upload an audio file to AssemblyAI
     *
     * @param audioFile The audio file to upload
     * @return Upload response containing the upload URL
     */
    public AssemblyAiUploadResponse uploadFile(File audioFile) {
        log.info("Uploading file to AssemblyAI: {}", audioFile.getName());

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        FileSystemResource fileResource = new FileSystemResource(audioFile);
        HttpEntity<FileSystemResource> requestEntity = new HttpEntity<>(fileResource, headers);

        ResponseEntity<AssemblyAiUploadResponse> response = restTemplate.exchange(
                BASE_URL + UPLOAD_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                AssemblyAiUploadResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            log.info("File uploaded successfully. Upload URL obtained.");
            return response.getBody();
        }

        throw new RuntimeException("Failed to upload file to AssemblyAI. Status: " + response.getStatusCode());
    }

    /**
     * Submit a transcription request
     *
     * @param request The transcription request
     * @return Initial transcript response (status will be queued/processing)
     */
    public AssemblyAiTranscriptResponse submitTranscription(AssemblyAiTranscriptRequest request) {
        log.info("Submitting transcription request for audio URL: {}", request.getAudioUrl());

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssemblyAiTranscriptRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<AssemblyAiTranscriptResponse> response = restTemplate.exchange(
                BASE_URL + TRANSCRIPT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                AssemblyAiTranscriptResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            log.info("Transcription submitted. ID: {}, Status: {}",
                    response.getBody().getId(), response.getBody().getStatus());
            return response.getBody();
        }

        throw new RuntimeException("Failed to submit transcription. Status: " + response.getStatusCode());
    }

    /**
     * Get transcript status/result by ID
     *
     * @param transcriptId The transcript ID
     * @return Transcript response
     */
    public AssemblyAiTranscriptResponse getTranscript(String transcriptId) {
        HttpHeaders headers = createHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<AssemblyAiTranscriptResponse> response = restTemplate.exchange(
                BASE_URL + TRANSCRIPT_ENDPOINT + "/" + transcriptId,
                HttpMethod.GET,
                requestEntity,
                AssemblyAiTranscriptResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("Failed to get transcript. Status: " + response.getStatusCode());
    }

    /**
     * Submit transcription and wait for completion (polling)
     *
     * @param request The transcription request
     * @return Completed transcript response
     */
    public AssemblyAiTranscriptResponse transcribeAndWait(AssemblyAiTranscriptRequest request) {
        // Submit the transcription request
        AssemblyAiTranscriptResponse transcript = submitTranscription(request);
        String transcriptId = transcript.getId();

        log.info("Polling for transcription completion. ID: {}", transcriptId);

        // Poll until completion or error
        for (int attempt = 0; attempt < MAX_POLL_ATTEMPTS; attempt++) {
            transcript = getTranscript(transcriptId);

            if (transcript.isCompleted()) {
                log.info("Transcription completed successfully. ID: {}", transcriptId);
                return transcript;
            }

            if (transcript.isError()) {
                String error = transcript.getError() != null ? transcript.getError() : "Unknown error";
                log.error("Transcription failed. ID: {}, Error: {}", transcriptId, error);
                throw new RuntimeException("Transcription failed: " + error);
            }

            // Still processing, wait before next poll
            log.debug("Transcription still processing. ID: {}, Status: {}, Attempt: {}/{}",
                    transcriptId, transcript.getStatus(), attempt + 1, MAX_POLL_ATTEMPTS);

            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Transcription polling interrupted", e);
            }
        }

        throw new RuntimeException("Transcription timed out after " +
                (MAX_POLL_ATTEMPTS * POLL_INTERVAL_MS / 1000) + " seconds. ID: " + transcriptId);
    }

    /**
     * Convenience method: Upload file and transcribe with polling
     * Uses automatic language detection by default
     *
     * @param audioFile The audio file to transcribe
     * @param languageCode Optional language code (e.g., "en_us", "vi"). If null, auto-detection is used.
     * @return Completed transcript response with utterances
     */
    public AssemblyAiTranscriptResponse uploadAndTranscribe(File audioFile, String languageCode) {
        AssemblyAiUploadResponse uploadResponse = uploadFile(audioFile);

        var requestBuilder = AssemblyAiTranscriptRequest.builder()
                .audioUrl(uploadResponse.getUploadUrl());

        // If language is explicitly specified, use it and disable auto-detection
        if (languageCode != null && !languageCode.isBlank()) {
            requestBuilder.languageCode(languageCode);
            requestBuilder.languageDetection(false);
        }

        return transcribeAndWait(requestBuilder.build());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        return headers;
    }
}
