package com.hcmut.lms.coachingchatbot.application.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Auto-deleting temporary file that implements AutoCloseable
 * Use with try-with-resources to automatically cleanup temp files
 *
 * Example usage:
 * 
 * <pre>
 * try (AutoDeletingTempFile tempFile = new AutoDeletingTempFile("video_", ".mp4", tempDir)) {
 *     // Use tempFile.getFile() or tempFile.getPath()
 *     downloadTo(tempFile.getPath());
 * } // File is automatically deleted here
 * </pre>
 */
@Slf4j
public class AutoDeletingTempFile implements AutoCloseable {

    private final File file;
    private final Path path;
    private boolean keepOnClose = false;

    /**
     * Create a temp file in system temp directory
     *
     * @param prefix File name prefix
     * @param suffix File name suffix (extension)
     */
    public AutoDeletingTempFile(String prefix, String suffix) throws IOException {
        this.path = Files.createTempFile(prefix, suffix);
        this.file = path.toFile();
        log.debug("Created temp file: {}", path);
    }

    /**
     * Create a temp file in specified directory
     *
     * @param prefix    File name prefix
     * @param suffix    File name suffix (extension)
     * @param directory Directory to create temp file in
     */
    public AutoDeletingTempFile(String prefix, String suffix, Path directory) throws IOException {
        if (directory != null && Files.exists(directory)) {
            this.path = Files.createTempFile(directory, prefix, suffix);
        } else {
            this.path = Files.createTempFile(prefix, suffix);
        }
        this.file = path.toFile();
        log.debug("Created temp file: {}", path);
    }

    /**
     * Wrap an existing file path for auto-deletion
     *
     * @param existingPath Path to existing file
     */
    public AutoDeletingTempFile(Path existingPath) {
        this.path = existingPath;
        this.file = existingPath.toFile();
        log.debug("Wrapped existing file for auto-deletion: {}", path);
    }

    /**
     * Get the underlying File object
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the file path as Path object
     */
    public Path getPath() {
        return path;
    }

    /**
     * Get the absolute path as String
     */
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    /**
     * Check if file exists
     */
    public boolean exists() {
        return file != null && file.exists();
    }

    /**
     * Get file size in bytes
     */
    public long size() throws IOException {
        return Files.size(path);
    }

    /**
     * Set whether to keep the file on close (don't delete)
     * Useful when you want to conditionally keep the file
     */
    public void setKeepOnClose(boolean keep) {
        this.keepOnClose = keep;
    }

    /**
     * Mark file to be kept (not deleted on close)
     */
    public AutoDeletingTempFile keep() {
        this.keepOnClose = true;
        return this;
    }

    /**
     * Automatically called when exiting try-with-resources block
     * Deletes the temp file
     */
    @Override
    public void close() {
        if (keepOnClose) {
            log.debug("Keeping temp file (keepOnClose=true): {}", path);
            return;
        }

        if (file != null && file.exists()) {
            try {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("Cleaned up temp file: {}", file.getName());
                } else {
                    // Try using Files.delete for better error handling
                    Files.deleteIfExists(path);
                    log.info("Cleaned up temp file (via Files): {}", file.getName());
                }
            } catch (IOException e) {
                log.warn("Failed to delete temp file {}: {}", file.getName(), e.getMessage());
                // Schedule for deletion on JVM exit as fallback
                file.deleteOnExit();
            }
        }
    }

    @Override
    public String toString() {
        return "AutoDeletingTempFile{path=" + path + "}";
    }
}
