package com.nainfox.drawview.view.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.nainfox.drawview.R;

public class SavePopup extends Activity{
    private final String TAG = "### WritePopup";
    private final int SAVE_RESULT_CODE = 14;
    private final String SAVE_KEY = "save";

    private final String YES = "yes";
    private final String NO = "no";

    private Button yesButton;
    private Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_save);

        init();
    }


    private void init(){
        yesButton = (Button) findViewById(R.id.yesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra(SAVE_KEY, YES);
                setResult(SAVE_RESULT_CODE, i);
                finish();
            }
        });
        noButton = (Button) findViewById(R.id.noButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra(SAVE_KEY, NO);
                setResult(SAVE_RESULT_CODE, i);
                finish();
            }
        });

    }

}
