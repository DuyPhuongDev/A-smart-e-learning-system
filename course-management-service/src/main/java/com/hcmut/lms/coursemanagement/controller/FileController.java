package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${prefix-api}/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam String folderPath, @RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadFile(folderPath,file);
        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upload-url")
    public ResponseEntity<Map<String, String>> uploadUrl(@RequestParam String folderPath, @RequestParam String fileName) {
        return  ResponseEntity.ok(fileService.generateUploadUrl(folderPath, fileName));
    }
}