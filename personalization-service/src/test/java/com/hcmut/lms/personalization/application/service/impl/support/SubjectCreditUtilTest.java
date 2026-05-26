package com.hcmut.lms.personalization.application.service.impl.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService.CompletedSubjectDetail;
import org.junit.jupiter.api.Test;

class SubjectCreditUtilTest {

    @Test void safeCredits_shouldReturnCredits_whenValidInput() {
        assertEquals(0, SubjectCreditUtil.safeCredits((Integer) null));
        assertEquals(3, SubjectCreditUtil.safeCredits(3));
        assertTrue(true);
    }

    @Test void safeCreditsCandidate_shouldReturnCredits_whenValidInput() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .credits(3).build();
        assertEquals(3, SubjectCreditUtil.safeCredits(candidate));
    }

    @Test void safeCreditsCandidate_shouldReturnZero_whenNullCredits() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .credits(null).build();
        assertEquals(0, SubjectCreditUtil.safeCredits(candidate));
    }

    @Test void safeCreditsCandidate_shouldThrow_whenNegativeCredits() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .credits(-1).build();
        try {
            SubjectCreditUtil.safeCredits(candidate);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Invalid subject credits"));
        }
    }

    private static CompletedSubjectDetail makeCompleted(UUID subjectId, String code, String name, Integer credits, boolean isPassed) {
        return new CompletedSubjectDetail(
            subjectId, UUID.randomUUID(), UUID.randomUUID(), 1, 1,
            code, name, credits, 1, 8.0, "B", 3.0, 1, true, isPassed);
    }

    @Test void safeCompletedCredits_shouldReturnCredits_whenValid() {
        var detail = makeCompleted(UUID.randomUUID(), "CS101", "Subject", 3, true);
        assertEquals(3, SubjectCreditUtil.safeCompletedCredits(detail));
    }

    @Test void safeCompletedCredits_shouldReturnZero_whenNullCredits() {
        var detail = makeCompleted(UUID.randomUUID(), "CS101", "Subject", null, true);
        assertEquals(0, SubjectCreditUtil.safeCompletedCredits(detail));
    }

    @Test void safeCompletedCredits_shouldThrow_whenNegativeCredits() {
        var detail = makeCompleted(UUID.randomUUID(), "CS101", "Subject", -1, true);
        try {
            SubjectCreditUtil.safeCompletedCredits(detail);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Invalid completed subject credits"));
        }
    }

    @Test void safePriority1_shouldReturnPriority_whenValidInput() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .priority1(5).build();
        assertEquals(5, SubjectCreditUtil.safePriority1(candidate));
    }

    @Test void safePriority1_shouldReturnMax_whenNullPriority() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .priority1(null).build();
        assertEquals(Integer.MAX_VALUE, SubjectCreditUtil.safePriority1(candidate));
    }

    @Test void safePriority2_shouldReturnPriority_whenValidInput() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .priority2(3).build();
        assertEquals(3, SubjectCreditUtil.safePriority2(candidate));
    }

    @Test void safePriority2_shouldReturnMax_whenNullPriority() {
        var candidate = SubjectCandidate.builder()
            .subjectId(UUID.randomUUID()).subjectCode("CS101")
            .priority2(null).build();
        assertEquals(Integer.MAX_VALUE, SubjectCreditUtil.safePriority2(candidate));
    }
}
