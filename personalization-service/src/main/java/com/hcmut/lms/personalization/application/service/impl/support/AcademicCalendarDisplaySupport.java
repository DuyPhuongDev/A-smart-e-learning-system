package com.hcmut.lms.personalization.application.service.impl.support;

public final class AcademicCalendarDisplaySupport {

  private AcademicCalendarDisplaySupport() {
  }

  public static String academicYearFromCode(String value) {
    if (value == null || !value.matches("\\d{1,2}")) {
      return null; // Or throw a custom exception
    }

    try {
      int yearSuffix = Integer.parseInt(value);
      return String.valueOf(2000 + yearSuffix);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public static String semesterFromCode(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }
}

