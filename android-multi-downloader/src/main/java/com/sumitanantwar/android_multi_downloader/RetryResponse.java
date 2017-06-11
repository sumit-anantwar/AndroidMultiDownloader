package com.sumitanantwar.android_multi_downloader;

import java.util.Map;

/**
 * Created by Sumit Anantwar on 7/14/16.
 */
interface RetryResponse
{
    void needsRetry(Map<Downloadable, Processable> processableMap);
    void retryNotNeeded(Throwable e);
}
