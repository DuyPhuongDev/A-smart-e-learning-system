package com.hcmut.lms.coachingchatbot.client.dto.assemblyai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Word object with timing information from AssemblyAI
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyAiWord {

    @JsonProperty("text")
    private String text;

    @JsonProperty("start")
    private Long start;

    @JsonProperty("end")
    private Long end;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("speaker")
    private String speaker;
}
