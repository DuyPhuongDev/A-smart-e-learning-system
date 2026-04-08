package com.hcmut.lms.personalization.application.service.impl.support;

import com.hcmut.lms.personalization.client.dto.SemesterResponse;

public final class SemesterClassifier {

  private SemesterClassifier() {
  }

  public static boolean isSummerSemester(SemesterResponse semester) {
    if (semester == null) {
      return false;
    }

    Integer semKey = semester.getSemKey();
    if (semKey != null) {
      return Math.abs(semKey) % 10 == 3;
    }

    String semesterCode = semester.getSemesterCode();
    if (semesterCode == null || semesterCode.isBlank()) {
      return false;
    }

    String digits = semesterCode.replaceAll("\\D", "");
    return !digits.isEmpty() && digits.charAt(digits.length() - 1) == '3';
  }
}
