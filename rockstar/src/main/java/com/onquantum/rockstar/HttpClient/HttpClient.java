package com.onquantum.rockstar.HttpClient;


import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 12/10/15.
 */
public class HttpClient {

    private Handler handler;

    public HttpClient(){
        handler = new Handler();
    }

    public HttpClient(Handler handler){
        this.handler = handler;
    }

    public interface HttpClientResponse {
        void Success(int statusCode, InputStream inputStream);
        void Cancel();
        void Start();
        void ConnectionTimeOut();
    }

    public void httpPost(final String postUrl, final HashMap<String, String> params, final HttpClientResponse httpClientResponse) {
        httpClientResponse.Start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("info"," HttpClient :  START HTTP POST REQUEST " + postUrl);
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(postUrl);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(25000);
                    httpURLConnection.setReadTimeout(25000);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    Uri.Builder uriBuilder = new Uri.Builder();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(),entry.getValue());
                    }
                    String queryParam = uriBuilder.build().getEncodedQuery();
                    if(queryParam != null) {
                        bufferedWriter.write(queryParam);
                        bufferedWriter.flush();
                    }
                    bufferedWriter.close();
                    outputStream.close();

                    handler.post(new ResponseSuccessCallback(httpClientResponse, httpURLConnection.getResponseCode(),httpURLConnection.getInputStream()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e){
                    e.printStackTrace();

                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    handler.post(new ResponseCancelCallback(httpClientResponse));
                    if(httpURLConnection != null) {
                        Log.i("info"," HttpClient :  DISCONNECT HTTP CONNECTION");
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void httpPost(final String postUrl, final String rawData, final HttpClientResponse httpClientResponse) {
        httpClientResponse.Start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("info"," HttpClient :  START HTTP POST REQUEST " + postUrl);
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(postUrl);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(25000);
                    httpURLConnection.setReadTimeout(25000);
                    httpURLConnection.setRequestProperty("Content-Type","application/json");
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(rawData.getBytes());
                    outputStream.flush();

                    handler.post(new ResponseSuccessCallback(httpClientResponse, httpURLConnection.getResponseCode(),httpURLConnection.getInputStream()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    handler.post(new ResponseCancelCallback(httpClientResponse));
                    if(httpURLConnection != null) {
                        Log.i("info"," HttpClient :  DISCONNECT HTTP CONNECTION");
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void httpPut(final String postUrl, final HashMap<String, String> params, final HttpClientResponse httpClientResponse) {
        httpClientResponse.Start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("info"," HttpClient :  START HTTP POST REQUEST " + postUrl);
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(postUrl);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.setConnectTimeout(25000);
                    httpURLConnection.setReadTimeout(25000);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    Uri.Builder uriBuilder = new Uri.Builder();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        uriBuilder.appendQueryParameter(entry.getKey(),entry.getValue());
                    }
                    String queryParam = uriBuilder.build().getEncodedQuery();
                    if(queryParam != null) {
                        bufferedWriter.write(queryParam);
                        bufferedWriter.flush();
                    }
                    bufferedWriter.close();
                    outputStream.close();

                    handler.post(new ResponseSuccessCallback(httpClientResponse, httpURLConnection.getResponseCode(),httpURLConnection.getInputStream()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e){
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    handler.post(new ResponseCancelCallback(httpClientResponse));
                    if(httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void httpGet(final String getUrl, final HttpClientResponse httpClientResponse) {
        httpClientResponse.Start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("info"," HttpClient :  START HTTP GET REQUEST " + getUrl);
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(getUrl);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(25000);
                    httpURLConnection.setReadTimeout(25000);

                    handler.post(new ResponseSuccessCallback(httpClientResponse, httpURLConnection.getResponseCode(), httpURLConnection.getInputStream()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    handler.post(new ResponseCancelCallback(httpClientResponse));
                    if(httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    // Http client response callback
    private class ResponseSuccessCallback implements Runnable {
        private HttpClientResponse httpClientResponse;
        private int responseCode;
        private InputStream inputStream;
        public ResponseSuccessCallback(HttpClientResponse httpClientResponse, int responseCode, InputStream inputStream) {
            this.httpClientResponse = httpClientResponse;
            this.responseCode = responseCode;
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            httpClientResponse.Success(responseCode,inputStream);
        }
    }

    private class ResponseCancelCallback implements Runnable {
        public HttpClientResponse httpClientResponse;
        public ResponseCancelCallback(HttpClientResponse httpClientResponse) {
            this.httpClientResponse = httpClientResponse;
        }
        @Override
        public void run() {
            httpClientResponse.Cancel();
        }
    }

    private class ConnectionTimeOut implements Runnable {
        private HttpClientResponse httpClientResponse;
        public ConnectionTimeOut(HttpClientResponse httpClientResponse) {
            this.httpClientResponse = httpClientResponse;
        }
        @Override
        public void run() {
            httpClientResponse.ConnectionTimeOut();
        }
    }

    // Helper method parse input stream to json object
    public static JSONObject ParseJsonObject(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            Log.i("info"," HttClient : (ParseJsonObject) : " + jsonObject.toString());
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
