package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.training.ModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModelVersionRepository extends JpaRepository<ModelVersion, UUID> {

    Optional<ModelVersion> findByVersionName(String versionName);

    List<ModelVersion> findByTrainingJobId(UUID trainingJobId);

    List<ModelVersion> findByDatasetVersionId(UUID datasetVersionId);

    List<ModelVersion> findTop20ByOrderByCreatedAtDesc();

    List<ModelVersion> findByStatusOrderByCreatedAtDesc(ModelVersion.ModelVersionStatus status);

    Optional<ModelVersion> findByIsActiveTrue();

    boolean existsByIsActiveTrue();

    /**
     * Hủy kích hoạt tất cả các version đang active trước khi kích hoạt version mới.
     * Đảm bảo chỉ có duy nhất một version active tại một thời điểm.
     */
    @Modifying
    @Query("UPDATE ModelVersion mv SET mv.isActive = false, mv.status = 'INACTIVE' WHERE mv.isActive = true")
    void deactivateAll();
}
