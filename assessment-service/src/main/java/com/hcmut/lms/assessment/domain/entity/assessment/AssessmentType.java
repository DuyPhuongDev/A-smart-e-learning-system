package com.hcmut.lms.assessment.domain.entity.assessment;

public enum AssessmentType {
    TUTORIAL,
    LABS,
    ASSIGNMENT,
    MIDTERM,
    FINAL,
    EXAM,
    /** Kept for backward-compatibility with existing data; does not map to any grading component. */
    QUIZ,
    /** Practice assessments do not contribute to any grading component. */
    PRACTICE
}
