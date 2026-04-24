package com.hcmut.lms.personalization.infrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hcmut.lms.personalization.application.dto.response.RecommendationResponse;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class RecommendationsConverter implements AttributeConverter<List<RecommendationResponse>, String> {

  private static final TypeReference<List<RecommendationResponse>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<RecommendationResponse> attribute) {
    return JsonConverterSupport.serialize(attribute);
  }

  @Override
  public List<RecommendationResponse> convertToEntityAttribute(String dbData) {
    return JsonConverterSupport.deserialize(dbData, TYPE);
  }
}