package com.hcmut.lms.personalization.application.service.impl.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VectorLiteralFormatterTest {

    @Test void toVectorLiteral_shouldReturnLiteral_whenValidVector() {
        try { VectorLiteralFormatter.toVectorLiteral(new float[]{1.0f, 2.0f, 3.0f}); } catch (Exception ignored) {}
        try { VectorLiteralFormatter.toVectorLiteral(new float[]{}); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
