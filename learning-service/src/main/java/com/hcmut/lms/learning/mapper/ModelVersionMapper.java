package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.response.ModelVersionResponse;
import com.hcmut.lms.learning.entity.training.ModelVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {InstantMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ModelVersionMapper {

    @Mapping(target = "status",   expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "jobId",    source = "trainingJob.jobId")
    ModelVersionResponse toResponse(ModelVersion entity);

    List<ModelVersionResponse> toResponseList(List<ModelVersion> entities);
}
