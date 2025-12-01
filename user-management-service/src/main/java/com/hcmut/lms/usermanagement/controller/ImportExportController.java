package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import com.hcmut.lms.usermanagement.service.ImportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import-export")
@RequiredArgsConstructor
public class ImportExportController {
    
    private final ImportExportService importExportService;
    
    @PostMapping("/users/import")
    public ResponseEntity<ImportResultDto> importUsers(@RequestParam("file") MultipartFile file) {
        ImportResultDto result = importExportService.importUsersFromExcel(file);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/users/export")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String department) {
        byte[] excelData = importExportService.exportUsersToExcel(search, status, department);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users_export.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    
    @GetMapping("/users/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = importExportService.generateImportTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "user_import_template.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }
}

