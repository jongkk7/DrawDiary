package com.nainfox.drawview.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by yjk on 2018. 1. 10..
 */

public class SizeFactory {

    public int getWindowWidth(Activity activity){
        DisplayMetrics dm = activity.getApplicationContext().getResources().getDisplayMetrics();

        return dm.widthPixels;
    }
}
