package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingType;
import org.springframework.stereotype.Component;

@Component
public class StudentProgressGradeUtil {

  public String convertToLetterGrade(double grade) {
    if (grade >= 9.0) return "A+";
    if (grade >= 8.5) return "A";
    if (grade >= 8.0) return "B+";
    if (grade >= 7.0) return "B";
    if (grade >= 6.5) return "C+";
    if (grade >= 5.5) return "C";
    if (grade >= 5.0) return "D+";
    if (grade >= 4.0) return "D";
    return "F";
  }

  public double convertTo4Scale(double grade) {
    if (grade >= 9.0) return 4.0;
    if (grade >= 8.5) return 3.7;
    if (grade >= 8.0) return 3.5;
    if (grade >= 7.0) return 3.0;
    if (grade >= 6.5) return 2.5;
    if (grade >= 5.5) return 2.0;
    if (grade >= 5.0) return 1.5;
    if (grade >= 4.0) return 1.0;
    return 0.0;
  }

  public Boolean isStudentPassedSubject(StudentEnrollmentResponse enrollment, SubjectGradingType gradingType) {
    if (enrollment == null) {
      return null;
    }

    return switch (gradingType) {
      case GRADED -> enrollment.getFinalGrade() != null ? enrollment.getFinalGrade() >= 4.0 : null;
      case PASS_FAIL -> enrollment.getIsPassed();
      case BOTH -> {
        Boolean isPassed = enrollment.getIsPassed();
        Double grade = enrollment.getFinalGrade();
        if (isPassed != null && isPassed) {
          yield true;
        }
        if (grade != null && grade >= 5.0) {
          yield true;
        }
        if (isPassed != null && !isPassed && grade != null && grade < 5.0) {
          yield false;
        }
        yield null;
      }
    };
  }
}