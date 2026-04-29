package com.hcmut.lms.personalization.application.dto.record;

import java.util.UUID;

public record CandidateInfo(UUID subjectId, String subjectCode, String subjectName,
                            int credits, Boolean isRequired, int priority1, int sectionWeight) {}
