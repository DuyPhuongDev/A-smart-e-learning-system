package com.hcmut.lms.personalization.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

final class JsonConverterSupport {

  static final ObjectMapper MAPPER = new ObjectMapper()
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  private JsonConverterSupport() {}

  static <T> T deserialize(String json, TypeReference<T> typeRef) {
    if (json == null || json.isEmpty()) return null;
    try {
      return MAPPER.readValue(json, typeRef);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("JSON deserialization failed for " + typeRef.getType(), e);
    }
  }

  static String serialize(Object value) {
    if (value == null) return null;
    try {
      return MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("JSON serialization failed", e);
    }
  }
}