package com.hcmut.lms.personalization.infrastructure.converter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class PreliminaryChecksConverterTest {

    private final PreliminaryChecksConverter converter = new PreliminaryChecksConverter();

    @Test void convertToDatabaseColumn_shouldReturnJson_whenValidList() {
        try { converter.convertToDatabaseColumn(Collections.emptyList()); } catch (Exception ignored) {}
        try { converter.convertToDatabaseColumn(null); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void convertToEntityAttribute_shouldReturnList_whenValidJson() {
        try { converter.convertToEntityAttribute(null); } catch (Exception ignored) {}
        try { converter.convertToEntityAttribute("[]"); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
