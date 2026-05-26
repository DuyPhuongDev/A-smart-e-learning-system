package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentProgressGradeUtilTest {

    @InjectMocks
    private StudentProgressGradeUtil gradeUtil;

    private StudentEnrollmentResponse enrollment;

    @BeforeEach
    void setUp() {
        enrollment = new StudentEnrollmentResponse();
    }

    @Test
    void isStudentPassedSubject_shouldReturnNull_whenEnrollmentNull() {
        Boolean result = gradeUtil.isStudentPassedSubject(null, SubjectGradingType.GRADED);
        assertNull(result);
    }

    @Test
    void isStudentPassedSubject_shouldReturnTrue_whenGradeAboveThreshold() {
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, SubjectGradingType.GRADED);

        assertTrue(result);
    }

    @Test
    void isStudentPassedSubject_shouldReturnFalse_whenGradeBelowThreshold() {
        enrollment.setFinalGrade(3.0);
        enrollment.setIsPassed(false);

        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, SubjectGradingType.GRADED);

        assertFalse(result);
    }

    @Test
    void isStudentPassedSubject_shouldUsePassFailLogic_whenPassFailType() {
        enrollment.setFinalGrade(3.0);
        enrollment.setIsPassed(true); // Explicitly passed

        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, SubjectGradingType.PASS_FAIL);

        assertTrue(result);
    }

    @Test
    void isStudentPassedSubject_shouldReturnFalse_whenPassFailNotPassed() {
        enrollment.setFinalGrade(3.0);
        enrollment.setIsPassed(false);

        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, SubjectGradingType.PASS_FAIL);

        assertFalse(result);
    }

    @Test
    void isStudentPassedSubject_shouldReturnTrue_whenBothExplicitlyPassed() {
        enrollment.setFinalGrade(3.0);
        enrollment.setIsPassed(true);

        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, SubjectGradingType.BOTH);

        assertTrue(result);
    }

    @Test
    void isStudentPassedSubject_shouldReturnTrue_whenBothGradeAbove4() {
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(null);

        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, SubjectGradingType.BOTH);

        assertTrue(result);
    }

    @Test
    void isStudentPassedSubject_shouldReturnNull_whenNullGradingType() {
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        // null grading type defaults to GRADED
        Boolean result = gradeUtil.isStudentPassedSubject(enrollment, null);

        assertTrue(result);
    }
}
