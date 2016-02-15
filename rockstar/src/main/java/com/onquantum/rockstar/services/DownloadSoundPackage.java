package com.onquantum.rockstar.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onquantum.rockstar.HttpClient.HttpClient;
import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Admin on 7/28/15.
 */
public class DownloadSoundPackage extends Service {

    public interface OnProgressUpdate {
        public void updateProgress(String soundPackage, long progress);
    }
    public OnProgressUpdate onProgressUpdate;
    public void SetOnProgressUpdateListener(OnProgressUpdate progressUpdate) {
        this.onProgressUpdate = progressUpdate;
    }

    public static String BROADCAST_COMPLETE_DOWNLOAD_SOUND_PACKAGE_ACTION = "com.onquantum.rockstar.services.DownloadSoundPackage";

    private DownloadPackBinder downloadPackBinder = new DownloadPackBinder();
    private ExecutorService executorService;
    private HashMap<String, Long> packagesInProgress = new HashMap<String, Long>();

    private static float onePercent = 150.0f / 100.0f;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("info","DownloadSoundFile : onBind");
        return downloadPackBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i("info","DownloadSoundFile : onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("info", "DownloadSoundFile : onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.i("info","DownloadSoundFile : onCreate");
        executorService = Executors.newFixedThreadPool(1);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("info","DownloadSoundFile : onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        //Log.i("info", "DownloadSoundFile : onStartCommand flag = " + flag + " startId = " + startId);
        executorService.execute(new DownloadFile(intent, startId));
        return START_REDELIVER_INTENT;
    }

    public boolean isSoundPackInProgress(String soundPack) {
        return packagesInProgress.containsKey(soundPack);
    }
    public long GetProgressForSoundPack(String soundPack) {
        if(isSoundPackInProgress(soundPack))
            return packagesInProgress.get(soundPack);
        return -1;
    }


    private class DownloadFile implements Runnable {
        private int startId;
        private Intent intent;
        HttpClient httpClient;

        public DownloadFile(Intent intent, int startId) {
            this.startId = startId;
            this.intent = intent;
            packagesInProgress.put(intent.getStringExtra("sound_pack"), 0L);
            httpClient = new HttpClient();
        }
        
        @Override
        public void run() {
            final String guitarPackageName = intent.getStringExtra("sound_pack");
            final String fileName = intent.getStringExtra("file_name");
            final File file = new File(FileSystem.GetSoundFilesPath(guitarPackageName) + "/", fileName);

            if(file.exists() && file.length() > 0) {
                Log.i("info", " - DownloadSoundFile EXIST : packageName = " + guitarPackageName + " fileName = " + fileName + " startId = " + startId);
                stopSelf(startId);
                return;
            }

            if(guitarPackageName == null || fileName == null) {
                Log.i("info","DownloadSoundFile : fake call");
                stopSelf(startId);
                return;
            }

            HttpURLConnection httpURLConnection = null;
            try {

                URL url = new URL(QURL.GET_SOUND_FILE);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoInput(true);

                // POST parameter
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("sound_pack",guitarPackageName);
                builder.appendQueryParameter("file_name", fileName);
                bufferedWriter.write(builder.build().getEncodedQuery());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int bufferSize;
                while ((bufferSize = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferSize);
                }
                fileOutputStream.close();
                inputStream.close();

                Log.i("info", "DownloadSoundFile : packageName = " + guitarPackageName + " fileName = " + fileName + " startId = " + startId);
                GuitarEntity guitarEntity = DBGuitarTable.GetGuitarEntityByArticle(getApplicationContext(), guitarPackageName);
                long[] progress = new long[1];
                if(guitarEntity.isSoundPackAvailable(progress)) {
                    Intent intent = new Intent(BROADCAST_COMPLETE_DOWNLOAD_SOUND_PACKAGE_ACTION);
                    intent.putExtra(DBGuitarTable.ARTICLE, guitarPackageName);
                    sendBroadcast(intent);
                    packagesInProgress.remove(guitarPackageName);
                } else {
                    packagesInProgress.put(guitarPackageName, (long) (progress[0] / onePercent));
                }

                if(onProgressUpdate != null) {
                    onProgressUpdate.updateProgress(guitarPackageName, (long)(progress[0] / onePercent));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                Log.i("info","SOCET TIME OUT ");
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
                stopSelf(startId);
            }
        }
    }

    public class DownloadPackBinder extends Binder {
        public DownloadSoundPackage getServiceInstance() {
            return DownloadSoundPackage.this;
        }
    }
}
