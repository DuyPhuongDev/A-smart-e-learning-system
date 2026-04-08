package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import org.springframework.web.multipart.MultipartFile;

public interface ImportExportService {
    
    ImportResultDto importUsersFromExcel(MultipartFile file);
    
    byte[] exportUsersToExcel(String search, UserStatus status, String department);
}

