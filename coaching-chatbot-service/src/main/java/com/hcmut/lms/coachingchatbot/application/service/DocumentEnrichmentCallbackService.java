package com.hcmut.lms.coachingchatbot.application.service;

import com.hcmut.lms.coachingchatbot.application.dto.request.DocumentEnrichmentCallbackRequest;
import com.hcmut.lms.coachingchatbot.application.dto.response.DocumentEnrichmentCallbackResponse;

/**
 * Service for handling document/text enrichment callbacks from AWS Fargate workers.
 *
 * Flow:
 * 1. Receive enriched chunks from Fargate worker (already processed by LLM)
 * 2. Create/update LectureKnowledge record
 * 3. Save LectureKnowledgeChunk records
 */
public interface DocumentEnrichmentCallbackService {

    /**
     * Process document/text enrichment callback from Fargate worker
     *
     * @param request Document enrichment callback request containing enriched chunks
     * @return DocumentEnrichmentCallbackResponse with processing status
     */
    DocumentEnrichmentCallbackResponse processEnrichmentCallback(DocumentEnrichmentCallbackRequest request);
}
