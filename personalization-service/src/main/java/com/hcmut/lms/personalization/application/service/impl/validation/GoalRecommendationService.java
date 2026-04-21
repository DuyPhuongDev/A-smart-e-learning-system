package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.dto.response.RecommendationResponse;
import com.hcmut.lms.personalization.application.dto.response.WarningResponse;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.application.dto.response.enums.RecommendationType;
import com.hcmut.lms.personalization.application.dto.response.enums.WarningSeverity;
import com.hcmut.lms.personalization.application.dto.response.enums.WarningType;
import com.hcmut.lms.personalization.application.service.impl.validation.model.FeasibilityCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalRecommendationService {

  private static final double GOOD_THRESHOLD = 0.50;

  private static final int PRIORITY_GPA = 1;
  private static final int PRIORITY_KNOWLEDGE = 2;
  private static final int PRIORITY_TIME = 3;

  public FeasibilityLevel classifyFeasibility(FeasibilityCheckResult feasibility, double probabilityScore) {
    if (!feasibility.overallPassed()) {
      return FeasibilityLevel.WEAK;
    }
    return probabilityScore >= GOOD_THRESHOLD ? FeasibilityLevel.GOOD : FeasibilityLevel.MEDIUM;
  }

  public List<RecommendationResponse> buildRecommendations(
      FeasibilityLevel feasibilityLevel, LearningGoal goal,
      FeasibilityCheckResult feasibility) {
    int topPriority = resolveTopPriority(goal);
    List<RecommendationResponse> recommendations = new ArrayList<>();

    switch (feasibilityLevel) {
      case GOOD -> addGoodRecommendations(recommendations, topPriority);
      case MEDIUM -> addMediumRecommendations(recommendations, topPriority);
      case WEAK -> addWeakRecommendations(recommendations, topPriority, feasibility);
    }

    return recommendations;
  }

  public List<WarningResponse> buildWarnings(FeasibilityCheckResult feasibility) {
    List<WarningResponse> warnings = new ArrayList<>();

    List<String> atRisk = feasibility.graduationReqResult().atRiskRequirements();
    if (!atRisk.isEmpty()) {
      warnings.add(WarningResponse.builder()
          .type(WarningType.GRADUATION_REQUIREMENT)
          .severity(WarningSeverity.HIGH)
          .message(String.format(
              "Các yêu cầu tốt nghiệp sau chưa có kế hoạch hoàn thành: %s. " + "Sinh viên cần đảm bảo hoàn thành các "
                  + "yêu cầu này trước học kỳ dự kiến tốt nghiệp.",
              String.join(", ", atRisk)))
          .missingRequirements(feasibility.graduationReqResult().atRiskRequirementDetails())
          .missingRequirementIds(atRisk)
          .build());
    }

    List<String> missing = feasibility.graduationReqResult().missingRequirements();
    if (!missing.isEmpty() && atRisk.isEmpty()) {
      warnings.add(WarningResponse.builder()
          .type(WarningType.GRADUATION_REQUIREMENT)
          .severity(WarningSeverity.MEDIUM)
          .message(String.format(
              "Sinh viên vẫn cần hoàn thành: %s. Cần lập kế hoạch thực hiện các yêu cầu này trước khi tốt nghiệp.",
              String.join(", ", missing)))
          .missingRequirements(feasibility.graduationReqResult().missingRequirementDetails())
          .missingRequirementIds(missing)
          .build());
    }

    return warnings;
  }

  private int resolveTopPriority(LearningGoal goal) {
    List<int[]> entries = new ArrayList<>();
    if (goal.getAttemptTargetGpaOrder() != null) {
      entries.add(new int[]{goal.getAttemptTargetGpaOrder(), PRIORITY_GPA});
    }
    if (goal.getCompletedOnTime() != null) {
      entries.add(new int[]{goal.getCompletedOnTime(), PRIORITY_TIME});
    }
    if (goal.getFocusOnTargetOccupation() != null) {
      entries.add(new int[]{goal.getFocusOnTargetOccupation(), PRIORITY_KNOWLEDGE});
    }

    if (entries.isEmpty()) {
      return PRIORITY_GPA;
    }

    int minOrder = entries.stream().mapToInt(e -> e[0]).min().orElse(99);
    return entries.stream()
        .filter(e -> e[0] == minOrder)
        .map(e -> e[1])
        .findFirst()
        .orElse(PRIORITY_GPA);
  }

  private void addGoodRecommendations(List<RecommendationResponse> recs, int topPriority) {
    switch (topPriority) {
      case PRIORITY_GPA -> {
        recs.add(rec(
            1, RecommendationType.MAINTAIN_LOAD,
            "Duy trì khối lượng học tập ở mức vừa phải để tránh quá tải và bảo vệ GPA."));
        recs.add(rec(
            2, RecommendationType.FOCUS_HIGH_CREDIT,
            "Tập trung duy trì kết quả tốt ở các học phần có số tín chỉ cao, vì chúng tác động lớn đến GPA."));
      }
      case PRIORITY_KNOWLEDGE -> {
        recs.add(rec(
            1, RecommendationType.ADD_ELECTIVES,
            "Cân nhắc đăng ký các học phần tự chọn nâng cao hoặc chuyên sâu để mở rộng nền tảng kiến thức."));
        recs.add(rec(
            2, RecommendationType.ACADEMIC_ACTIVITIES,
            "Tham gia các câu lạc bộ học thuật hoặc hoạt động nghiên cứu khi kế hoạch hiện tại đang phù hợp."));
      }
      case PRIORITY_TIME -> {
        recs.add(rec(
            1, RecommendationType.STICK_TO_PLAN,
            "Tuân thủ kế hoạch học tập và hạn chế trì hoãn các học phần bắt buộc để đảm bảo tiến độ tốt nghiệp."));
        recs.add(rec(
            2, RecommendationType.AVOID_LONG_BREAKS,
            "Hạn chế bỏ học kỳ hè hoặc gián đoạn dài vì điều này làm giảm quỹ thời gian dự phòng."));
      }
    }
  }

  private void addMediumRecommendations(List<RecommendationResponse> recs, int topPriority) {
    recs.add(
        rec(
            1, RecommendationType.REVIEW_LOAD,
            "Rà soát khối lượng học kỳ và cân nhắc điều chỉnh số tín chỉ để giảm rủi ro."));
    recs.add(rec(
        2, RecommendationType.USE_SUMMER,
        "Cân nhắc sử dụng các học kỳ hè nhằm phân bổ khối lượng học tập và giảm áp lực học kỳ chính."));
    recs.add(rec(
        3, RecommendationType.PRIORITIZE_CORE,
        "Ưu tiên các học phần cốt lõi và có số tín chỉ cao để tối ưu tác động đến GPA."));

    switch (topPriority) {
      case PRIORITY_GPA -> {
        recs.add(rec(
            4, RecommendationType.REDUCE_LOAD_FOR_GPA,
            "Cân nhắc giảm nhẹ số tín chỉ mỗi học kỳ chính để tăng thời gian tập trung cho từng học phần."));
        recs.add(rec(
            5, RecommendationType.CHOOSE_MANAGEABLE_COURSES,
            "Ưu tiên các học phần có độ khó vừa phải để đảm bảo kết quả ổn định, cải thiện GPA."));
      }
      case PRIORITY_KNOWLEDGE -> {
        recs.add(rec(
            4, RecommendationType.REDUCE_FOR_DEPTH,
            "Giảm nhẹ số tín chỉ để có thời gian nghiên cứu sâu hơn các học phần quan trọng."));
        recs.add(rec(
            5, RecommendationType.CONSIDER_DELAY,
            "Nếu cần thiết, cân nhắc kéo dài thời gian tốt nghiệp thêm một học kỳ để tránh quá tải."));
      }
      case PRIORITY_TIME -> {
        recs.add(rec(
            4, RecommendationType.INCREASE_LOAD,
            "Tăng nhẹ số tín chỉ trong giới hạn cho phép và sử dụng 1–2 học kỳ hè để đảm bảo tiến độ."));
        recs.add(rec(
            5, RecommendationType.ACCEPT_GPA_TRADEOFF,
            "Nếu khoảng cách giữa GPA dự báo và mục tiêu lớn, cân nhắc điều chỉnh mục tiêu GPA để giảm rủi ro."));
      }
    }
  }

  private void addWeakRecommendations(
      List<RecommendationResponse> recs, int topPriority,
      FeasibilityCheckResult feasibility) {
    if (!feasibility.creditTimeResult().passed()) {
      recs.add(rec(
          1, RecommendationType.EXTEND_TIMELINE, String.format(
              "Số tín chỉ còn lại (%d) vượt quá khả năng hoàn thành theo kế hoạch hiện tại (tối đa %d tín chỉ). " +
                  "Cần kéo dài lộ trình tốt nghiệp hoặc tăng cường độ học tập.",
              feasibility.creditTimeResult().remainingCredits(), feasibility.creditTimeResult().maxCredits())));
    }

    if (!feasibility.gpaCheckResult().passed()) {
      recs.add(rec(
          2, RecommendationType.LOWER_GPA_TARGET, String.format(
              "Để đạt GPA mục tiêu %.2f, cần GPA trung bình %.2f cho các học phần còn lại, " + "mức này vượt quá 4" +
                  ".00" + ". Cân nhắc điều chỉnh mục tiêu GPA phù hợp hơn.",
              feasibility.gpaCheckResult().targetGpa(), feasibility.gpaCheckResult().requiredGpa())));
    }

    if (!feasibility.prerequisiteChainResult().passed()) {
      recs.add(rec(
          3, RecommendationType.EXTEND_FOR_PREREQUISITES, String.format(
              "Chuỗi học phần tiên quyết dài nhất (%d học kỳ) vượt quá số học kỳ còn lại (%d). " + "Cần kéo dài tiến "
                  + "độ tốt nghiệp ít nhất %d học kỳ.",
              feasibility.prerequisiteChainResult().longestChain(),
              feasibility.prerequisiteChainResult().availableSemesters(),
              feasibility.prerequisiteChainResult().longestChain() - feasibility.prerequisiteChainResult()
                  .availableSemesters())));
    }

    switch (topPriority) {
      case PRIORITY_GPA -> {
        recs.add(rec(
            4, RecommendationType.EXTEND_REDUCE_PRESSURE,
            "Kéo dài lộ trình tốt nghiệp để giảm áp lực mỗi học kỳ và tăng khả năng đạt GPA mục tiêu."));
        recs.add(rec(
            5, RecommendationType.RECONSIDER_GPA,
            "Nếu không thể kéo dài, cần cân nhắc điều chỉnh mục tiêu GPA theo hướng khả thi hơn."));
      }
      case PRIORITY_KNOWLEDGE -> {
        recs.add(rec(
            4, RecommendationType.REDUCE_LOAD_ACCEPT_DELAY,
            "Giảm số tín chỉ mỗi học kỳ để học sâu hơn và chấp nhận thời gian tốt nghiệp muộn hơn."));
        recs.add(rec(
            5, RecommendationType.FOCUS_CORE,
            "Tập trung vào các học phần nền tảng và cốt lõi thay vì cố gắng hoàn thành quá nhiều cùng lúc."));
      }
      case PRIORITY_TIME -> {
        recs.add(rec(
            4, RecommendationType.MAX_LOAD_AND_SUMMER,
            "Nếu cần giữ mốc tốt nghiệp, tăng số tín chỉ tối đa cho phép và tận dụng toàn bộ học kỳ hè."));
        recs.add(rec(
            5, RecommendationType.LOWER_GPA_FOR_TIME,
            "Cân nhắc giảm mục tiêu GPA để giảm rủi ro rớt môn trong khi vẫn giữ tiến độ."));
      }
    }
  }

  private RecommendationResponse rec(int priority, RecommendationType type, String description) {
    return RecommendationResponse.builder().priority(priority).type(type).description(description).build();
  }
}
