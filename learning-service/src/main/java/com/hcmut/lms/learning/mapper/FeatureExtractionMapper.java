package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.internal.ExtractedFeatures;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for converting ExtractedFeatures DTO to Map for ONNX inference.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FeatureExtractionMapper {

    /**
     * Convert ExtractedFeatures DTO to a Map for ONNX inference.
     * This method builds the feature map matching the training dataset column order.
     */
    default Map<String, Object> toFeatureMap(ExtractedFeatures features) {
        Map<String, Object> featureMap = new HashMap<>();

        // Semester features
        featureMap.put("sem_credits", features.getSemCredits());
        featureMap.put("sem_credits_squared", features.getSemCredits() * features.getSemCredits());
        featureMap.put("retake_no", features.getRetakeNo());

        // Student history features
        featureMap.put("num_semesters_prior", features.getNumSemestersPrior());
        featureMap.put("cumulative_grade_avg", features.getCumulativeGradeAvg());
        featureMap.put("previous_sem_grade_avg", features.getPreviousSemGradeAvg());

        // Subject baseline features
        featureMap.put("subject_hist_median_smooth", features.getSubjectHistMedianSmooth());

        // Relative course features
        featureMap.put("relative_avg_course_grade", features.getRelativeAvgCourseGrade());

        return featureMap;
    }
}
