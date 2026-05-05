package com.hcmut.lms.common.util;

public final class StudentGradeUtil {
  private StudentGradeUtil() {}

  public static String convertToLetterGrade(double grade) {
    if (grade >= 9.5) return "A+";
    if (grade >= 8.5) return "A";
    if (grade >= 8.0) return "B+";
    if (grade >= 7.0) return "B";
    if (grade >= 6.5) return "C+";
    if (grade >= 5.5) return "C";
    if (grade >= 5.0) return "D+";
    if (grade >= 4.0) return "D";
    return "F";
  }

  public static double convertTo4Scale(double grade) {
    if (grade >= 8.5) return 4.0;   // A
    if (grade >= 8.0) return 3.5;   // B+
    if (grade >= 7.0) return 3.0;   // B
    if (grade >= 6.5) return 2.5;   // C+
    if (grade >= 5.5) return 2.0;   // C
    if (grade >= 5.0) return 1.5;   // D+
    if (grade >= 4.0) return 1.0;   // D
    return 0.0;
  }
}
