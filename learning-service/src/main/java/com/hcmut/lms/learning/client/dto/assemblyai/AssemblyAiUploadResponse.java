package com.hcmut.lms.learning.client.dto.assemblyai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response from AssemblyAI file upload endpoint
 * POST /v2/upload
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyAiUploadResponse {

    @JsonProperty("upload_url")
    private String uploadUrl;
}
