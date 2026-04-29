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

  public static boolean isMainSemester(SemesterResponse semester) {
    if (semester == null || semester.getSemKey() == null) {
      return false;
    }
    int t = Math.abs(semester.getSemKey()) % 10;
    return t == 1 || t == 2;
  }

  /**
   * Returns the semester type from the semester's semKey.
   * 1 = HK1 (Fall), 2 = HK2 (Spring), 3 = HK3 (Summer), 0 = unknown.
   */
  public static int semesterType(SemesterResponse semester) {
    if (semester == null || semester.getSemKey() == null) {
      return 0;
    }
    return Math.abs(semester.getSemKey()) % 10;
  }
}
