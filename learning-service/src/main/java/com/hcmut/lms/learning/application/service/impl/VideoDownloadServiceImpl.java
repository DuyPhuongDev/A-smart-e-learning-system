package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.VideoDownloadService;
import com.hcmut.lms.learning.application.util.AutoDeletingTempFile;
import com.hcmut.lms.learning.config.VideoProcessingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Implementation of VideoDownloadService
 * Downloads videos from S3 (via pre-signed URL) and YouTube (via yt-dlp)
 * Returns AutoDeletingTempFile for automatic cleanup
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoDownloadServiceImpl implements VideoDownloadService {

    private final VideoProcessingConfig videoProcessingConfig;

    @Override
    public AutoDeletingTempFile downloadFromS3(UUID lectureId, String presignedUrl) {
        log.info("Downloading video from S3 for lecture: {}", lectureId);

        AutoDeletingTempFile tempFile = null;
        try {
            // Create temp file with auto-cleanup
            String prefix = "video_" + lectureId + "_";
            tempFile = new AutoDeletingTempFile(prefix, ".mp4", videoProcessingConfig.getTempDirPath());
            Path outputPath = tempFile.getPath();

            // Download file using HTTP connection
            URL url = URI.create(presignedUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(videoProcessingConfig.getDownloadTimeoutSeconds() * 1000);
            connection.setReadTimeout(videoProcessingConfig.getDownloadTimeoutSeconds() * 1000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to download video from S3, HTTP code: " + responseCode);
            }

            // Copy stream to file
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }

            long fileSize = tempFile.size();
            log.info("Downloaded S3 video to: {}, size: {} bytes", outputPath, fileSize);

            return tempFile;

        } catch (Exception e) {
            // Close temp file on error to trigger cleanup
            if (tempFile != null) {
                tempFile.close();
            }
            log.error("Failed to download video from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download video from S3: " + e.getMessage(), e);
        }
    }

    @Override
    public AutoDeletingTempFile downloadFromYouTube(UUID lectureId, String youtubeUrl) {
        log.info("Downloading video from YouTube for lecture: {}", lectureId);

        try {
            // Create output file path (yt-dlp will create the actual file)
            String fileNameBase = "video_" + lectureId + "_" + System.currentTimeMillis();
            Path outputTemplate = videoProcessingConfig.getTempDirPath().resolve(fileNameBase + ".%(ext)s");

            // Build yt-dlp command
            ProcessBuilder processBuilder = new ProcessBuilder(
                    videoProcessingConfig.getYtDlpPath(),
                    "-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best",
                    "--merge-output-format", "mp4",
                    "-o", outputTemplate.toString(),
                    "--no-playlist",
                    "--socket-timeout", String.valueOf(videoProcessingConfig.getDownloadTimeoutSeconds()),
                    youtubeUrl
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Read output for logging
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.debug("yt-dlp: {}", line);
                }
            }

            // Wait for process to complete
            boolean completed = process.waitFor(videoProcessingConfig.getDownloadTimeoutSeconds(), TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new RuntimeException("yt-dlp download timed out");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("yt-dlp failed with exit code: {}, output: {}", exitCode, output);
                throw new RuntimeException("yt-dlp failed with exit code: " + exitCode);
            }

            // Find the downloaded file (yt-dlp might use different extension)
            Path downloadedFile = findDownloadedFile(videoProcessingConfig.getTempDirPath(), fileNameBase);
            if (downloadedFile == null || !Files.exists(downloadedFile)) {
                throw new RuntimeException("Downloaded file not found");
            }

            // Wrap in AutoDeletingTempFile for cleanup
            AutoDeletingTempFile tempFile = new AutoDeletingTempFile(downloadedFile);
            long fileSize = tempFile.size();
            log.info("Downloaded YouTube video to: {}, size: {} bytes", downloadedFile, fileSize);

            return tempFile;

        } catch (Exception e) {
            log.error("Failed to download video from YouTube: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download video from YouTube: " + e.getMessage(), e);
        }
    }

    @Override
    public AutoDeletingTempFile downloadVideo(UUID lectureId, String url, String sourceType) {
        return switch (sourceType.toLowerCase()) {
            case "s3" -> downloadFromS3(lectureId, url);
            case "youtube" -> downloadFromYouTube(lectureId, url);
            case "direct" -> downloadFromS3(lectureId, url); // Direct URLs handled same as S3
            default -> throw new IllegalArgumentException("Unsupported source type: " + sourceType);
        };
    }

    /**
     * Find downloaded file by base name (handles different extensions)
     */
    private Path findDownloadedFile(Path directory, String fileNameBase) throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(path -> path.getFileName().toString().startsWith(fileNameBase))
                    .findFirst()
                    .orElse(null);
        }
    }
}
