package com.hcmut.lms.personalization.application.service.impl.support;

import java.math.BigDecimal;

public final class VectorLiteralFormatter {

  private VectorLiteralFormatter() {
  }

  public static String toVectorLiteral(float[] vector) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < vector.length; i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(BigDecimal.valueOf(vector[i]).toPlainString());
    }
    sb.append(']');
    return sb.toString();
  }
}
