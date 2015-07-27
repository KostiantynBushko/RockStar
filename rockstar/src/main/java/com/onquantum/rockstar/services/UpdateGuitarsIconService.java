package com.onquantum.rockstar.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Bushko on 7/19/15.
 */
public class UpdateGuitarsIconService extends Service {

    public static String BROADCAST_COMPLETE_DOWNLOAD_ICON_FILE_ACTION = "com.onquantum.rockstar.services.updateguitarsiconservice";
    private ExecutorService executorService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("info","UpdateGuitarsIconService : onCreate");
        executorService = Executors.newFixedThreadPool(1);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("info","UpdateGuitarsIconService : onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.i("info", "UpdateGuitarsIconService : onStartCommand flag = " + flag + " startId = " + startId);
        executorService.execute(new DownloadIcon(startId, intent.getStringExtra(DBGuitarTable.ICON),intent.getIntExtra(DBGuitarTable.ID,0)));
        return START_REDELIVER_INTENT;
    }


    private class DownloadIcon implements Runnable {
        int startId = 0;
        int id = 0;
        String iconFileName = null;

        public DownloadIcon(int startId, String iconFileName, int id) {
            this.startId = startId;
            this.iconFileName = iconFileName;
            this.id = id;
        }
        @Override
        public void run() {
            if(id == 0) {
                stopSelf(startId);
                return;
            }
            Log.i("info", "UpdateGuitarsIconService: START TASK Download file icon : startId = " + startId + " dataBaseId = " + id + " iconFileName = " + iconFileName);
            //SystemClock.sleep(5000);

            try {
                URL url = new URL(QURL.GET_SOUND_PACK_ICON);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                // POST parameter
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("file_name",this.iconFileName);
                bufferedWriter.write(builder.build().getEncodedQuery());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read data from http
                //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                File iconFile = new File(FileSystem.GetIconPath(),this.iconFileName);
                FileOutputStream fileOutputStream = new FileOutputStream(iconFile);
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int bufferSize = 0;
                while ((bufferSize = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferSize);
                }
                fileOutputStream.close();

                // Send broadcast message the image icon file complete download
                Intent intent = new Intent(BROADCAST_COMPLETE_DOWNLOAD_ICON_FILE_ACTION);
                intent.putExtra(DBGuitarTable.ICON, iconFileName);
                intent.putExtra(DBGuitarTable.ID,id);
                sendBroadcast(intent);
                stopSelf(startId);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
