package com.hcmut.lms.personalization.application.service.impl.support;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;

import java.util.Map;

public final class IntensityCreditCapSupport {

  private static final Map<LearningIntensity, Integer> MAIN_SEMESTER_CAPS = Map.of(
      LearningIntensity.Low, 13,
      LearningIntensity.Light, 15, LearningIntensity.Standard, 17, LearningIntensity.Heavy, 22);

  private static final Map<SummerLearningIntensity, Integer> SUMMER_SEMESTER_CAPS = Map.of(
      SummerLearningIntensity.Light, 4, SummerLearningIntensity.Standard, 6, SummerLearningIntensity.Heavy, 8);

  private IntensityCreditCapSupport() {
  }

  public static int mainSemesterCapStrict(LearningIntensity intensity) {
    if (intensity == null) {
      throw new IllegalArgumentException(
          "prefMainSemLearnIntensity is required for learning path generation but was null");
    }
    return MAIN_SEMESTER_CAPS.get(intensity);
  }

  public static int summerSemesterCapStrict(SummerLearningIntensity intensity) {
    if (intensity == null) {
      throw new IllegalArgumentException(
          "learningIntensity is required for preferred summer semester but was null");
    }
    return SUMMER_SEMESTER_CAPS.get(intensity);
  }
}

