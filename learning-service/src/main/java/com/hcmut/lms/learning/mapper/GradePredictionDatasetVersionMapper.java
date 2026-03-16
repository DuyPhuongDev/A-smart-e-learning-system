package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.response.GradePredictionDatasetVersionResponse;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = InstantMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface GradePredictionDatasetVersionMapper {

    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    GradePredictionDatasetVersionResponse toResponse(GradePredictionDatasetVersion entity);

    List<GradePredictionDatasetVersionResponse> toResponseList(List<GradePredictionDatasetVersion> entities);
}
