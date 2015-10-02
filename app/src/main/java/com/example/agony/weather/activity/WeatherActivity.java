package com.example.agony.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.agony.weather.BuildConfig;
import com.example.agony.weather.R;
import com.example.agony.weather.service.AutoUpdateService;
import com.example.agony.weather.util.HttpCallbackListener;
import com.example.agony.weather.util.HttpUtil;
import com.example.agony.weather.util.LogUtil;
import com.example.agony.weather.util.ParseUtil;

/**
 * Created by Agony on 2015/9/29 0029.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "WeatherActivity";

    private LinearLayout llWeatherInfo;
    private TextView tvCityName, tvPublishTime, tvWeatherDesp, tvTemp1, tvTemp2, tvCurrentDate;
    private Button btnSwitchCity, btnRefreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initViews();
        initEvents();
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            tvPublishTime.setText("Synchronizing...");
            llWeatherInfo.setVisibility(View.INVISIBLE);
            tvCityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //default
            showWeatherInfo();
        }
    }

    /**
     * Show Weather Info From SharedPreferences
     */
    private void showWeatherInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvCityName.setText(preferences.getString("city_name", ""));
        tvPublishTime.setText("Today " + preferences.getString("publish_time", "") + " published");
        tvWeatherDesp.setText(preferences.getString("weather_desp", ""));
        tvTemp1.setText(preferences.getString("temp1", ""));
        tvTemp2.setText(preferences.getString("temp2", ""));
        tvCurrentDate.setText(preferences.getString("current_date", ""));
        llWeatherInfo.setVisibility(View.VISIBLE);
        tvCityName.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * Query Weather Code By County Code
     *
     * @param countyCode
     */
    private void queryWeatherCode(String countyCode) {
        String address = BuildConfig.SERVER_CITY_ADDRESS + countyCode + ".xml";
        LogUtil.d(TAG, "Query Weather Code By County Code " + address);
        queryFromServer(address, "countyCode");
    }

    /**
     * Query Weather Info By Weather Code
     *
     * @param weatherCode
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = BuildConfig.SERVER_WEATHER_ADDRESS + weatherCode + ".html";
        LogUtil.d(TAG, "Query Weather Info By Weather Code " + address);
        queryFromServer(address, "weatherCode");
    }

    /**
     * Query Weather Code Or Weather Info By Address And Type
     *
     * @param address
     * @param type
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                //"str".equals(var) instead of var.equals("str") avoid var is null
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    LogUtil.d(TAG, "Weather Info " + response);
                    ParseUtil.handleWeatherResponse(WeatherActivity.this, response);
                    //main thread → ui thread → sub thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                tvPublishTime.setText("Synchronize Failed");
            }
        });
    }

    private void initViews() {
        llWeatherInfo = (LinearLayout) findViewById(R.id.ll_weather_info);
        tvCityName = (TextView) findViewById(R.id.tv_city_name);
        tvPublishTime = (TextView) findViewById(R.id.tv_publish_time);
        tvWeatherDesp = (TextView) findViewById(R.id.tv_weather_desp);
        tvTemp1 = (TextView) findViewById(R.id.tv_temp1);
        tvTemp2 = (TextView) findViewById(R.id.tv_temp2);
        tvCurrentDate = (TextView) findViewById(R.id.tv_current_date);
        btnSwitchCity = (Button) findViewById(R.id.btn_switch_city);
        btnRefreshWeather = (Button) findViewById(R.id.btn_refresh_weather);
    }

    private void initEvents() {
        btnSwitchCity.setOnClickListener(this);
        btnRefreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refresh_weather:
                tvPublishTime.setText("Synchronizing...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = preferences.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
