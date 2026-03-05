package com.hcmut.lms.learning.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.learning.dto.response.TrainingJobResponse;
import com.hcmut.lms.learning.dto.training.TrainingJobCallbackRequest;
import com.hcmut.lms.learning.dto.training.TriggerTrainingRequest;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDataset;
import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import com.hcmut.lms.learning.entity.training.TrainingJob;
import com.hcmut.lms.learning.entity.training.TrainingMetrics;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import com.hcmut.lms.learning.mapper.TrainingJobMapper;
import com.hcmut.lms.learning.repository.GradePredictionDatasetRepository;
import com.hcmut.lms.learning.repository.TrainingJobRepository;
import com.hcmut.lms.learning.repository.TrainingMetricsRepository;
import com.hcmut.lms.learning.service.GradePredictionDatasetService;
import com.hcmut.lms.learning.service.ModelVersionService;
import com.hcmut.lms.learning.service.TrainingJobService;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingJobServiceImpl implements TrainingJobService {

    private final GradePredictionDatasetService datasetService;
    private final GradePredictionDatasetRepository datasetRepository;
    private final TrainingJobRepository trainingJobRepository;
    private final TrainingMetricsRepository trainingMetricsRepository;
    private final TrainingJobMapper trainingJobMapper;
    private final ModelVersionService modelVersionService;
    private final S3Client s3Client;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.sqs.training-job-queue-url}")
    private String trainingQueueUrl;

    @Value("${training.default-model-name:linear_grade_predictor}")
    private String defaultModelName;

    private static final String S3_DATASET_PREFIX = "linear-grade-predictor-model/training-dataset/";
    private static final int BATCH_SIZE = 500;

    @Override
    @Async
    @Transactional
    public CompletableFuture<TrainingJobResponse> triggerTrainingJob(TriggerTrainingRequest request) {
        log.info("Starting training job trigger: datasetVersionId={}",
                request.getDatasetVersionId());

        try {
            // 1. Get dataset version
            GradePredictionDatasetVersion version = getDatasetVersion(request.getDatasetVersionId());
            log.info("Using dataset version: versionNumber={}, totalRows={}",
                    version.getVersionNumber(), version.getTotalRows());

            // 2. Check for concurrent jobs
            checkConcurrentJobs(version.getId());

            // 3. Tự động sinh tên model: {defaultModelName}_{yyyyMMdd_HHmmss}
            String jobId = generateJobId();
            String modelName = generateModelName();

            TrainingJob trainingJob = TrainingJob.builder()
                    .jobId(jobId)
                    .datasetVersionId(version.getId())
                    .modelName(modelName)
                    .s3DatasetUrl("") // Will be set after upload
                    .status(TrainingJob.TrainingJobStatus.PENDING)
                    .description(request.getDescription())
                    .build();

            trainingJob = trainingJobRepository.save(trainingJob);
            log.info("Created training job record: jobId={}, modelName={}, id={}",
                    jobId, modelName, trainingJob.getId());

            // 4. Fetch dataset rows
            List<GradePredictionDataset> datasetRows = datasetRepository.findByVersionId(version.getId());
            log.info("Fetched {} dataset rows for CSV generation", datasetRows.size());

            // 5. Generate CSV
            String csvContent = generateCsvFromDataset(datasetRows);
            log.info("Generated CSV content: {} bytes", csvContent.length());

            // 6. Upload to S3
            String s3DatasetUrl = uploadCsvToS3(csvContent, version.getId(), version.getVersionNumber());
            log.info("Uploaded CSV to S3: {}", s3DatasetUrl);

            // 7. Update training job with S3 URL
            trainingJob.setS3DatasetUrl(s3DatasetUrl);
            trainingJob = trainingJobRepository.save(trainingJob);

            // 8. Send message to SQS (gửi modelName đã sinh để worker dùng đặt tên file)
            String sqsMessageId = sendToSqs(jobId, s3DatasetUrl, modelName);
            log.info("Sent message to SQS: messageId={}", sqsMessageId);

            // 9. Mark job as queued
            trainingJob.markAsQueued();
            trainingJob = trainingJobRepository.save(trainingJob);

            // 10. Build response using mapper
            TrainingJobResponse response = trainingJobMapper.toResponse(trainingJob);

            log.info("Training job triggered successfully: jobId={}, modelName={}", jobId, modelName);
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to trigger training job", e);
            throw new RuntimeException("Failed to trigger training job: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void handleTrainingCallback(TrainingJobCallbackRequest callback) {
        log.info("Handling training callback: jobId={}, status={}", callback.getJobId(), callback.getStatus());

        TrainingJob trainingJob = trainingJobRepository.findByJobId(callback.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Training job not found: " + callback.getJobId()));

        if ("SUCCESS".equalsIgnoreCase(callback.getStatus())) {
            // Mark as completed
            trainingJob.markAsCompleted(callback.getModelS3Path(), callback.getTrainingTimeSeconds());

            // Parse and save metrics
            if (callback.getMetrics() != null) {
                List<TrainingMetrics> metricsList = parseMetrics(trainingJob.getId(), callback.getMetrics());
                trainingMetricsRepository.saveAll(metricsList);
                log.info("Saved {} training metrics", metricsList.size());
            }

            trainingJobRepository.save(trainingJob);
            log.info("Training job completed successfully: jobId={}", callback.getJobId());

            // Tự động tạo ModelVersion cho model vừa được train xong
            try {
                // Lấy description từ request ban đầu nếu có (lưu tạm trong TrainingJob hoặc truyền qua callback)
                // Hiện tại callback không có description, nên để null
                modelVersionService.createFromCompletedJob(trainingJob, callback);
            } catch (Exception e) {
                // Không để lỗi tạo ModelVersion ảnh hưởng tới kết quả callback
                log.error("Failed to create ModelVersion for jobId={}: {}", callback.getJobId(), e.getMessage(), e);
            }

        } else {
            trainingJob.markAsFailed("Training failed with status: " + callback.getStatus());
            trainingJobRepository.save(trainingJob);
            log.warn("Training job failed: jobId={}", callback.getJobId());
        }
    }

    @Override
    @Transactional
    public void handleTrainingError(String jobId, String errorMessage) {
        log.warn("Handling training error: jobId={}, error={}", jobId, errorMessage);

        TrainingJob trainingJob = trainingJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Training job not found: " + jobId));

        trainingJob.markAsFailed(errorMessage);
        trainingJobRepository.save(trainingJob);

        log.info("Training job marked as failed: jobId={}", jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingJob getTrainingJobByJobId(String jobId) {
        return trainingJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Training job not found: " + jobId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingJob> getTrainingJobsByDatasetVersion(UUID datasetVersionId) {
        return trainingJobRepository.findByDatasetVersionId(datasetVersionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingJob> getRecentTrainingJobs(int limit) {
        return trainingJobRepository.findTop20ByOrderByCreatedAtDesc();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helper methods
    // ─────────────────────────────────────────────────────────────────────────

    private GradePredictionDatasetVersion getDatasetVersion(UUID datasetVersionId) {
        if (datasetVersionId != null) {
            return datasetService.getVersionById(datasetVersionId);
        } else {
            return datasetService.getLatestCompletedVersion();
        }
    }

    private void checkConcurrentJobs(UUID datasetVersionId) {
        List<TrainingJob.TrainingJobStatus> activeStatuses = Arrays.asList(
                TrainingJob.TrainingJobStatus.QUEUED,
                TrainingJob.TrainingJobStatus.RUNNING
        );

        boolean hasActiveJob = trainingJobRepository.existsByDatasetVersionIdAndStatusIn(
                datasetVersionId, activeStatuses);

        if (hasActiveJob) {
            throw new IllegalStateException(
                    "A training job is already running for this dataset version. Please wait for it to complete.");
        }
    }

    private String generateJobId() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("TRAIN_%s_%s", timestamp, shortUuid);
    }

    /**
     * Sinh tên model tự động theo công thức: {defaultModelName}_{yyyyMMdd_HHmmss}
     * Ví dụ: linear_grade_predictor_20260305_143022
     */
    private String generateModelName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        return String.format("%s_%s", defaultModelName, timestamp);
    }

    private String generateCsvFromDataset(List<GradePredictionDataset> rows) throws IOException {
        StringWriter writer = new StringWriter();

        String[] headers = {
                "student_id", "semester_id", "course_id",
                "course_grade", "target_gap",
                "sem_credits", "sem_credits_squared", "retake_flg",
                "num_semesters_prior", "has_student_history",
                "cumulative_grade_avg", "previous_sem_grade_avg",
                "grade_trend", "grade_consistency", "historic_fail_ratio",
                "sem_rank_percentile", "gpa_rank_percentile",
                "student_a_plus_grade_rate", "student_a_grade_rate",
                "student_b_plus_grade_rate", "student_b_grade_rate",
                "student_c_plus_grade_rate", "student_c_grade_rate",
                "student_d_plus_grade_rate", "student_d_grade_rate",
                "course_hist_count", "course_hist_median_smooth", "course_hist_missing",
                "rank_course_difficulty", "course_fail_rate",
                "course_a_plus_grade_rate", "course_a_grade_rate",
                "course_b_plus_grade_rate", "course_b_grade_rate",
                "course_c_plus_grade_rate", "course_c_grade_rate",
                "course_d_plus_grade_rate", "course_d_grade_rate",
                "has_relative_course", "relative_avg_course_grade",
                "relative_avg_course_grade_rank_percentile"
        };

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);

            for (GradePredictionDataset row : rows) {
                String[] data = mapRowToCsvArray(row);
                csvWriter.writeNext(data);
            }
        }

        return writer.toString();
    }

    private String[] mapRowToCsvArray(GradePredictionDataset row) {
        return new String[]{
                row.getStudentId().toString(),
                row.getSemesterId().toString(),
                row.getCourseId().toString(),
                toString(row.getCourseGrade()),
                toString(row.getTargetGap()),
                toString(row.getSemCredits()),
                toString(row.getSemCreditsSquared()),
                toString(row.getRetakeFlg()),
                toString(row.getNumSemestersPrior()),
                toString(row.getHasStudentHistory()),
                toString(row.getCumulativeGradeAvg()),
                toString(row.getPreviousSemGradeAvg()),
                toString(row.getGradeTrend()),
                toString(row.getGradeConsistency()),
                toString(row.getHistoricFailRatio()),
                toString(row.getSemRankPercentile()),
                toString(row.getGpaRankPercentile()),
                toString(row.getStudentAPlusGradeRate()),
                toString(row.getStudentAGradeRate()),
                toString(row.getStudentBPlusGradeRate()),
                toString(row.getStudentBGradeRate()),
                toString(row.getStudentCPlusGradeRate()),
                toString(row.getStudentCGradeRate()),
                toString(row.getStudentDPlusGradeRate()),
                toString(row.getStudentDGradeRate()),
                toString(row.getCourseHistCount()),
                toString(row.getCourseHistMedianSmooth()),
                toString(row.getCourseHistMissing()),
                toString(row.getRankCourseDifficulty()),
                toString(row.getCourseFailRate()),
                toString(row.getCourseAPlusGradeRate()),
                toString(row.getCourseAGradeRate()),
                toString(row.getCourseBPlusGradeRate()),
                toString(row.getCourseBGradeRate()),
                toString(row.getCourseCPlusGradeRate()),
                toString(row.getCourseCGradeRate()),
                toString(row.getCourseDPlusGradeRate()),
                toString(row.getCourseDGradeRate()),
                toString(row.getHasRelativeCourse()),
                toString(row.getRelativeAvgCourseGrade()),
                toString(row.getRelativeAvgCourseGradeRankPercentile())
        };
    }

    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }

    private String uploadCsvToS3(String csvContent, UUID versionId, int versionNumber) {
        String timestamp = Instant.now().toString().replaceAll("[:.]+", "-");
        String filename = String.format("dataset_v%d_%s.csv", versionNumber, timestamp);
        String s3Key = S3_DATASET_PREFIX + filename;

        // Check if file already exists
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());

            // File exists, append UUID to make unique
            filename = String.format("dataset_v%d_%s_%s.csv",
                    versionNumber, timestamp, UUID.randomUUID().toString().substring(0, 8));
            s3Key = S3_DATASET_PREFIX + filename;
            log.info("File exists, using unique filename: {}", filename);
        } catch (NoSuchKeyException e) {
            // File doesn't exist, proceed with original name
            log.debug("File doesn't exist, using original filename: {}", filename);
        }

        // Upload to S3
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("text/csv")
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObject(putRequest,
                RequestBody.fromString(csvContent, StandardCharsets.UTF_8));

        return String.format("s3://%s/%s", bucketName, s3Key);
    }

    private String sendToSqs(String jobId, String s3DatasetUrl, String modelName)
            throws JsonProcessingException {

        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("job_id", jobId);
        messageBody.put("s3_dataset_url", s3DatasetUrl);
        messageBody.put("model_name", modelName);
        messageBody.put("timestamp", Instant.now().toString());

        String messageJson = objectMapper.writeValueAsString(messageBody);

        SendMessageRequest sendRequest = SendMessageRequest.builder()
                .queueUrl(trainingQueueUrl)
                .messageBody(messageJson)
                .build();

        SendMessageResponse response = sqsClient.sendMessage(sendRequest);
        return response.messageId();
    }

    private List<TrainingMetrics> parseMetrics(UUID trainingJobId, Map<String, Object> metricsMap) {
        List<TrainingMetrics> metricsList = new ArrayList<>();

        // Parse simplified structure: metrics -> {train/test} -> {baseline/model/error_distribution}
        // Worker now only trains on 4-point scale (0.0-4.0)
        for (String split : Arrays.asList("train", "test")) {
            if (!metricsMap.containsKey(split)) continue;

            @SuppressWarnings("unchecked")
            Map<String, Object> splitMetrics = (Map<String, Object>) metricsMap.get(split);

            // Parse baseline metrics (4-point scale)
            if (splitMetrics.containsKey("baseline")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> baselineMetrics = (Map<String, Object>) splitMetrics.get("baseline");
                metricsList.add(createMetric(trainingJobId, split, "baseline", baselineMetrics));
            }

            // Parse model metrics (4-point scale)
            if (splitMetrics.containsKey("model")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> modelMetrics = (Map<String, Object>) splitMetrics.get("model");
                metricsList.add(createMetric(trainingJobId, split, "model", modelMetrics));
            }

            // Parse error_distribution if present
            if (splitMetrics.containsKey("error_distribution")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> errorDist = (Map<String, Object>) splitMetrics.get("error_distribution");

                TrainingMetrics errorMetric = TrainingMetrics.builder()
                        .trainingJobId(trainingJobId)
                        .metricType(TrainingMetrics.MetricType.model)
                        .datasetSplit(TrainingMetrics.DatasetSplit.valueOf(split))
                        .scaleType(TrainingMetrics.ScaleType.FOUR_POINT)
                        .muError(getDoubleValue(errorDist, "mu_error"))
                        .sigmaError(getDoubleValue(errorDist, "sigma_error"))
                        .sampleCount(getIntegerValue(errorDist, "n"))
                        .build();
                metricsList.add(errorMetric);
            }
        }

        return metricsList;
    }

    private TrainingMetrics createMetric(UUID trainingJobId, String split, String metricType,
                                         Map<String, Object> metrics) {
        return TrainingMetrics.builder()
                .trainingJobId(trainingJobId)
                .metricType(TrainingMetrics.MetricType.valueOf(metricType))
                .datasetSplit(TrainingMetrics.DatasetSplit.valueOf(split))
                .scaleType(TrainingMetrics.ScaleType.FOUR_POINT)  // Always 4-point scale
                .mae(getDoubleValue(metrics, "mae"))
                .rmse(getDoubleValue(metrics, "rmse"))
                .r2(getDoubleValue(metrics, "r2"))
                .build();
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
}
