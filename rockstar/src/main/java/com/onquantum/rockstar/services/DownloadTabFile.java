package com.onquantum.rockstar.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.list_adapter.RemoteTabListAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Admin on 5/9/16.
 */
public class DownloadTabFile extends Service {

    public static String BROADCAST_COMPLETE_DOWNLOAD_TAB_FILE_ACTION = "com.onquantum.rockstar.services.DownloadTabFile";
    public static String FILE_NAME = "file_name";

    private ExecutorService executorService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Log.i("info","DownloadTabFile : onCreate");
        executorService = Executors.newFixedThreadPool(1);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //Log.i("info","DownloadTabFile : onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        executorService.execute(new DownloadFile(intent, startId));
        return START_REDELIVER_INTENT;
    }

    private class DownloadFile implements Runnable {
        private Intent intent = null;
        private int startId = 0;
        private int fileID = 0;

        public DownloadFile(Intent intent, int startId) {
            this.intent = intent;
            this.startId = startId;
            this.fileID = intent.getIntExtra("file_id",0);
        }

        @Override
        public void run() {

            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(QURL.DOWNLOAD_FILE);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoInput(true);

                // POST parameter
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("id",Integer.toString(fileID));
                bufferedWriter.write(builder.build().getEncodedQuery());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                String path = FileSystem.GetTabsFilesPath();
                path = path + "/" + httpURLConnection.getHeaderField("file_name");
                File file = new File(path);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int bufferSize;
                while ((bufferSize = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferSize);
                }
                Log.i("info","header : " + httpURLConnection.getHeaderField("file_name"));
                fileOutputStream.close();
                inputStream.close();


                Intent intent = new Intent(BROADCAST_COMPLETE_DOWNLOAD_TAB_FILE_ACTION);
                intent.putExtra(DownloadTabFile.FILE_NAME, file.getName());
                sendBroadcast(intent);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stopSelf(this.startId);
                httpURLConnection.disconnect();
            }
        }
    }
}
