package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.util.SubjectPassUtil;
import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingType;
import org.springframework.stereotype.Component;

@Component
public class StudentProgressGradeUtil {

  public Boolean isStudentPassedSubject(StudentEnrollmentResponse enrollment, SubjectGradingType gradingType) {
    if (enrollment == null) {
      return null;
    }
    return SubjectPassUtil.evaluateIsPassed(
        enrollment.getIsPassed(), enrollment.getFinalGrade(),
        gradingType != null ? gradingType.name() : null);
  }
}