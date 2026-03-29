package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.entity.dataset.GradePredictionDataset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GradePredictionDatasetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "versionId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromSource(@MappingTarget GradePredictionDataset target, GradePredictionDataset source);
}
