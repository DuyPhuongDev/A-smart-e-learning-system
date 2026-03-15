package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.internal.PredictionResult;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {InstantMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GradePredictionMapper {

    @Mapping(target = "isActualGrade", constant = "false")
    @Mapping(target = "actualGrade", ignore = true)
    @Mapping(target = "confidenceIntervalWidth", expression = "java(result.getUpperBound95() - result.getLowerBound95())")
    @Mapping(target = "predictionTimestamp", expression = "java(java.time.Instant.now())")
    GradePredictionResponse toResponse(PredictionResult result);
}
