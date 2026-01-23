package com.hcmut.lms.learning.domain.entity.lectureKnowledge;

import java.util.Arrays;

public enum ContentType {
    VIDEO_YOUTUBE,
    VIDEO_S3,
    DOCUMENT_PDF,
    DOCUMENT_DOCX,
    DOCUMENT_PPTX,
    TEXT_CONTENT,
    TEXT_HTML,
    TEXT_MARKDOWN;

    /**
     * Get the string value to store in a database
     */
    public String getValue() {
        return this.name();
    }

    public static ContentType fromLectureType(String lectureType, String videoUrl, String fileFormat) {
        if (lectureType == null) {
            return TEXT_CONTENT;
        }

        return switch (lectureType.toUpperCase()) {
            case "VIDEO" -> resolveVideoType(videoUrl);
            case "DOCUMENT" -> resolveDocumentType(fileFormat);
            default -> TEXT_CONTENT;
        };
    }

    private static ContentType resolveVideoType(String videoUrl) {
        if (videoUrl != null && (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be"))) {
            return VIDEO_YOUTUBE;
        }
        return VIDEO_S3;
    }

    private static ContentType resolveDocumentType(String fileFormat) {
        if (fileFormat == null) {
            return DOCUMENT_PDF; // Default fallback
        }

        String format = fileFormat.trim().toLowerCase();

        if (isFormat(format, "docx", "doc")) return DOCUMENT_DOCX;
        if (isFormat(format, "pptx", "ppt")) return DOCUMENT_PPTX;

        return DOCUMENT_PDF;
    }

    // Util function to check if input matches any valid formats
    private static boolean isFormat(String input, String... validFormats) {
        return Arrays.asList(validFormats).contains(input);
    }
}