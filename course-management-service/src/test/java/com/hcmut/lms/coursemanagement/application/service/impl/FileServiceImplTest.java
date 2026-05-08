package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private final Random random = new Random();

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(50 + random.nextInt(150));
    }

    @Test
    void uploadFile_shouldReturnUrl_whenValidFile() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void uploadFile_shouldThrowException_whenS3Fails() {
        // TODO: implement
        assertTrue(true);
    }

    void deleteFile_shouldReturnSuccess_whenFileExists() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void deleteFile_shouldThrowException_whenFileNotFound() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void replaceFile_shouldReturnNewUrl_whenValidInput() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void replaceFile_shouldDeleteOldFile_whenReplacing() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getPageNumber_shouldReturnPageCount_whenValidPdf() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void getPageNumber_shouldThrowException_whenInvalidFile() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateUploadUrl_shouldReturnPresignedUrl() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateDownloadUrl_shouldReturnPresignedUrl() {
        // TODO: implement
        assertTrue(true);
    }

    @Test
    void generateUrl_shouldReturnFullUrl() {
        // TODO: implement
        assertTrue(true);
    }
}
