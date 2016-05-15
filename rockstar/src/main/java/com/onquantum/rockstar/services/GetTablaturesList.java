package com.onquantum.rockstar.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Admin on 5/7/16.
 */
public class GetTablaturesList extends Service {

    public static String BROADCAST_COMPLETE_OBTAIN_TABLATURE_LIST_ACTION = "com.onquantum.rockstar.services.GetTablaturesList";

    private ExecutorService executorService = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i("info","GetTablaturesList : onCreate");
        this.executorService = Executors.newFixedThreadPool(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("info","GetTablaturesList : onDestroy");
        this.executorService = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        this.executorService.execute(new GetTabList(intent, startId));
        return START_REDELIVER_INTENT;
    }

    private class GetTabList implements Runnable{
        private int startId = 0;
        private int from = 0;
        private int limit = 0;

        public GetTabList(Intent intent, int startId) {
            this.startId = startId;
            this.from = intent.getIntExtra("from",0);
            this.limit = intent.getIntExtra("limit", 0);
            //Log.i("info"," from " + from + " limit " + limit);
        }

        @Override
        public void run() {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(QURL.GTE_TABLATURE_LIST);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(50000);
                httpURLConnection.setReadTimeout(50000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoInput(true);

                // POST parameter
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("from",Integer.toString(this.from));
                builder.appendQueryParameter("limit", Integer.toString(this.limit));
                bufferedWriter.write(builder.build().getEncodedQuery());
                bufferedWriter.flush();
                bufferedWriter.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                //Log.i("info","GetTablaturesList :  " + stringBuilder.toString());
                Intent intent = new Intent(BROADCAST_COMPLETE_OBTAIN_TABLATURE_LIST_ACTION);
                intent.putExtra("tablature_list",stringBuilder.toString());
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
