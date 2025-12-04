package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl  implements FileService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.cloud-front.domain}")
    private String cloudFrontDomain;

    public String uploadFile(String folderPath, MultipartFile file) {
        String key = folderPath + "/" + generateFileKey(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PRIVATE)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Thay vì trả về S3 URL → trả về CloudFront URL
            return "https://" + cloudFrontDomain + "/" + key;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public String deleteFile(String filePath) {
        try {
            String oldKey = extractKeyFromCloudFrontUrl(filePath);
            if (oldKey != null && !oldKey.equals("thumbnail/course-default.png")) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(extractKeyFromCloudFrontUrl(filePath))
                        .build();

                s3Client.deleteObject(deleteRequest);
            }
            return "Deleted: " + filePath;

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    public String replaceFile(String oldFileUrl, String folderPath, MultipartFile newFile) {
        try {
            // 1. Convert CloudFront URL về key
            String oldKey = extractKeyFromCloudFrontUrl(oldFileUrl);

            // 2. Xoá file cũ (nếu có)
            if (oldKey != null && !oldKey.equals("thumbnail/course-default.png")) {
                try {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(oldKey)
                            .build());
                } catch (Exception e) {
                    // không throw, tránh làm fail cả update
                    System.err.println("Could not delete old file: " + oldKey);
                }
            }

            // 3. Upload file mới
            String newKey = folderPath + "/" + UUID.randomUUID() + "-" + newFile.getOriginalFilename();

            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newKey)
                    .contentType(newFile.getContentType())
                    .acl(ObjectCannedACL.PRIVATE)
                    .build();

            s3Client.putObject(
                    putReq,
                    RequestBody.fromInputStream(newFile.getInputStream(), newFile.getSize())
            );

            // 4. Trả về URL mới (CloudFront)
            return "https://" + cloudFrontDomain + "/" + newKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to replace file", e);
        }
    }

    private String extractKeyFromCloudFrontUrl(String url) {
        String oldKey = null;
        if (url != null && !url.isBlank()) {
            oldKey = url.replace("https://" + cloudFrontDomain + "/", "");
        }
        return oldKey;
    }


    private String generateFileKey(String originalFilename) {
        return UUID.randomUUID().toString() + "-" + sanitizeFilename(originalFilename);
    }

    private String sanitizeFilename(String original) {
        if (original == null) return "file";

        // Tách tên và đuôi
        String ext = "";
        int dotIndex = original.lastIndexOf(".");
        if (dotIndex != -1) {
            ext = original.substring(dotIndex).toLowerCase(); // giữ extension
            original = original.substring(0, dotIndex);
        }

        // Chuẩn hóa Unicode (loại dấu)
        String normalized = java.text.Normalizer.normalize(original, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // bỏ dấu tiếng Việt

        // Chỉ giữ ký tự an toàn: chữ + số + dấu gạch ngang
        normalized = normalized.replaceAll("[^a-zA-Z0-9\\-\\s_]", "");

        // Thay khoảng trắng bằng '-'
        normalized = normalized.trim().replaceAll("\\s+", "-");

        // Đưa về lowercase
        normalized = normalized.toLowerCase();

        if (normalized.isEmpty()) normalized = "file";

        return normalized + ext;
    }

}
