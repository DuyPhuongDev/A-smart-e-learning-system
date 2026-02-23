package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.ImportResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ImportService {
    ImportResultResponse importClassSectionsFromExcel(MultipartFile file, UUID semesterId, UUID currentUserId);
}
