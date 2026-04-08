package com.hcmut.lms.personalization.application.service.impl.support;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;

import java.util.Map;

public final class IntensityCreditCapSupport {

  private static final int DEFAULT_MAIN_SEMESTER_CAP = 17;
  private static final int DEFAULT_SUMMER_SEMESTER_CAP = 7;

  private static final Map<LearningIntensity, Integer> MAIN_SEMESTER_CAPS = Map.of(
      LearningIntensity.Low, 13,
      LearningIntensity.Light, 15, LearningIntensity.Standard, 17, LearningIntensity.Heavy, 19);

  private static final Map<SummerLearningIntensity, Integer> SUMMER_SEMESTER_CAPS = Map.of(
      SummerLearningIntensity.Light, 4, SummerLearningIntensity.Standard, 9, SummerLearningIntensity.Heavy, 11);

  private IntensityCreditCapSupport() {
  }

  public static int mainSemesterCap(LearningIntensity intensity) {
    return MAIN_SEMESTER_CAPS.getOrDefault(intensity, DEFAULT_MAIN_SEMESTER_CAP);
  }

  public static int summerSemesterCap(SummerLearningIntensity intensity) {
    return SUMMER_SEMESTER_CAPS.getOrDefault(intensity, DEFAULT_SUMMER_SEMESTER_CAP);
  }

  public static int defaultSummerSemesterCap() {
    return DEFAULT_SUMMER_SEMESTER_CAP;
  }
}

