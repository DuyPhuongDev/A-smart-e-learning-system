package com.hcmut.lms.learning.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CachedModel {
    private UUID versionId;
    private String versionName;
    private String localOnnxPath;
    private String localMetadataPath;
    private List<String> featureColumns;
    private ModelMetrics metrics;
    private Instant cachedAt;
}
