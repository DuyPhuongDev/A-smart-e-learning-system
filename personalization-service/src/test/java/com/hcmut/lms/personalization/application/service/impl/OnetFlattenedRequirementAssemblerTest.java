package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.*;

import com.hcmut.lms.personalization.domain.entity.occupationData.DwaReference;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.OnetFlattenedRequirement;
import com.hcmut.lms.personalization.domain.entity.occupationData.TaskStatement;
import com.hcmut.lms.personalization.domain.entity.occupationData.TasksToDwa;
import org.junit.jupiter.api.Test;

class OnetFlattenedRequirementAssemblerTest {

    private final OnetFlattenedRequirementAssembler assembler = new OnetFlattenedRequirementAssembler();

    private static OccupationData makeOccupation(String code) {
        OccupationData o = new OccupationData();
        o.setOnetsocCode(code);
        return o;
    }

    @Test void fromTasks_shouldReturnEmpty_whenEmptyList() {
        List<OnetFlattenedRequirement> result = assembler.fromTasks(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void fromTasks_shouldMapCoreTask_whenCoreType() {
        TaskStatement task = new TaskStatement();
        task.setTaskId(BigDecimal.valueOf(1));
        task.setTask("Analyze requirements");
        task.setTaskType("Core");
        task.setOccupation(makeOccupation("15-1252.00"));

        List<OnetFlattenedRequirement> result = assembler.fromTasks(List.of(task));
        assertNotNull(result);
        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getImportanceScore().compareTo(BigDecimal.valueOf(87.5)) == 0);
    }

    @Test void fromTasks_shouldMapSupplementalTask() {
        TaskStatement task = new TaskStatement();
        task.setTaskId(BigDecimal.valueOf(2));
        task.setTask("Write documentation");
        task.setTaskType("Supplemental");
        task.setOccupation(makeOccupation("15-1252.00"));

        List<OnetFlattenedRequirement> result = assembler.fromTasks(List.of(task));
        assertTrue(result.get(0).getImportanceScore().compareTo(BigDecimal.valueOf(37.5)) == 0);
    }

    @Test void fromDwa_shouldReturnEmpty_whenEmptyList() {
        List<OnetFlattenedRequirement> result = assembler.fromDwa(Collections.emptyList(), new HashMap<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void fromDwa_shouldMapRequirements_whenValidMappings() {
        DwaReference dwa = new DwaReference();
        dwa.setDwaId("DWA1");
        dwa.setDwaTitle("Software Development");

        TaskStatement taskStmt = new TaskStatement();
        taskStmt.setTaskType("Core");

        TasksToDwa mapping = new TasksToDwa();
        mapping.setDwaId("DWA1");
        mapping.setOccupation(makeOccupation("15-1252.00"));
        mapping.setTaskStatement(taskStmt);

        Map<String, DwaReference> dwaById = Map.of("DWA1", dwa);
        List<OnetFlattenedRequirement> result = assembler.fromDwa(List.of(mapping), dwaById);
        assertNotNull(result);
        assertTrue(result.size() == 1);
        assertTrue("DWA".equals(result.get(0).getElementType()));
    }

    @Test void fromTasks_shouldUseDefaultImportance_whenUnknownType() {
        TaskStatement task = new TaskStatement();
        task.setTaskId(BigDecimal.valueOf(3));
        task.setTask("Unknown task");
        task.setTaskType("Other");
        task.setOccupation(makeOccupation("15-1252.00"));

        List<OnetFlattenedRequirement> result = assembler.fromTasks(List.of(task));
        assertTrue(result.get(0).getImportanceScore().compareTo(BigDecimal.valueOf(50.0)) == 0);
    }

    @Test void fromDwa_shouldHandleNullTaskStatement() {
        DwaReference dwa = new DwaReference();
        dwa.setDwaId("DWA2");
        dwa.setDwaTitle("Test DWA");

        TasksToDwa mapping = new TasksToDwa();
        mapping.setDwaId("DWA2");
        mapping.setOccupation(makeOccupation("15-1252.00"));
        // taskStatement is null

        Map<String, DwaReference> dwaById = Map.of("DWA2", dwa);
        List<OnetFlattenedRequirement> result = assembler.fromDwa(List.of(mapping), dwaById);
        assertTrue(result.size() == 1);
    }

    @Test void fromDwa_shouldSkipDwa_whenDwaReferenceNotFound() {
        TasksToDwa mapping = new TasksToDwa();
        mapping.setDwaId("DWA_MISSING");
        mapping.setOccupation(makeOccupation("15-1252.00"));

        List<OnetFlattenedRequirement> result = assembler.fromDwa(List.of(mapping), new HashMap<>());
        assertTrue(result.isEmpty());
    }
}
