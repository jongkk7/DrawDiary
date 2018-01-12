package com.nainfox.drawview.database;

import android.provider.BaseColumns;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class Database {
    private  Database() {}

    public static class Entry implements BaseColumns{
        public static final String TABLE_NAME = "diary";
        public static final String TIME = "time";
        public static final String WEATHER  = "weather";
        public static final String URL = "url";
        public static final String ALL_URL = "allurl";
        public static final String WRITE = "write";
    }



    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Entry.TIME + TEXT_TYPE + COMMA_SEP +
                    Entry.WEATHER + TEXT_TYPE + COMMA_SEP +
                    Entry.URL + BLOB_TYPE + COMMA_SEP +
                    Entry.ALL_URL + BLOB_TYPE + COMMA_SEP +
                    Entry.WRITE + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;
}
