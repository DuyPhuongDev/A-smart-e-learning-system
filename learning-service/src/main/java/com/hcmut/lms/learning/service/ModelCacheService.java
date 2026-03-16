package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.dto.internal.CachedModel;

public interface ModelCacheService {
    /**
     * Get active model from cache or download from S3 if not cached.
     * Thread-safe.
     *
     * @return CachedModel containing model metadata and local paths
     */
    CachedModel getActiveModel();

    /**
     * Force reload active model from S3.
     */
    void reloadActiveModel();
}
