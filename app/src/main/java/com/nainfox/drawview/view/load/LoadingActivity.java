package com.nainfox.drawview.view.load;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.nainfox.drawview.R;
import com.nainfox.drawview.util.SharedData;
import com.nainfox.drawview.view.common.BasicActivity;
import com.nainfox.drawview.view.main.MainActivity;

public class LoadingActivity extends BasicActivity{

    private AnimationDrawable animationDrawable;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        init();

        checkIsFirst();
    }

    // 첫실행 여부
    private void checkIsFirst(){
        SharedData sharedData = new SharedData(this);
        if(sharedData.getIsFirst()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoadingActivity.this, TutorialActivity.class);
                    startActivity(i);
                    finish();
                }
            },2000);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            },2000);
        }
    }


    private void init(){
        imageView = (ImageView) findViewById(R.id.loadingView);
        imageView.setBackgroundResource(R.drawable.anim_loading);

        animationDrawable = (AnimationDrawable)imageView.getBackground();
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            animationDrawable.start();
        }else{
            animationDrawable.stop();
        }
    }
}
