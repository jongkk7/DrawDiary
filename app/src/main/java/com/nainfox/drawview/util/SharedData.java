package com.nainfox.drawview.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yjk on 2017. 10. 30..
 */

public class SharedData {
    private final String DB = "db";

    private final String ISFIRST = "first"; // first : true

    private final String NAME = "name";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public SharedData(Context context){
        sharedPreferences = context.getSharedPreferences(DB, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    // getter
    public String getName(){
        return sharedPreferences.getString(NAME , "null");
    }
    public boolean getIsFirst(){
        return sharedPreferences.getBoolean(ISFIRST , true);
    }


    // setter
    public void setName(String name) {
        editor.putString(NAME, name);
        editor.commit();
    }
    public void setIsFirst(boolean isFirst) {
        editor.putBoolean(ISFIRST, isFirst);
        editor.commit();
    }


    // reset
    public void reset(){
        editor.putString(NAME, "null");
        editor.putBoolean(ISFIRST, true);

        editor.commit();
    }


}
