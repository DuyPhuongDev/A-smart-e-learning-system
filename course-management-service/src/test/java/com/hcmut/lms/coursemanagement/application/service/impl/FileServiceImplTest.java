package com.hcmut.lms.coursemanagement.application.service.impl;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private FileServiceImpl fileService;

    private static final String BUCKET = "test-bucket";
    private static final String CLOUD_FRONT = "cdn.example.com";
    private static final int EXPIRATION_HOURS = 24;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "bucketName", BUCKET);
        ReflectionTestUtils.setField(fileService, "cloudFrontDomain", CLOUD_FRONT);
        ReflectionTestUtils.setField(fileService, "expirationHours", EXPIRATION_HOURS);
    }

    @AfterEach
    void tearDown() {
        // Clean up if needed - fields are reassigned on each @BeforeEach
    }

    // --- Helpers ---

    private MultipartFile mockFile(String name, String contentType, long size, byte[] bytes) {
        MultipartFile file = mock(MultipartFile.class);
        lenient().when(file.getOriginalFilename()).thenReturn(name);
        lenient().when(file.getContentType()).thenReturn(contentType);
        lenient().when(file.getSize()).thenReturn(size);
        try {
            lenient().when(file.getInputStream()).thenReturn(new ByteArrayInputStream(bytes != null ? bytes : new byte[0]));
            lenient().when(file.getBytes()).thenReturn(bytes != null ? bytes : new byte[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    // --- uploadFile ---

    @Test
    void uploadFile_shouldReturnUrl_whenValidFile() {
        MultipartFile file = mockFile("test.pdf", "application/pdf", 1024, new byte[]{1, 2, 3});

        String result = fileService.uploadFile("documents", file);

        assertTrue(result.startsWith("https://" + CLOUD_FRONT + "/documents/"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFile_shouldThrowRuntimeException_whenS3Fails() {
        MultipartFile file = mockFile("test.pdf", "application/pdf", 1024, new byte[]{1, 2, 3});
        doThrow(new RuntimeException("S3 upload failed"))
                .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThrows(RuntimeException.class, () -> fileService.uploadFile("documents", file));
    }

    // --- deleteFile ---

    @Test
    void deleteFile_shouldReturnSuccess_whenFileExists() {
        String filePath = "https://" + CLOUD_FRONT + "/documents/oldfile.pdf";

        String result = fileService.deleteFile(filePath);

        assertEquals("Deleted: " + filePath, result);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_shouldSkipDelete_whenNullPath() {
        String result = fileService.deleteFile(null);

        assertEquals("Deleted: null", result);
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_shouldSkipDelete_whenDefaultThumbnail() {
        String filePath = "https://" + CLOUD_FRONT + "/thumbnail/course-default.png";

        String result = fileService.deleteFile(filePath);

        assertEquals("Deleted: " + filePath, result);
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_shouldThrowRuntimeException_whenS3DeleteFails() {
        String filePath = "https://" + CLOUD_FRONT + "/documents/test.pdf";
        doThrow(new RuntimeException("S3 error"))
                .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(RuntimeException.class, () -> fileService.deleteFile(filePath));
    }

    // --- replaceFile ---

    @Test
    void replaceFile_shouldReturnNewUrl_whenValidInput() {
        MultipartFile newFile = mockFile("newfile.pdf", "application/pdf", 2048, new byte[]{4, 5, 6});
        String oldFileUrl = "https://" + CLOUD_FRONT + "/documents/oldfile.pdf";

        String result = fileService.replaceFile(oldFileUrl, "documents", newFile);

        assertTrue(result.startsWith("https://" + CLOUD_FRONT + "/documents/"));
        assertTrue(result.endsWith("newfile.pdf"));
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));  // old file deleted
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));  // new file uploaded
    }

    @Test
    void replaceFile_shouldNotFail_whenOldFileUrlIsNull() {
        MultipartFile newFile = mockFile("newfile.pdf", "application/pdf", 2048, new byte[]{4, 5, 6});

        String result = fileService.replaceFile(null, "documents", newFile);

        assertTrue(result.startsWith("https://" + CLOUD_FRONT + "/documents/"));
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void replaceFile_shouldNotFail_whenOldDeleteThrowsException() {
        MultipartFile newFile = mockFile("newfile.pdf", "application/pdf", 2048, new byte[]{4, 5, 6});
        String oldFileUrl = "https://" + CLOUD_FRONT + "/documents/oldfile.pdf";

        doThrow(new RuntimeException("Delete failed"))
                .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        String result = fileService.replaceFile(oldFileUrl, "documents", newFile);

        // Should still succeed despite old file delete failure
        assertTrue(result.startsWith("https://" + CLOUD_FRONT + "/documents/"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void replaceFile_shouldThrowRuntimeException_whenUploadFails() {
        MultipartFile newFile = mockFile("newfile.pdf", "application/pdf", 2048, new byte[]{4, 5, 6});
        String oldFileUrl = "https://" + CLOUD_FRONT + "/documents/oldfile.pdf";

        doThrow(new RuntimeException("Upload failed"))
                .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThrows(RuntimeException.class,
                () -> fileService.replaceFile(oldFileUrl, "documents", newFile));
    }

    // --- getPageNumber ---

    @Test
    void getPageNumber_shouldReturnPageCount_whenValidPdf() {
        MultipartFile file = mockFile("test.pdf", "application/pdf", 1024, new byte[]{1, 2, 3});

        try (MockedStatic<Loader> loaderMock = mockStatic(Loader.class)) {
            PDDocument mockDoc = mock(PDDocument.class);
            when(mockDoc.getNumberOfPages()).thenReturn(10);
            loaderMock.when(() -> Loader.loadPDF(any(byte[].class))).thenReturn(mockDoc);

            Integer result = fileService.getPageNumber(file);

            assertEquals(10, result);
        }
    }

    @Test
    void getPageNumber_shouldReturnMinusOne_whenLoadingException() {
        MultipartFile file = mockFile("corrupt.pdf", "application/pdf", 1024, new byte[]{1, 2, 3});

        try (MockedStatic<Loader> loaderMock = mockStatic(Loader.class)) {
            loaderMock.when(() -> Loader.loadPDF(any(byte[].class)))
                    .thenThrow(new RuntimeException("PDF parsing error"));

            Integer result = fileService.getPageNumber(file);

            assertEquals(-1, result);
        }
    }

    @Test
    void getPageNumber_shouldReturnMinusOne_whenBytesException() {
        MultipartFile file = mock(MultipartFile.class);
        try {
            when(file.getBytes()).thenThrow(new RuntimeException("IO error"));
        } catch (Exception e) {
            // won't happen in mock setup
        }

        Integer result = fileService.getPageNumber(file);

        assertEquals(-1, result);
    }

    // --- generateUploadUrl ---

    @Test
    void generateUploadUrl_shouldReturnPresignedUrl() throws Exception {
        URL mockUrl = new URL("https://" + BUCKET + ".s3.amazonaws.com/presigned-upload-url");
        PresignedPutObjectRequest mockPresigned = mock(PresignedPutObjectRequest.class);
        when(mockPresigned.url()).thenReturn(mockUrl);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(mockPresigned);

        Map<String, String> result = fileService.generateUploadUrl("documents", "test.pdf");

        assertNotNull(result.get("path"));
        assertTrue(result.get("path").startsWith("https://" + CLOUD_FRONT + "/documents/"));
        assertEquals(mockUrl.toString(), result.get("url"));
        assertNotNull(result.get("contentType"));
    }

    // --- generateDownloadUrl ---

    @Test
    void generateDownloadUrl_shouldReturnPresignedUrl() throws Exception {
        URL mockUrl = new URL("https://" + BUCKET + ".s3.amazonaws.com/presigned-download-url");
        PresignedGetObjectRequest mockPresigned = mock(PresignedGetObjectRequest.class);
        when(mockPresigned.url()).thenReturn(mockUrl);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(mockPresigned);

        String result = fileService.generateDownloadUrl("documents", "test.pdf");

        assertEquals(mockUrl.toString(), result);
    }

    // --- generateUrl ---

    @Test
    void generateUrl_shouldReturnFullUrl() {
        String result = fileService.generateUrl("documents", "test.pdf");

        assertTrue(result.startsWith("https://" + CLOUD_FRONT + "/documents/"));
        assertTrue(result.endsWith("test.pdf"));
    }
}
