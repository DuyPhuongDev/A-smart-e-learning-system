package com.hcmut.lms.coursemanagement.util;

import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;

public class SemesterUtil {

  /**
   * Compute semKey from a semester code string.
   * <p>
   * Expected format: {@code HK(YY)(S)} where YY is 2-digit year and S is semester number.
   * Examples: HK231 → 20231, HK242 → 20242, HK253 → 20253
   *
   * @param semester the semester entity (e.g., "HK231")
   * @return semKey integer, or null if the code is null or malformed
   */
  public static Integer computeSemKey(Semester semester) {
    return computeSemKeyFromSemesterCode(semester != null ? semester.getSemesterCode() : null);
  }

  public static Integer computeSemKeyFromSemesterCode(String semesterCode) {
    if (semesterCode == null) {
      return null;
    }

    // Expected format HK(YY)(S) requires at least 5 characters (e.g., HK231)
    if (semesterCode.length() < 5 || !semesterCode.startsWith("HK")) {
      return null;
    }

    try {
      // Extract YY: indices 2 and 3 (e.g., "23")
      String yearPart = semesterCode.substring(2, 4);
      // Extract S: index 4 onwards (e.g., "1")
      String semPart = semesterCode.substring(4);

      int yy = Integer.parseInt(yearPart);
      int semNum = Integer.parseInt(semPart);

      // Standardize to 4-digit year assuming 21st century (2000s)
      int startYear = 2000 + yy;

      return (startYear * 10) + semNum;
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      return null;
    }
  }
}
