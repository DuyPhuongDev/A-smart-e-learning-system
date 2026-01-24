package com.hcmut.lms.learning.client.dto.assemblyai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyAiTranscriptRequest {

    @JsonProperty("audio_url")
    private String audioUrl;

    @JsonProperty("language_code")
    private String languageCode;

    @JsonProperty("punctuate")
    @Builder.Default
    private Boolean punctuate = true;

    @JsonProperty("format_text")
    @Builder.Default
    private Boolean formatText = true;

    @JsonProperty("language_detection")
    @Builder.Default
    private Boolean languageDetection = true;
}
