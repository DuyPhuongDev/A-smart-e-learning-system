package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LearningProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "progressPercentage", ignore = true)
    @Mapping(target = "completedAt", expression = "java(request.getIsCompleted() != null && request.getIsCompleted() ? java.time.Instant.now() : null)")
    LearningProgress toEntity(LearningProgressRequest request);

    LearningProgressResponse toResponse(LearningProgress learningProgress);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "progressPercentage", ignore = true) // BE tự tính
    @Mapping(target = "completedAt",
            expression = "java(request.getIsCompleted() != null && request.getIsCompleted() ? java.time.Instant.now() : learningProgress.getCompletedAt())")
    void updateEntityFromRequest(
            LearningProgressRequest request,
            @MappingTarget LearningProgress learningProgress
    );
}
