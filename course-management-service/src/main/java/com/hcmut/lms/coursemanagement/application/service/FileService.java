package com.hcmut.lms.coursemanagement.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    String uploadFile(String folderPath, MultipartFile file);

    String deleteFile(String filePath);

    String replaceFile(String oldFileUrl, String folderPath, MultipartFile newFile);

    Integer getPageNumber(MultipartFile file);

    Map<String, String> generateUploadUrl(String folderPath, String fileName);

    String generateDownloadUrl(String folderPath, String fileName);

    String generateUrl(String folderPath, String fileName);
}
