package com.hcmut.lms.common.util;

public final class SubjectPassUtil {

  private SubjectPassUtil() {}

  /**
   * Evaluates whether a student passed a subject based on grading type.
   *
   * @param isPassed    the explicit pass flag (nullable)
   * @param finalGrade  the numeric final grade (nullable)
   * @param gradingType the grading type string: "GRADED", "PASS_FAIL", or "BOTH"
   * @return {@code true} if passed, {@code false} if failed, {@code null} if not studied / unknown
   */
  public static Boolean evaluateIsPassed(Boolean isPassed, Double finalGrade, String gradingType) {
    if (gradingType == null) {
      gradingType = "GRADED";
    }

    return switch (gradingType) {
      case "PASS_FAIL" -> isPassed;
      case "BOTH" -> {
        if (Boolean.TRUE.equals(isPassed)) {
          yield true;
        }
        if (finalGrade != null && finalGrade >= 4.0) {
          yield true;
        }
        if (finalGrade != null && finalGrade < 4.0) {
          yield false;
        }
        if (Boolean.FALSE.equals(isPassed)) {
          yield false;
        }
        yield null;
      }
      default -> finalGrade != null ? finalGrade >= 4.0 : null;
    };
  }
}
