package com.example.agony.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.agony.weather.model.City;
import com.example.agony.weather.model.County;
import com.example.agony.weather.model.Province;
import com.example.agony.weather.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton DAO
 * Created by Agony on 2015/9/27 0027.
 */
public class WeatherDB {

    public static final String TAG = "WeatherDB";

    public static final String DB_NAME = "weather";
    public static final int DB_VERSION = 1;

    private static WeatherDB weatherDB;

    private SQLiteDatabase db;

    /**
     * Singleton Constructor
     *
     * @param context ApplicationContext
     */
    private WeatherDB(Context context) {
        LocationOpenHelper dbHelper = new LocationOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Get DAO instance
     *
     * @param context ApplicationContext
     * @return WeatherDB instance
     */
    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    /**
     * save province to database
     *
     * @param province
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("province", null, values);
        }
        LogUtil.d(TAG, "Save Province To Database " + province.toString());
    }

    /**
     * get provinces form database
     *
     * @return provinces
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        LogUtil.d(TAG, "Load Province From Database " + list.toString());
        return list;
    }

    /**
     * save city to database
     *
     * @param city cities
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("city", null, values);
        }
        LogUtil.d(TAG, "Save City To Database " + city.toString());
    }

    /**
     * get cities from database
     *
     * @param provinceId
     * @return cities
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("city", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        LogUtil.d(TAG, "Load City From Database " + list.toString());
        return list;
    }

    /**
     * save county to database
     *
     * @param county
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("county", null, values);
        }
        LogUtil.d(TAG, "Save County To Database " + county.toString());
    }

    /**
     * get counties from database
     *
     * @param cityId
     * @return counties
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("county", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        LogUtil.d(TAG, "Load County From Database " + list.toString());
        return list;
    }
}
