package com.hcmut.lms.learning.client.dto.assemblyai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyAiTranscriptResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("text")
    private String text;

    @JsonProperty("words")
    private List<AssemblyAiWord> words;


    @JsonProperty("audio_duration")
    private Integer audioDuration;

    @JsonProperty("error")
    private String error;

    @JsonProperty("language_code")
    private String languageCode;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("language_confidence")
    private Double languageConfidence;

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    public boolean isError() {
        return "error".equalsIgnoreCase(status);
    }

    public boolean isProcessing() {
        return "queued".equalsIgnoreCase(status) || "processing".equalsIgnoreCase(status);
    }
}
