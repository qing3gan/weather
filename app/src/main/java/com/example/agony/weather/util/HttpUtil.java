package com.example.agony.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http Request Util For The First Time
 * Created by Agony on 2015/9/27 0027.
 */
public class HttpUtil {

    public static final int TIME_OUT = 8000;

    /**
     * Send Http Request
     *
     * @param address
     * @param listener
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //HttpURLConnection&HttpClient{HttpRequest(HttpGet,HttpPost),HttpResponse)
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(TIME_OUT);
                    connection.setReadTimeout(TIME_OUT);
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    //StringBuffer(thread safe) → StringBuilder(not thread safe)
                    StringBuilder response = new StringBuilder();
                    //Assignment is ok(Initial avoid dump data for arithmetic operation and null for var.invoke)
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        listener.onSuccess(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
