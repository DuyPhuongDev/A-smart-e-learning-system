package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.ChapterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChapterMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classSection", ignore = true)
    @Mapping(target = "lectures", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Chapter toEntity(ChapterRequest dto);
    
    @Mapping(target = "classSectionId", source = "classSection.id")
    ChapterResponse toResponseDTO(Chapter entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classSection", ignore = true)
    @Mapping(target = "lectures", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ChapterRequest dto, @MappingTarget Chapter entity);
}

