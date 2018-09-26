package com.learning.service;

import com.learning.beans.CacheKeyConstants;

public interface SysCacheService {

    void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix);

    void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix, String... keys);

    String getFromCache(CacheKeyConstants prefix, String... keys);
}
