package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.domain.entity.occupationData.DwaReference;
import com.hcmut.lms.personalization.domain.entity.occupationData.OnetFlattenedRequirement;
import com.hcmut.lms.personalization.domain.entity.occupationData.TaskStatement;
import com.hcmut.lms.personalization.domain.entity.occupationData.TasksToDwa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OnetFlattenedRequirementAssembler {

  public List<OnetFlattenedRequirement> fromTasks(List<TaskStatement> tasks) {
    List<OnetFlattenedRequirement> list = new ArrayList<>();
    for (TaskStatement t : tasks) {
      BigDecimal importance = importanceFromTaskType(t.getTaskType());
      list.add(OnetFlattenedRequirement.builder()
          .occupation(t.getOccupation())
          .elementType("Task")
          .originalId(String.valueOf(t.getTaskId()))
          .contentText(t.getTask())
          .importanceScore(importance)
          .build());
    }
    return list;
  }

  public List<OnetFlattenedRequirement> fromDwa(List<TasksToDwa> mappings, Map<String, DwaReference> dwaById) {
    Map<String, BigDecimal> maxImportanceByDwa = mappings.stream().collect(Collectors.groupingBy(
        TasksToDwa::getDwaId, Collectors.collectingAndThen(
            Collectors.mapping(
                m -> m.getTaskStatement() != null ? m.getTaskStatement().getTaskType() : null,
                Collectors.toList()), this::maxImportanceFromTaskTypes)));

    List<OnetFlattenedRequirement> list = new ArrayList<>();
    for (TasksToDwa m : mappings) {
      DwaReference dwa = dwaById.get(m.getDwaId());
      if (dwa == null) continue;
      BigDecimal imp = maxImportanceByDwa.getOrDefault(m.getDwaId(), BigDecimal.valueOf(50.0));
      list.add(OnetFlattenedRequirement.builder()
          .occupation(m.getOccupation())
          .elementType("DWA")
          .originalId(m.getDwaId())
          .contentText(dwa.getDwaTitle())
          .importanceScore(imp)
          .build());
    }
    return list;
  }

  private BigDecimal importanceFromTaskType(String taskType) {
    if ("Core".equals(taskType)) return BigDecimal.valueOf(87.5);
    if ("Supplemental".equals(taskType)) return BigDecimal.valueOf(37.5);
    return BigDecimal.valueOf(50.0);
  }

  private BigDecimal maxImportanceFromTaskTypes(List<String> taskTypes) {
    BigDecimal max = BigDecimal.ZERO;
    for (String tt : taskTypes) {
      BigDecimal val = importanceFromTaskType(tt);
      if (val.compareTo(max) > 0) {
        max = val;
      }
    }
    return max;
  }
}
