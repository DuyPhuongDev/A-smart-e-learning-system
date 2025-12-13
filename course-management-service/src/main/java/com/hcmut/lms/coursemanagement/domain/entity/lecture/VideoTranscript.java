package com.hcmut.lms.coursemanagement.domain.entity.lecture;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "video_transcripts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VideoTranscript extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_lecture_id", nullable = false)
    private VideoLecture videoLecture;
    
    @Column(name = "transcript_text", columnDefinition = "TEXT")
    private String transcriptText;
}

