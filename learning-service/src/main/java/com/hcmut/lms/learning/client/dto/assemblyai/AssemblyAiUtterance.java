package com.hcmut.lms.learning.client.dto.assemblyai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyAiUtterance {

    @JsonProperty("text")
    private String text;

    @JsonProperty("start")
    private Long start;

    @JsonProperty("end")
    private Long end;

    @JsonProperty("confidence")
    private Double confidence;
}
