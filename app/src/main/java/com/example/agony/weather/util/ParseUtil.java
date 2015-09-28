package com.example.agony.weather.util;

import android.text.TextUtils;

import com.example.agony.weather.db.WeatherDB;
import com.example.agony.weather.model.City;
import com.example.agony.weather.model.County;
import com.example.agony.weather.model.Province;

/**
 * Parse And Store Location Data Util
 * Created by Agony on 2015/9/27 0027.
 */
public class ParseUtil {

    /**
     * Handle Provinces Response To Database
     *
     * @param weatherDB
     * @param response
     * @return true if handle success
     */
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handle Cities Response To Database
     *
     * @param weatherDB
     * @param response
     * @param provinceId
     * @return true if handle success
     */
    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String array[] = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handle Counties Response To Database
     *
     * @param weatherDB
     * @param response
     * @param cityId
     * @return true if handle success
     */
    public synchronized static boolean handleCountriesResponse(WeatherDB weatherDB, String response, int cityId) {
        if (TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String array[] = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCountry(county);
                }
                return true;
            }
        }
        return false;
    }
}
