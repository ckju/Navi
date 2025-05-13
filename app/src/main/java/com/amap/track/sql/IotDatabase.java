package com.amap.track.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class IotDatabase extends SQLiteOpenHelper {

    public static final String NOTE_TABLE_NAME = "Iot";
    private static final String DB_NAME = "Iot.db";
    private static final int V = 1;

    public IotDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, V);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NOTE_TABLE_NAME
                + "(id integer primary key autoincrement, title varchar, nameEnd varchar,time varchar, " +
                "latitude varchar, longitude longitude, latitudeStart varchar, longitudeEnd longitude)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}