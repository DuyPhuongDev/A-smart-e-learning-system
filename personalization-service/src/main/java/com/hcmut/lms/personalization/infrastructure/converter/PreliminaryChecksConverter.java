package com.hcmut.lms.personalization.infrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hcmut.lms.personalization.application.dto.response.ValidationCheckResponse;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class PreliminaryChecksConverter implements AttributeConverter<List<ValidationCheckResponse>, String> {

  private static final TypeReference<List<ValidationCheckResponse>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<ValidationCheckResponse> attribute) {
    return JsonConverterSupport.serialize(attribute);
  }

  @Override
  public List<ValidationCheckResponse> convertToEntityAttribute(String dbData) {
    return JsonConverterSupport.deserialize(dbData, TYPE);
  }
}