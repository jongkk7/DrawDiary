package com.nainfox.drawview.view.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nainfox.drawview.R;

public class WeatherPopup extends Activity{
    private final String TAG = "### WeatherPopup";

    private final int WEATHER_RESULT_CODE = 11;
    private final int WEATHER01 = 1;
    private final int WEATHER02 = 2;
    private final int WEATHER03 = 3;
    private final int WEATHER04 = 4;


    private ImageView weather01Button, weather02Button, weather03Button, weather04Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initWindow();

        setContentView(R.layout.popup_weather);





        init();
    }

    private void initWindow(){
        Intent i = getIntent();
        int x = i.getExtras().getInt("x",0);
        int y = i.getExtras().getInt("y",0);

        Log.d(TAG, "x : " + x + ", y : " + y);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

//        Log.d(TAG, "w : " + width + ", height : " + height);
//
//        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = x;
        params.y = -300;

        getWindow().setAttributes(params);
//        getWindow().getAttributes().x = x;
//        getWindow().getAttributes().y = y;
    }

    private void init(){
        weather01Button = (ImageView) findViewById(R.id.weather_button01);
        weather02Button = (ImageView) findViewById(R.id.weather_button02);
        weather03Button = (ImageView) findViewById(R.id.weather_button03);
        weather04Button = (ImageView) findViewById(R.id.weather_button04);

        weather01Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER01);
            }
        });
        weather02Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER02);
            }
        });
        weather03Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER03);
            }
        });
        weather04Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER04);
            }
        });

    }

    private void choice(int type){
        Intent i = new Intent();
        i.putExtra("type", type);
        setResult(WEATHER_RESULT_CODE, i);
        finish();
    }
}
