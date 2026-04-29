package com.hcmut.lms.personalization.application.service.impl.support;

public final class AcademicCalendarDisplaySupport {

  private AcademicCalendarDisplaySupport() {
  }

  /**
   * Convert a 2-digit year code to a 4-digit display string.
   * E.g., "23" → "2023", "24" → "2024".
   */
  public static String academicYearFromCode(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      int yearSuffix = Integer.parseInt(value.trim());
      return String.valueOf(2000 + yearSuffix);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Return the semester code as-is for display.
   * E.g., "HK231" → "HK231".
   */
  public static String semesterFromCode(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }
}