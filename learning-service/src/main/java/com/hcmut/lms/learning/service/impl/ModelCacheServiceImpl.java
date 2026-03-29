package com.hcmut.lms.learning.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.learning.dto.internal.CachedModel;
import com.hcmut.lms.learning.dto.internal.ModelMetrics;
import com.hcmut.lms.learning.entity.training.ModelVersion;
import com.hcmut.lms.learning.exception.ModelNotAvailableException;
import com.hcmut.lms.learning.repository.ModelVersionRepository;
import com.hcmut.lms.learning.service.ModelCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelCacheServiceImpl implements ModelCacheService {

    private final S3Client s3Client;
    private final ModelVersionRepository modelVersionRepository;

    @Value("${model.cache.directory}")
    private String cacheDirectory;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private volatile CachedModel currentModel;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @PostConstruct
    public void initialize() {
        File cacheDir = new File(cacheDirectory);
        if (!cacheDir.exists()) {
            boolean created = cacheDir.mkdirs();
            if (created) {
                log.info("Created model cache directory: {}", cacheDirectory);
            }
        }
    }

    @Override
    public CachedModel getActiveModel() {
        // Read lock for checking
        lock.readLock().lock();
        try {
            ModelVersion activeVersion = modelVersionRepository.findByIsActiveTrue()
                    .orElseThrow(() -> new ModelNotAvailableException("No active model found in database"));

            // Check if cached model matches active version
            if (currentModel != null && currentModel.getVersionId().equals(activeVersion.getId())) {
                log.debug("Using cached model: {} (version {})", activeVersion.getVersionName(), activeVersion.getId());
                return currentModel;
            }
        } finally {
            lock.readLock().unlock();
        }

        // Write lock for downloading/updating
        lock.writeLock().lock();
        try {
            // Double-check after acquiring write lock
            ModelVersion activeVersion = modelVersionRepository.findByIsActiveTrue()
                    .orElseThrow(() -> new ModelNotAvailableException("No active model found in database"));

            if (currentModel != null && currentModel.getVersionId().equals(activeVersion.getId())) {
                return currentModel;
            }

            cleanupOutdatedModelFiles(activeVersion);

            // Download and cache new model
            log.info("Downloading active model: {} (version {})",
                    activeVersion.getVersionName(), activeVersion.getId());
            currentModel = downloadAndCacheModel(activeVersion);
            return currentModel;

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void reloadActiveModel() {
        lock.writeLock().lock();
        try {
            ModelVersion activeVersion = modelVersionRepository.findByIsActiveTrue()
                    .orElseThrow(() -> new ModelNotAvailableException("No active model found in database"));

            cleanupOutdatedModelFiles(activeVersion);

            log.info("Force reloading active model: {} (version {})",
                    activeVersion.getVersionName(), activeVersion.getId());
            currentModel = downloadAndCacheModel(activeVersion);

        } finally {
            lock.writeLock().unlock();
        }
    }

    private void cleanupOutdatedModelFiles(ModelVersion activeVersion) {
        try {
            String s3Path = activeVersion.getS3ModelPath();
            String[] filenames = deriveFilenamesFromS3Path(s3Path);
            String activeOnnx = filenames[0];
            String activeMetadata = filenames[1];

            Set<String> cachedFiles = listCachedModelFiles();
            for (String fileName : cachedFiles) {
                if (fileName.equals(activeOnnx) || fileName.equals(activeMetadata)) {
                    continue;
                }
                Path target = Paths.get(cacheDirectory, fileName);
                try {
                    Files.deleteIfExists(target);
                    log.info("Removed outdated cached model file: {}", target);
                } catch (IOException e) {
                    log.warn("Failed to remove cached model file {}: {}", target, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup outdated cached model files: {}", e.getMessage());
        }
    }

    private String[] deriveFilenamesFromS3Path(String s3Path) {
        if (s3Path == null || !s3Path.startsWith("s3://")) {
            throw new ModelNotAvailableException("Invalid S3 path format: " + s3Path);
        }
        String s3Key = s3Path.replace("s3://" + bucketName + "/", "");
        String onnxFilename = s3Key.substring(s3Key.lastIndexOf('/') + 1);
        String metadataFilename = onnxFilename.replace(".onnx", "_metadata.json");
        return new String[]{onnxFilename, metadataFilename};
    }

    private Set<String> listCachedModelFiles() {
        try (Stream<Path> stream = Files.list(Path.of(cacheDirectory))) {
            return stream
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(".onnx") || name.endsWith("_metadata.json"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.warn("Failed to list cached model files: {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    private CachedModel downloadAndCacheModel(ModelVersion version) {
        try {
            // Parse S3 path: s3://bucket/key
            String s3Path = version.getS3ModelPath();
            if (!s3Path.startsWith("s3://")) {
                throw new ModelNotAvailableException("Invalid S3 path format: " + s3Path);
            }

            String s3Key = s3Path.replace("s3://" + bucketName + "/", "");

            // Derive filenames for this version
            String[] filenames = deriveFilenamesFromS3Path(s3Path);
            String onnxFilename = filenames[0];
            String metadataFilename = filenames[1];

            // Download .onnx file
            String localOnnxPath = cacheDirectory + File.separator + onnxFilename;
            downloadFromS3(s3Key, localOnnxPath);

            // Download metadata.json
            String metadataS3Key = s3Key.replace(".onnx", "_metadata.json");
            String localMetadataPath = cacheDirectory + File.separator + metadataFilename;
            downloadFromS3(metadataS3Key, localMetadataPath);

            // Parse metadata
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> metadata = mapper.readValue(new File(localMetadataPath), Map.class);
            List<String> featureColumns = (List<String>) metadata.get("feature_cols");
            ModelMetrics metrics = mapper.convertValue(metadata.get("metrics"), ModelMetrics.class);

            log.info("Successfully cached model: {} with {} features",
                    version.getVersionName(), featureColumns.size());

            return new CachedModel(
                    version.getId(),
                    version.getVersionName(),
                    localOnnxPath,
                    localMetadataPath,
                    featureColumns,
                    metrics,
                    Instant.now()
            );

        } catch (IOException e) {
            throw new ModelNotAvailableException("Failed to download or parse model from S3", e);
        } catch (Exception e) {
            throw new ModelNotAvailableException("Unexpected error while caching model", e);
        }
    }

    private void downloadFromS3(String s3Key, String localPath) throws IOException {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            java.nio.file.Path target = Paths.get(localPath);
            Files.createDirectories(target.getParent());
            Files.deleteIfExists(target);

            s3Client.getObject(request, ResponseTransformer.toFile(target));
            log.info("Downloaded: {} -> {}", s3Key, localPath);

        } catch (Exception e) {
            throw new IOException("Failed to download from S3: " + s3Key, e);
        }
    }
}
