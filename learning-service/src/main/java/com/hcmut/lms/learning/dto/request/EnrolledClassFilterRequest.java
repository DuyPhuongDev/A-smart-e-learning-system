package com.hcmut.lms.learning.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Filter request for getting enrolled classes
 */
@Getter
@Setter
public class EnrolledClassFilterRequest {
    private String semesterCode;
    private String searchTerm; // Search by class name or subject name
    private int page = 0;
    private int size = 10;
}
