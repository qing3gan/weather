package com.example.agony.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.agony.weather.BuildConfig;
import com.example.agony.weather.receiver.AutoUpdateReceiver;
import com.example.agony.weather.util.HttpCallbackListener;
import com.example.agony.weather.util.HttpUtil;
import com.example.agony.weather.util.ParseUtil;

public class AutoUpdateService extends Service {

    public static final String TAG = "AutoUpdateService";

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service OnStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + BuildConfig.ALARM_TIME;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Update Weather Info
     */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = preferences.getString("weather_code", "");
        String address = BuildConfig.SERVER_WEATHER_ADDRESS + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                ParseUtil.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
