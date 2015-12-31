package com.onquantum.rockstar.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by Bushko on 7/15/15.
 */
public class UpdateGuitarsService extends Service {

    public static boolean updateTaskIsRunning = false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("info","UpdateGuitarsService : onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("info","UpdateGuitarsService : onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        Log.i("info","UpdateGuitarsService : onStartCommand : flag = " + flag + " startId = " + startId);
        new UpdateTask().start();
        return START_REDELIVER_INTENT;
    }

    private class UpdateTask extends Thread {
        @Override
        public void run() {
            updateTaskIsRunning = true;
            Log.i("info", " UpdateGuitarService UpdateTask : run");
            long countRows = 1; //DBGuitarTable.GetCountOfRows(getApplicationContext(), DBGuitarTable.DB_GUITAR_TABLE);
            countRows++;

            OutputStream outputStream = null;
            try {
                URL url = new URL(QURL.GET_SOUND_PACK);

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                // POST parameter
                outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("fromId", Long.toString(countRows));
                builder.appendQueryParameter("status_id", "3");
                String queryParams = builder.build().getEncodedQuery();

                bufferedWriter.write(queryParams);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read data from http
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = null;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    Log.i("info"," UpdateGuitarsService response: " + line);
                }

                LinkedList<GuitarEntity>guitarEntities = new LinkedList<GuitarEntity>();
                JSONArray guitarObjects = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < guitarObjects.length(); i++) {
                    GuitarEntity guitarEntity = GuitarEntity.CreateGuitarEntity((JSONObject) guitarObjects.get(i));
                    if(DBGuitarTable.GuitarPackageAlreadyExists(getApplicationContext(), guitarEntity.article) == false) {
                        Log.i("info", "UpdateGuitarsService GUITAR ENTITY " + guitarEntity.toString());
                        guitarEntities.add(guitarEntity);
                    }
                }

                if(guitarEntities.size() > 0) {
                    // Update db guitar with new entity
                    DBGuitarTable.AddGuitarEntities(getApplicationContext(), guitarEntities);

                    // Download icon files for new
                    if(countRows == 2) {
                        GuitarEntity firstGuitarEntityItem = DBGuitarTable.GetGuitarEntityByID(getApplicationContext(), 1);
                        if(firstGuitarEntityItem != null) {
                            guitarEntities.addFirst(firstGuitarEntityItem);
                        }
                    }

                    for (GuitarEntity entity : guitarEntities) {
                        GuitarEntity guitarEntity = DBGuitarTable.GetGuitarEntityByArticle(getApplication(), entity.article);
                        String iconFileName = guitarEntity.icon;
                        Intent intent = new Intent(getApplicationContext(), UpdateGuitarsIconService.class);
                        intent.putExtra(DBGuitarTable.ICON,iconFileName);
                        intent.putExtra(DBGuitarTable.ID, guitarEntity.id);
                        startService(intent);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                if(outputStream != null)
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            updateTaskIsRunning = false;
            stopSelf();
        }
    }
}
