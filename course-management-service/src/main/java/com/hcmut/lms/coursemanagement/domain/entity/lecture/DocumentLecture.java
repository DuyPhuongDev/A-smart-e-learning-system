package com.hcmut.lms.coursemanagement.domain.entity.lecture;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document_lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "lecture_id")
public class DocumentLecture extends Lecture {
    
    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "num_pages")
    private Integer numPages;
    
    @Column(name = "file_format")
    private String fileFormat;

    public DocumentLecture(String title, String fileUrl, Integer numPages, String fileFormat) {
        this.setTitle(title);
        this.setLectureType(LectureType.DOCUMENT);
        this.fileUrl = fileUrl;
        this.numPages = numPages;
        this.fileFormat = fileFormat;
    }
}

