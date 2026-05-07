package com.hcmut.lms.assessment.dto.request.question;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private String name;
    private int size;
    private String url;
    private String type;
}
