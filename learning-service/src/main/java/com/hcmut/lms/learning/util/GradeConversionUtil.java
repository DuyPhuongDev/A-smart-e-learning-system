package com.hcmut.lms.learning.util;

/**
 * Utility class for grade scale conversions.
 * Provides methods to convert between 10-point and 4-point grading scales.
 */
public final class GradeConversionUtil {

    private GradeConversionUtil() {
        // Prevent instantiation
    }

    /**
     * Convert 10-point grade to 4-point scale.
     * <ul>
     *   <li>8.5-10.0 → 4.0 (A)</li>
     *   <li>8.0-8.4 → 3.5 (B+)</li>
     *   <li>7.0-7.9 → 3.0 (B)</li>
     *   <li>6.5-6.9 → 2.5 (C+)</li>
     *   <li>5.5-6.4 → 2.0 (C)</li>
     *   <li>5.0-5.4 → 1.5 (D+)</li>
     *   <li>4.0-4.9 → 1.0 (D)</li>
     *   <li>0.0-3.9 → 0.0 (F)</li>
     * </ul>
     *
     * @param grade10 Grade on 10-point scale
     * @return Grade on 4-point scale
     */
    public static double convertTo4Point(double grade10) {
        if (grade10 >= 8.5) return 4.0;   // A
        if (grade10 >= 8.0) return 3.5;   // B+
        if (grade10 >= 7.0) return 3.0;   // B
        if (grade10 >= 6.5) return 2.5;   // C+
        if (grade10 >= 5.5) return 2.0;   // C
        if (grade10 >= 5.0) return 1.5;   // D+
        if (grade10 >= 4.0) return 1.0;   // D
        return 0.0;                        // F
    }

}
