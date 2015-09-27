package com.example.agony.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Locale the province&city&country
 * Created by Agony on 2015/9/27 0027.
 */
public class LocaleOpenHelper extends SQLiteOpenHelper {

    //SQL
    public static final String CREATE_PROVINCE = "create table province(" +
            "id integer primary key antoincrement," +
            "province_name text," +
            "province_code text)";
    public static final String CREATE_CITY = "create table city(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";
    public static final String CREATE_COUNTRY = "create table country(" +
            "id integer primary key autoincrement," +
            "country_name text," +
            "country_code text," +
            "city_id integer)";

    public LocaleOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // execute SQL
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
