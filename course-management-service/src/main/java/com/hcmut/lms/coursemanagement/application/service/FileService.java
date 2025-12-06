package com.hcmut.lms.coursemanagement.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(String folderPath, MultipartFile file);

    String deleteFile(String filePath);

    String replaceFile(String oldFileUrl, String folderPath, MultipartFile newFile);

    Integer getPageNumber(MultipartFile file);
}
