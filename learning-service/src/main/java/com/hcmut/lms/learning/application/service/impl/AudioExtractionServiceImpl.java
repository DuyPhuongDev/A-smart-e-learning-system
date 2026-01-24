package com.hcmut.lms.learning.application.service.impl;

import com.hcmut.lms.learning.application.service.AudioExtractionService;
import com.hcmut.lms.learning.application.util.AutoDeletingTempFile;
import com.hcmut.lms.learning.config.VideoProcessingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of AudioExtractionService using FFmpeg
 * Extracts audio from video files in various formats
 * Returns AutoDeletingTempFile for automatic cleanup
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AudioExtractionServiceImpl implements AudioExtractionService {

    private final VideoProcessingConfig videoProcessingConfig;

    private static final List<String> SUPPORTED_VIDEO_EXTENSIONS = Arrays.asList(
            ".mp4", ".mkv", ".avi", ".mov", ".wmv", ".flv", ".webm", ".m4v", ".3gp"
    );

    private static final List<String> SUPPORTED_AUDIO_EXTENSIONS = Arrays.asList(
            ".mp3", ".wav", ".m4a", ".aac", ".ogg", ".flac", ".wma"
    );

    @Override
    public AutoDeletingTempFile extractAudio(Path videoFilePath) {
        return extractAudio(videoFilePath, "mp3");
    }

    @Override
    public AutoDeletingTempFile extractAudio(Path videoFilePath, String outputFormat) {
        log.info("Extracting audio from video: {} to format: {}", videoFilePath, outputFormat);

        if (videoFilePath == null || !Files.exists(videoFilePath)) {
            throw new IllegalArgumentException("Video file does not exist: " + videoFilePath);
        }

        // Check if input is already an audio file
        String fileName = videoFilePath.getFileName().toString().toLowerCase();
        if (isAudioFile(fileName)) {
            log.info("Input file is already an audio file, wrapping for return");
            // Wrap existing audio file - but mark it to NOT delete since it's the source
            AutoDeletingTempFile wrapper = new AutoDeletingTempFile(videoFilePath);
            wrapper.setKeepOnClose(true); // Don't delete source audio file
            return wrapper;
        }

        AutoDeletingTempFile tempAudioFile = null;
        try {
            // Create temp file for extracted audio with auto-cleanup
            String baseName = getFileBaseName(videoFilePath);
            String prefix = baseName + "_audio_";
            tempAudioFile = new AutoDeletingTempFile(prefix, "." + outputFormat, videoFilePath.getParent());
            Path outputPath = tempAudioFile.getPath();

            // Build FFmpeg command
            ProcessBuilder processBuilder = buildFfmpegCommand(videoFilePath, outputPath, outputFormat);
            processBuilder.redirectErrorStream(true);

            log.info("Running FFmpeg command: {}", String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();

            // Read output for logging
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.debug("FFmpeg: {}", line);
                }
            }

            // Wait for process to complete
            boolean completed = process.waitFor(videoProcessingConfig.getFfmpegTimeoutSeconds(), TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new RuntimeException("FFmpeg audio extraction timed out");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("FFmpeg failed with exit code: {}, output: {}", exitCode, output);
                throw new RuntimeException("FFmpeg failed with exit code: " + exitCode);
            }

            // Verify output file exists
            if (!tempAudioFile.exists()) {
                throw new RuntimeException("FFmpeg output file not found: " + outputPath);
            }

            long fileSize = tempAudioFile.size();
            log.info("Audio extracted successfully to: {}, size: {} bytes", outputPath, fileSize);

            return tempAudioFile;

        } catch (Exception e) {
            // Close temp file on error to trigger cleanup
            if (tempAudioFile != null) {
                tempAudioFile.close();
            }
            log.error("Failed to extract audio: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract audio from video: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isSupported(Path videoFilePath) {
        if (videoFilePath == null) {
            return false;
        }

        String fileName = videoFilePath.getFileName().toString().toLowerCase();
        return SUPPORTED_VIDEO_EXTENSIONS.stream().anyMatch(fileName::endsWith) ||
               SUPPORTED_AUDIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    /**
     * Build FFmpeg command for audio extraction
     */
    private ProcessBuilder buildFfmpegCommand(Path inputPath, Path outputPath, String outputFormat) {
        // FFmpeg command: ffmpeg -i input.mp4 -vn -acodec libmp3lame -q:a 2 output.mp3
        // For high quality audio extraction without video

        String audioCodec;
        String[] additionalArgs;

        switch (outputFormat.toLowerCase()) {
            case "mp3":
                audioCodec = "libmp3lame";
                additionalArgs = new String[]{"-q:a", "2"}; // High quality VBR
                break;
            case "wav":
                audioCodec = "pcm_s16le";
                additionalArgs = new String[]{"-ar", "44100"}; // 44.1kHz sample rate
                break;
            case "m4a":
                audioCodec = "aac";
                additionalArgs = new String[]{"-b:a", "192k"}; // 192kbps bitrate
                break;
            default:
                audioCodec = "libmp3lame";
                additionalArgs = new String[]{"-q:a", "2"};
        }

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(
                videoProcessingConfig.getFfmpegPath(),
                "-i", inputPath.toString(),
                "-vn",                          // No video
                "-acodec", audioCodec,          // Audio codec
                additionalArgs[0], additionalArgs[1],  // Quality/bitrate settings
                "-y",                           // Overwrite output file
                outputPath.toString()
        );

        return pb;
    }

    /**
     * Check if file is already an audio file
     */
    private boolean isAudioFile(String fileName) {
        return SUPPORTED_AUDIO_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    /**
     * Get file base name without extension
     */
    private String getFileBaseName(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }
}
