package com.hcmut.lms.personalization.application.service.impl.support;

import java.util.Locale;

public final class IntensityParsingSupport {

  private IntensityParsingSupport() {
  }

  public static String normalizeTrimmedTitleCase(String value) {
    if (value == null || value.isBlank()) {
      return value;
    }
    String lower = value.trim().toLowerCase(Locale.ROOT);
    return lower.substring(0, 1).toUpperCase(Locale.ROOT) + lower.substring(1);
  }

  public static <E extends Enum<E>> E parseRequiredTrimmedTitleCase(Class<E> enumClass, String value) {
    return Enum.valueOf(enumClass, normalizeTrimmedTitleCase(value));
  }

  public static <E extends Enum<E>> E parseNullableUntrimmedTitleCase(Class<E> enumClass, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String normalized = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    return Enum.valueOf(enumClass, normalized);
  }
}


