package com.hcmut.lms.personalization.infrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hcmut.lms.personalization.application.dto.response.WarningResponse;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class WarningsConverter implements AttributeConverter<List<WarningResponse>, String> {

  private static final TypeReference<List<WarningResponse>> TYPE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<WarningResponse> attribute) {
    return JsonConverterSupport.serialize(attribute);
  }

  @Override
  public List<WarningResponse> convertToEntityAttribute(String dbData) {
    return JsonConverterSupport.deserialize(dbData, TYPE);
  }
}