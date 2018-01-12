package com.nainfox.drawview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class SQLHelper {
    private final String TAG = "### SQLHelper";
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public SQLHelper(Context context){
        dbHelper = new DbHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    public long INSERT(String time, String weather, byte[] url, byte[] all_url, String write){
        ContentValues values = new ContentValues();
        values.put(Database.Entry.TIME, time);
        values.put(Database.Entry.WEATHER, weather);
        values.put(Database.Entry.URL, url);
        values.put(Database.Entry.ALL_URL, all_url);
        values.put(Database.Entry.WRITE, write);

        return db.insert(Database.Entry.TABLE_NAME, null, values);
    }

    public Cursor SELECTALL(){
        String[] colume = {
                Database.Entry._ID,
                Database.Entry.TIME,
                Database.Entry.WEATHER,
                Database.Entry.URL,
                Database.Entry.ALL_URL,
                Database.Entry.WRITE
        };

        String sortOrder = Database.Entry._ID + " DESC";

        Cursor c = db.query(
                Database.Entry.TABLE_NAME,
                colume,
                null,
                null,
                null,
                null,
                sortOrder
        );

        return c;
    }

    public void DELETE(String id){
        String selection = Database.Entry._ID + " LIKE ?";
        String[] selctionArgs = { id };

        db.delete(Database.Entry.TABLE_NAME, selection, selctionArgs);
    }

    public int UPDATE(String id, String time, String weather, byte[] url, byte[] all_url, String write) {
        ContentValues values = new ContentValues();
        values.put(Database.Entry.TIME, time);
        values.put(Database.Entry.WEATHER, weather);
        values.put(Database.Entry.URL, url);
        values.put(Database.Entry.ALL_URL, all_url);
        values.put(Database.Entry.WRITE, write);

        String selection = Database.Entry._ID + " LIKE ?";
        String[] selectionArgs = { id };

        int count = db.update(Database.Entry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }

}
