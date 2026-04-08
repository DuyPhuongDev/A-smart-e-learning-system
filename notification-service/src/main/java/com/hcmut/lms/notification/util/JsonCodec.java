package com.hcmut.lms.notification.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.notification.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JsonCodec {

    private final ObjectMapper objectMapper;

    public String toJsonString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON payload");
        }
    }

    public JsonNode toJsonNode(String value) {
        try {
            return objectMapper.readTree(value == null ? "{}" : value);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid persisted JSON payload");
        }
    }

    public Set<String> toStringSet(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<Set<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid persisted channels payload");
        }
    }

    public Map<String, Object> toMap(JsonNode node) {
        if (node == null || node.isNull()) {
            return Map.of();
        }
        return objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {
        });
    }
}
