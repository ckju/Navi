package com.amap.track.sql;



import static com.amap.track.sql.IotDatabase.NOTE_TABLE_NAME;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据库管理类
 */

public class DBManager {

    private static final String TAG = DBManager.class.getSimpleName();
    private IotDatabase helper;

    public DBManager(Context context) {
        if (helper == null) {
            helper = new IotDatabase(context);
        }
    }
    /**
     * 插入数据
     *
     * @param title   笔记标题
     * @param time    记录时间
     */
    public long insertNote(String title, String nameEnd, String time, Double latitude,
                           Double longitude, Double latitudeStart, Double longitudeEnd) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("time", time);
        values.put("nameEnd", nameEnd);
        values.put("latitude", String.valueOf(latitude));
        values.put("longitude", String.valueOf(longitude));
        values.put("latitudeStart", String.valueOf(latitudeStart));
        values.put("longitudeEnd", String.valueOf(longitudeEnd));
        return database.insert(NOTE_TABLE_NAME, null, values);
    }
    public List<ListData> queryAll() {
        List<ListData> list = new ArrayList<ListData>();
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.query(NOTE_TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String nameEnd = cursor.getString(cursor.getColumnIndex("nameEnd"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            @SuppressLint("Range") String latitudeStart = cursor.getString(cursor.getColumnIndex("latitudeStart"));
            @SuppressLint("Range") String longitudeEnd = cursor.getString(cursor.getColumnIndex("longitudeEnd"));

            list.add(new ListData(id, title, nameEnd,time,Double.valueOf(latitude),Double.valueOf(longitude),
                    Double.valueOf(latitudeStart),Double.valueOf(longitudeEnd)));
        }

        cursor.close();
        database.close();
        return list;
    }
    public long updateNote(Integer id,String title, String time, String type) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("time", time);
        values.put("latitude", type);
        values.put("longitude", type);
        return database.update(NOTE_TABLE_NAME, values,"id=?",new String[]{String.valueOf(id)});
    }

    public long deleteNote(Integer id) {
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(NOTE_TABLE_NAME, "id=?",new String[]{String.valueOf(id)});
    }

    public List<ListData> query(String key) {
        List<ListData> list = new ArrayList<ListData>();
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.query(NOTE_TABLE_NAME, null, "title like '%" + key + "%' or time like '%" + key + "%'", null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") String longitude = cursor.getString(cursor.getColumnIndex("longitude"));

//            list.add(new ListData(id, title, time,Double.valueOf(latitude),Double.valueOf(longitude)));
        }

        cursor.close();
        database.close();
        return list;
    }

    public List<ListData> queryType(String key) {
        List<ListData> list = new ArrayList<ListData>();
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.query(NOTE_TABLE_NAME, null, "type like " + key, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") String longitude = cursor.getString(cursor.getColumnIndex("longitude"));

//            list.add(new ListData(id, title, time,Double.valueOf(latitude),Double.valueOf(longitude)));
        }

        cursor.close();
        database.close();
        return list;
    }

    public ListData queryById(int key) {
        ListData note = null;
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = database.query(NOTE_TABLE_NAME, null, "id like " + key, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            @SuppressLint("Range") String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") String longitude = cursor.getString(cursor.getColumnIndex("longitude"));

//            note = new ListData(id, title, time,Double.valueOf(latitude),Double.valueOf(longitude));
        }

        cursor.close();
        database.close();
        return note;
    }
}
