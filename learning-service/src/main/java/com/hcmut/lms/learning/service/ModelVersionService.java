package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.dto.response.ModelVersionResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.entity.training.ModelVersion;
import com.hcmut.lms.learning.entity.training.TrainingJob;

import java.util.List;
import java.util.UUID;

public interface ModelVersionService {

    /**
     * Tự động tạo ModelVersion khi TrainingJob hoàn thành thành công.
     * Được gọi nội bộ bởi TrainingJobService sau khi xử lý callback SUCCESS.
     *
     * @param trainingJob     Training job đã hoàn thành
     * @param callback        Dữ liệu callback từ Fargate worker
     * @return ModelVersion vừa được tạo
     */
    ModelVersion createFromCompletedJob(TrainingJob trainingJob, TrainingJobCallbackRequest callback);

    /**
     * Lấy danh sách tất cả model version, sắp xếp mới nhất trước.
     */
    List<ModelVersionResponse> getAllModelVersions();

    /**
     * Lấy thông tin một model version theo ID.
     */
    ModelVersionResponse getModelVersionById(UUID id);

    /**
     * Lấy danh sách model version theo training job ID.
     */
    List<ModelVersionResponse> getModelVersionsByTrainingJob(UUID trainingJobId);

    /**
     * Kích hoạt một model version làm version mặc định cho prediction.
     * Tự động hủy kích hoạt version đang active trước đó.
     *
     * @param id ID của model version cần kích hoạt
     * @return ModelVersion sau khi cập nhật
     */
    ModelVersionResponse activateModelVersion(UUID id);

    /**
     * Lưu trữ (archive) một model version, không dùng nữa.
     *
     * @param id ID của model version cần archive
     * @return ModelVersion sau khi cập nhật
     */
    ModelVersionResponse archiveModelVersion(UUID id);

    /**
     * Lấy model version đang active (dùng cho prediction).
     */
    ModelVersionResponse getActiveModelVersion();
}
