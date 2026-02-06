package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.response.ImportResultResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    ImportResultResponse importSubjectsFromExcel(MultipartFile file);
}
