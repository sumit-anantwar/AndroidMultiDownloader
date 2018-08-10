package com.sumitanantwar.android_multi_downloader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sumit Anantwar on 7/13/16.
 *
 */
class RetryHandler extends AsyncTask<Void, Void, Boolean>
{
    private static final String LOG_TAG = RetryHandler.class.getSimpleName();
    
    private final List<Downloadable> mDownloadables;
    private final RetryResponse mRetryResponse;
    private AsyncDownloader mAsyncDownloader;
    
    private final String cacheDir;

    private final List<Processable> processables;
    private Throwable error;

    RetryHandler(Context context, List<Downloadable> downloadables, RetryResponse retryResponse)
    {
        this.mDownloadables = downloadables;
        this.mRetryResponse = retryResponse;
        
        this.cacheDir = context.getCacheDir().toString();
        
        this.processables = new ArrayList<>(downloadables.size());
    }

    @Override
    protected void onPostExecute(Boolean shouldRetry)
    {
        super.onPostExecute(shouldRetry);

        // Check the result Boolean
        // if True, we should retry downloading
        if (shouldRetry) {
            mAsyncDownloader = mRetryResponse.needsRetry(processables);
        }
        else if (error != null) {
            // if False and if an Exception was caught
            mRetryResponse.retryNotNeeded(error);
        }
        else {
            // Execution reaches here if (targetLength == downloadedContentSize)
            // This means that no more data is available for download, Download has been completed
            mRetryResponse.retryNotNeeded(null);
        }
    }

    public void cancelAllTasks() {
        if (mAsyncDownloader != null && mAsyncDownloader.getStatus() == Status.RUNNING) {
            mAsyncDownloader.cancel(true);
        } else {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mRetryResponse.onCancelled();
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        HttpURLConnection connection = null;
        try {

            long pendingContentSize = 0;

            for (Downloadable downloadable : mDownloadables)
            {
                if (isCancelled()) {
                    return false;
                }

                Processable processable = new Processable(cacheDir, downloadable);

                URL targetUrl = processable.getTargetUrl();
                
                // Open a new connection
                connection = (HttpURLConnection) targetUrl.openConnection();
                Map<String, List<String>> headers = connection.getHeaderFields();
                // We only need to check the total size of the file to be downloaded.
                // So, a HEAD request is enough.
                // This saves bandwidth and is faster.
                try {
                    connection.setRequestMethod("HEAD");
                }
                catch (ProtocolException e) {
                    // Some API versions do not support setting Request Method
                    // In this case, just catch the exception and proceed using the default "GET" method
                    e.printStackTrace();
                    Log.e(LOG_TAG, "API Version does not support setting request method");
                }

                connection.setConnectTimeout(7000);

                // Get the Response Code from the connection
                int responseCode = connection.getResponseCode();
                processable.setResponseCode(responseCode);
                processable.setHeaders(headers);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // The connection is successful

                    // Open the cache file.
                    File cacheFile = new File(processable.getCacheFilePath());
                    if(!cacheFile.exists()) {
                        // Create the parent folder(s) if necessary
                        cacheFile.getParentFile().mkdirs();
                        // Create the file
                        cacheFile.createNewFile();
                    }

                    processable.setDownloadedContentSize(cacheFile.length());

                    // Get the Content Length
                    processable.setTotalContentSize(connection.getContentLength());
                }

                // Disconnect
                connection.disconnect();
                connection = null;

                // Add the processable to the ArrayList
                processables.add(processable);

                // Calculate the Total Pending Content Size
                pendingContentSize += processable.getPendingContentSize();
            }

            // Check if we have enough space available in the Internal Memory
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();

            if (bytesAvailable < pendingContentSize) {

                // Not enough memory to download the content
                error = new OutOfMemoryError();
                return false;
            }
        }
        catch (IOException e) {
            // Catching the raised IOException
            // This happens when the URL is unreachable or the Host is not resolved.
            Log.i(LOG_TAG, "Connection Error");
            e.printStackTrace();
            error = e;
            return false;
        }
        finally {

            if (connection != null) {

                connection.disconnect();
                connection = null;
            }
        }

        // If execution reaches here, we need to retry, return True
        return true;
    }
}
