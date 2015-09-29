package com.example.agony.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agony.weather.BuildConfig;
import com.example.agony.weather.R;
import com.example.agony.weather.db.WeatherDB;
import com.example.agony.weather.model.City;
import com.example.agony.weather.model.County;
import com.example.agony.weather.model.Province;
import com.example.agony.weather.util.HttpCallbackListener;
import com.example.agony.weather.util.HttpUtil;
import com.example.agony.weather.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agony on 2015/9/27 0027.
 */
public class ChooseAreaActivity extends Activity {

    public static final String TAG = "ChooseAreaActivity";

    //Control Level
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //Database
    private WeatherDB weatherDB;

    //UI
    private ProgressDialog progressDialog;
    private TextView tvTitle;
    private ListView lvList;

    //Data Source
    private ArrayAdapter<String> adapter;
    //Current Data Source
    private List<String> dataList = new ArrayList<>();
    //Select Data Source
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //Select Level
    private Province selectedProvince;
    private City selectedCity;

    //Current Level
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("city_selected", false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        //This
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        lvList = (ListView) findViewById(R.id.lv_list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, dataList);
        lvList.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //Default
        queryProvinces();
    }

    /**
     * Query Provinces From Database First Then Server To Show In UI
     */
    private void queryProvinces() {
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvList.setSelection(0);
            tvTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * Query Cities From Database First Then Server To Show In UI
     */
    private void queryCities() {
        cityList = weatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvList.setSelection(0);
            tvTitle.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * Query Counties From Database First Then Server To Show In UI
     */
    private void queryCounties() {
        countyList = weatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvList.setSelection(0);
            tvTitle.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }


    /**
     * Query From Server(Thread) To Store In Database
     *
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        //Almost Not Null
        if (!TextUtils.isEmpty(code)) {
            address = BuildConfig.SERVER_CITY_ADDRESS + code + ".xml";
        } else {
            address = BuildConfig.SERVER_CITY_ADDRESS + ".xml";
        }
        showProgressDialog();
        //Anonymous Is Simple Then Implements
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = ParseUtil.handleProvincesResponse(weatherDB, response);
                } else if ("city".equals(type)) {
                    result = ParseUtil.handleCitiesResponse(weatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = ParseUtil.handleCountriesResponse(weatherDB, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "Load Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //Adpater
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
