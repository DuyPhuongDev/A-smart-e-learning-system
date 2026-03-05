package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.response.TrainingJobResponse;
import com.hcmut.lms.learning.dto.response.TrainingMetricsResponse;
import com.hcmut.lms.learning.entity.training.TrainingJob;
import com.hcmut.lms.learning.entity.training.TrainingMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {InstantMapper.class, TrainingMetricsMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainingJobMapper {

    @Mapping(target = "status",      expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "s3ModelPath", source = "s3ModelUrl")
    @Mapping(target = "startedAt",   source = "submittedAt")
    @Mapping(target = "metrics",     source = "metrics", qualifiedByName = "firstMetrics")
    TrainingJobResponse toResponse(TrainingJob entity);

    List<TrainingJobResponse> toResponseList(List<TrainingJob> entities);

    /**
     * Lấy phần tử đầu tiên trong danh sách metrics để đưa vào response.
     * MapStruct sẽ delegate việc convert {@link TrainingMetrics} →
     * {@link TrainingMetricsResponse} cho {@link TrainingMetricsMapper} (khai báo trong uses).
     */
    @Named("firstMetrics")
    default TrainingMetrics firstMetrics(List<TrainingMetrics> metrics) {
        return (metrics != null && !metrics.isEmpty()) ? metrics.get(0) : null;
    }
}
