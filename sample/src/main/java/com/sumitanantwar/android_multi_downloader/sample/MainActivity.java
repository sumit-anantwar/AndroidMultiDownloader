package com.sumitanantwar.android_multi_downloader.sample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sumitanantwar.android_multi_downloader.DownloadRequest;
import com.sumitanantwar.android_multi_downloader.DownloadRequestCallback;
import com.sumitanantwar.android_multi_downloader.DownloadRequestError;
import com.sumitanantwar.android_multi_downloader.Downloadable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG = "MainActivity";

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(buttonOnClick);
    }

    private View.OnClickListener buttonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String baseUrl = "http://staticfiles.popguide.me/";
            String extn = ".tgz";
            String baseDestination = getApplicationContext().getFilesDir() + File.separator + "Packages" + File.separator;

            String[] files = {"it", "ja", "ko", "pl", "pt", "ru", "en"};

            List<Downloadable> downloadables = new ArrayList<Downloadable>();
            int tag = 0;
            for (String fn : files) {

                String url = baseUrl + fn + extn;
                String destn = baseDestination + fn + extn;

                Downloadable d = new Downloadable(url, destn);
                d.setOnDownloadListener(downloadListener);
                downloadables.add(d);
                d.setTag(tag);

                tag++;
            }

//            downloadables.clear();
            Downloadable dldbl1 = new Downloadable("https://assets.popguide.me/uploads/home_location/image/3/mobile_Screenshot_from_2016-11-23_13-11-32.png", "/data/user/0/io.populi.pop_app_android/files/homeLocationImages/mobile_Screenshot_from_2016-11-23_13-11-32.png");
            dldbl1.setOnDownloadListener(downloadListener);
            downloadables.add(dldbl1);
            Downloadable dldbl2 = new Downloadable("https://assets.popguide.me/uploads/home_location/image/2/mobile_Rome.jpeg", "/data/user/0/io.populi.pop_app_android/files/homeLocationImages/mobile_Rome.jpeg");
            dldbl2.setOnDownloadListener(downloadListener);
            downloadables.add(dldbl2);

            DownloadRequest request = new DownloadRequest(context);
            request.download(downloadables, callback);
        }
    };

    private Downloadable.OnDownloadListener downloadListener = new Downloadable.OnDownloadListener()
    {
        @Override
        public void onDownloadComplete(Downloadable downloadable)
        {
            Log.i(LOG_TAG, "Download Success :  Tag - " + downloadable.getTag() + " - " + downloadable.getTargetUrl() + " - to : " + downloadable.getDestinationPath());
        }

        @Override
        public void onDownloadFailure(Downloadable downloadable, int responseCode, Map<String, List<String>> headerMap)
        {
            Log.i(LOG_TAG, "Download Failed : " + downloadable.getTargetUrl() + " -- Response Code - " + responseCode);
        }
    };

    private DownloadRequestCallback callback = new DownloadRequestCallback()
    {
        @Override
        public void onDownloadComplete(List<Downloadable> completedDownloadables, List<Downloadable> failedDownloadables) {

            Toast.makeText(context, "Download finished : Complete - " + completedDownloadables.size() + " -- Incomplete - " + failedDownloadables.size(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDownloadProgress(long completedBytes, long totalBytes) {

//            Log.i(LOG_TAG, String.format("Download Progress : %s / %s", completedBytes, totalBytes));
        }

        @Override
        public void onDownloadFailure(DownloadRequestError error) {

            String msg = "Download Failed with Error : " + error.name();
            Log.e(LOG_TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    };
}
