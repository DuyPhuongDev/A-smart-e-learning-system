package com.hcmut.lms.personalization.application.service.impl.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AcademicCalendarDisplaySupportTest {

    @Test void academicYearFromCode_shouldReturnYear_whenValidCode() {
        assertEquals("2023", AcademicCalendarDisplaySupport.academicYearFromCode("23"));
        assertEquals("2024", AcademicCalendarDisplaySupport.academicYearFromCode("24"));
    }

    @Test void academicYearFromCode_shouldReturnNull_whenNull() {
        assertNull(AcademicCalendarDisplaySupport.academicYearFromCode(null));
    }

    @Test void academicYearFromCode_shouldReturnNull_whenBlank() {
        assertNull(AcademicCalendarDisplaySupport.academicYearFromCode(""));
        assertNull(AcademicCalendarDisplaySupport.academicYearFromCode("  "));
    }

    @Test void academicYearFromCode_shouldReturnNull_whenInvalidNumber() {
        assertNull(AcademicCalendarDisplaySupport.academicYearFromCode("abc"));
    }

    @Test void semesterFromCode_shouldReturnCode_whenValidCode() {
        assertEquals("HK231", AcademicCalendarDisplaySupport.semesterFromCode("HK231"));
    }

    @Test void semesterFromCode_shouldReturnNull_whenNull() {
        assertNull(AcademicCalendarDisplaySupport.semesterFromCode(null));
    }

    @Test void semesterFromCode_shouldReturnNull_whenBlank() {
        assertNull(AcademicCalendarDisplaySupport.semesterFromCode("  "));
    }
}
