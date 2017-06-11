package com.sumitanantwar.android_multi_downloader;

import java.util.List;

/**
 * Created by Sumit Anantwar on 7/14/16.
 */
interface RetryResponse
{
    void needsRetry(List<Processable> processables);
    void retryNotNeeded(Throwable e);
}
