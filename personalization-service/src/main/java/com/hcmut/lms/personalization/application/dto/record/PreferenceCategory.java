package com.hcmut.lms.personalization.application.dto.record;

import java.util.Map;
import java.util.UUID;

public record PreferenceCategory(String key, int order, Map<UUID, Double> scores) {}
