package com.example.agony.weather.model;

/**
 * Created by Agony on 2015/9/27 0027.
 */
public class County {

    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "County{" +
                "id=" + id +
                ", countryName='" + countyName + '\'' +
                ", countryCode='" + countyCode + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
