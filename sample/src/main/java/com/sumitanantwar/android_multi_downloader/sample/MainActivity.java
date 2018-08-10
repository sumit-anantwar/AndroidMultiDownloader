package com.sumitanantwar.android_multi_downloader.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
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
            
            String baseDestination = getApplicationContext().getFilesDir() + File.separator + "Images" + File.separator;
            
            String[] urls = new String[]{
                    "https://assets.popguide.me/uploads/star_point/image/391/mobile_ef9b36e2d2b720648dd07c4ce5562ea4.jpg",
                    "https://assets.popguide.me/uploads/star_point_category/icon/3/06f310cad53e3b669d86aaad2f2e3e8f.png",
                    "https://assets.popguide.me/uploads/star_point/image/758/mobile_098344ed16b02a95093a7ebd7714fdeb.png",
                    "https://assets.popguide.me/uploads/star_point_category/icon/3/06f310cad53e3b669d86aaad2f2e3e8f.png",
                    "https://assets.popguide.me/uploads/star_point/image/922/mobile_ef9b36e2d2b720648dd07c4ce5562ea4.jpg",
                    "https://assets.popguide.me/uploads/star_point_category/icon/3/06f310cad53e3b669d86aaad2f2e3e8f.png",
                    "https://assets.popguide.me/uploads/star_point/image/925/mobile_1649d9bf5e5db7d639d645d7019d5d0e.png",
                    "https://assets.popguide.me/uploads/star_point_category/icon/3/06f310cad53e3b669d86aaad2f2e3e8f.png",
                    "https://assets.popguide.me/uploads/star_point/image/465/mobile_1649d9bf5e5db7d639d645d7019d5d0e.png",
                    "https://assets.popguide.me/uploads/star_point_category/icon/4/c288d7b2aba781063e1e4ae04c2387e5.png"
            };

            List<Downloadable> downloadables = new ArrayList<>();
            int tag = 0;
            for (String url : urls) {
                
                String destn = baseDestination + URLUtil.guessFileName(url, null, null);

                Downloadable d = new Downloadable(url, destn);
                d.setOnDownloadListener(downloadListener);
                downloadables.add(d);
                d.setTag(tag);

                tag++;
            }

//            downloadables.clear();
//            Downloadable dldbl1 = new Downloadable("https://assets.popguide.me/uploads/home_location/image/3/mobile_Screenshot_from_2016-11-23_13-11-32.png", "/data/user/0/io.populi.pop_app_android/files/homeLocationImages/mobile_Screenshot_from_2016-11-23_13-11-32.png");
//            dldbl1.setOnDownloadListener(downloadListener);
//            downloadables.add(dldbl1);
//            Downloadable dldbl2 = new Downloadable("https://assets.popguide.me/uploads/home_location/image/2/mobile_Rome.jpeg", "/data/user/0/io.populi.pop_app_android/files/homeLocationImages/mobile_Rome.jpeg");
//            dldbl2.setOnDownloadListener(downloadListener);
//            downloadables.add(dldbl2);

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

        @Override
        public void onDownloadCancelled() {
            Toast.makeText(context, "Task has been cancelled", Toast.LENGTH_LONG).show();
        }
    };
}
