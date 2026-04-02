package com.hcmut.lms.assessment.domain.entity.submission;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "submission_testcase_results")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionTestCaseResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_submission_id", nullable = false)
    private CodingSubmission codingSubmission;

    @Column(name = "testcase_id", nullable = false)
    private UUID testCaseId;

    @Column(name = "is_pass")
    private Boolean pass;

    @Column(name = "verdict", length = 16)
    private String verdict;

    @Column(name = "detail_error", columnDefinition = "text")
    private String detailError;

    @Column(name = "output", columnDefinition = "text")
    private String output;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;
}
