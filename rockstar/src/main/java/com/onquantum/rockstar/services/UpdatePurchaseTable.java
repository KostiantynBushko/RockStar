package com.onquantum.rockstar.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onquantum.rockstar.QURL.QURL;
import com.onquantum.rockstar.gsqlite.DBAbstractTable;
import com.onquantum.rockstar.gsqlite.DBPurchaseTable;
import com.onquantum.rockstar.gsqlite.PurchaseEntity;

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
 * Created by Admin on 7/29/15.
 */
public class UpdatePurchaseTable extends Service {
    public static String BROADCAST_COMPLETE_UPDATE_PURCHASE = "com.onquantum.rockstar.services.update_purchase";
    private boolean isRunning = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Log.i("info","UpdatePurchaseTable : onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ///Log.i("info", "UpdatePurchaseTable : onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        ///Log.i("info","UpdatePurchaseTable : onStartCommand : flag = " + flag + " startId = " + startId);
        if(!isRunning)
            new UpdateTask().start();
        return START_NOT_STICKY;
    }

    private class UpdateTask extends Thread {
        @Override
        public void run() {
            ///Log.i("info","UpdatePurchaseTable UpdateTask : run");
            isRunning = true;
            long countRows = DBPurchaseTable.GetCountOfRows(getApplicationContext(), DBPurchaseTable.DB_PURCHASE_TB);
            countRows++;

            try {
                URL url = new URL(QURL.GET_PURCHASES);

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                // POST parameter
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("fromId", Long.toString(countRows));
                String queryParams = builder.build().getEncodedQuery();

                bufferedWriter.write(queryParams);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    ///Log.i("info", " UpdatePurchaseTable response: " + line);
                }

                LinkedList<PurchaseEntity>purchaseEntities = new LinkedList<PurchaseEntity>();
                JSONArray purchaseObjects = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < purchaseObjects.length(); i++) {
                    PurchaseEntity purchaseEntity = PurchaseEntity.CreatePurchaseEntity((JSONObject)purchaseObjects.get(i));
                    purchaseEntities.add(purchaseEntity);
                    Log.i("info","UpdatePurchasesTable PURCHASES ENTITY : " + purchaseEntity.toString());
                }

                if(purchaseEntities.size() > 0) {
                    DBPurchaseTable.AddPurchaseEntities(getApplicationContext(), purchaseEntities);
                }
                Intent intent = new Intent(BROADCAST_COMPLETE_UPDATE_PURCHASE);
                sendBroadcast(intent);
                stopSelf();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

























