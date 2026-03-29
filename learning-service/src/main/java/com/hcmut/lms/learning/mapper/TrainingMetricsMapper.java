package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.response.TrainingMetricsResponse;
import com.hcmut.lms.learning.entity.training.TrainingMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = InstantMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainingMetricsMapper {

    @Mapping(target = "metricType",   expression = "java(entity.getMetricType()   != null ? entity.getMetricType().name()   : null)")
    @Mapping(target = "datasetSplit", expression = "java(entity.getDatasetSplit() != null ? entity.getDatasetSplit().name() : null)")
    @Mapping(target = "scaleType",    expression = "java(entity.getScaleType()    != null ? entity.getScaleType().name()    : null)")
    TrainingMetricsResponse toResponse(TrainingMetrics entity);
}
