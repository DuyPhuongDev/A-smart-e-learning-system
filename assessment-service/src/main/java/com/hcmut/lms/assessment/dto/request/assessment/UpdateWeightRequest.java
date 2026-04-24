package com.hcmut.lms.assessment.dto.request.assessment;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Setter
@Getter
public class UpdateWeightRequest {
    @Range(min = 0, max = 100, message = "Value must be in range from 0% to 100%")
    private BigDecimal weight;
}
