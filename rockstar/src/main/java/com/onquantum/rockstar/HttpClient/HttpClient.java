package com.onquantum.rockstar.HttpClient;


import android.net.Uri;
import android.os.Handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 12/10/15.
 */
public class HttpClient {

    private int REQUEST_TIME_OUT_MS = 25000;
    private int CONNECTION_TIME_OUT_MS = 50000;
    private Handler handler;


    interface HttpClientResponseHandler {
        void onSuccessResponse();
        void Start();
        void onCancel();
    }

    private HttpURLConnection getConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setReadTimeout(REQUEST_TIME_OUT_MS);
        httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_MS);
        return httpURLConnection;
    }

    public void doPost(final String urlString, final HashMap<String, String>postParams, final HttpClientResponseHandler httpClientResponseHandler) {
        new Thread(new Runnable() {
            HttpURLConnection httpURLConnection = null;
            @Override
            public void run() {
                try {
                    httpURLConnection = getConnection(urlString);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    Uri.Builder uriBuilder = new Uri.Builder();
                    for (Map.Entry<String, String> entry : postParams.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(),entry.getValue());
                    }
                    String queryParam = uriBuilder.build().getEncodedQuery();
                    if(queryParam != null) {
                        bufferedWriter.write(queryParam);
                        bufferedWriter.flush();
                    }
                    bufferedWriter.close();
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            }
        }).start();
    }
}
