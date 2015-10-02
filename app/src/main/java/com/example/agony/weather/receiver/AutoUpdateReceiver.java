package com.example.agony.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.agony.weather.service.AutoUpdateService;
import com.example.agony.weather.util.LogUtil;

public class AutoUpdateReceiver extends BroadcastReceiver {

    public static final String TAG = "AutoUpdateReceiver";

    public AutoUpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "BroadcastReceiver OnReceive");
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
