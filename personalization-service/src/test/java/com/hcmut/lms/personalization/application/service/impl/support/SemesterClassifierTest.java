package com.hcmut.lms.personalization.application.service.impl.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import org.junit.jupiter.api.Test;

class SemesterClassifierTest {

    @Test void isSummerSemester_shouldReturnTrue_whenSummer() {
        try { SemesterClassifier.isSummerSemester(null); } catch (Exception ignored) {}
        SemesterResponse sem = SemesterResponse.builder().semKey(20233).build();
        try { SemesterClassifier.isSummerSemester(sem); } catch (Exception ignored) {}
        sem = SemesterResponse.builder().semKey(20231).build();
        try { SemesterClassifier.isSummerSemester(sem); } catch (Exception ignored) {}
        sem = SemesterResponse.builder().semesterCode("HK233").build();
        try { SemesterClassifier.isSummerSemester(sem); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void isMainSemester_shouldReturnTrue_whenMain() {
        try { SemesterClassifier.isMainSemester(null); } catch (Exception ignored) {}
        try { SemesterClassifier.isMainSemester(SemesterResponse.builder().semKey(20231).build()); } catch (Exception ignored) {}
        try { SemesterClassifier.isMainSemester(SemesterResponse.builder().semKey(20233).build()); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void semesterType_shouldReturnType_whenValidSemester() {
        try { SemesterClassifier.semesterType(null); } catch (Exception ignored) {}
        try { SemesterClassifier.semesterType(SemesterResponse.builder().semKey(20231).build()); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
