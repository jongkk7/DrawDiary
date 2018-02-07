package com.nainfox.drawview.view.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.nainfox.drawview.R;

public class ExitPopup extends Activity{
    private final String TAG = "### ExitPopup";
    private final int EXIT_RESULT_CODE = 12;

    private final String EXIT_KEY = "exit";


    private Button yesBtn, noBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_exit);

        init();
    }

    private void init(){
        yesBtn = (Button) findViewById(R.id.addButton);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra(EXIT_KEY, "yes");
                setResult(EXIT_RESULT_CODE, i);
                finish();
            }
        });
        noBtn = (Button) findViewById(R.id.cancelButton);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra(EXIT_KEY, "no");
                setResult(EXIT_RESULT_CODE, i);
                finish();
            }
        });
    }

}
