package com.hcmut.lms.personalization.application.service.impl.support;

import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService.CompletedSubjectDetail;

public final class SubjectCreditUtil {

  private SubjectCreditUtil() {}

  public static int safeCredits(Integer credits) {
    return credits != null ? credits : 0;
  }

  public static int safeCredits(SubjectCandidate candidate) {
    Integer credits = candidate.getCredits();
    if (credits == null) {
      return 0;
    }
    if (credits < 0) {
      throw new IllegalStateException(
          "Invalid subject credits: subjectId=" + candidate.getSubjectId()
              + ", subjectCode=" + candidate.getSubjectCode()
              + ", credits=" + credits);
    }
    return credits;
  }

  public static int safeCompletedCredits(CompletedSubjectDetail subject) {
    Integer credits = subject.credits();
    if (credits == null) {
      return 0;
    }
    if (credits < 0) {
      throw new IllegalStateException(
          "Invalid completed subject credits: subjectId=" + subject.subjectId()
              + ", subjectCode=" + subject.subjectCode()
              + ", credits=" + credits);
    }
    return credits;
  }

  public static int safePriority1(SubjectCandidate candidate) {
    return candidate.getPriority1() != null ? candidate.getPriority1() : Integer.MAX_VALUE;
  }

  public static int safePriority2(SubjectCandidate candidate) {
    return candidate.getPriority2() != null ? candidate.getPriority2() : Integer.MAX_VALUE;
  }
}