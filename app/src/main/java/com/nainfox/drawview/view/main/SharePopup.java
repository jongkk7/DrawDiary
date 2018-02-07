package com.nainfox.drawview.view.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.nainfox.drawview.R;

public class SharePopup extends Activity{
    private final String TAG = "### WritePopup";
    private final int SHARE_RESULT_CODE = 13;

    private final int TYPE_SHARE = 100;
    private final int TYPE_MODIFY = 101;
    private final int TYPE_SAVE = 102;
    private final int TYPE_REMOVE = 103;
    private final int TYPE_CANCEL = 104;

    private RelativeLayout cancelBtn;
    private RelativeLayout shareButton, modifyButton, saveButton, removeButton;

    private byte[] all_url, url;
    private int id;
    private String time, write, weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_share);

        getData();
        init();
    }

    private void getData(){
        Intent i = getIntent();
        id = i.getIntExtra("id", -1);
        all_url = i.getByteArrayExtra("all_url");
        url = i.getByteArrayExtra("url");
        time = i.getStringExtra("time");
        write = i.getStringExtra("write");
        weather = i.getStringExtra("weather");
    }

    private void init(){
        shareButton = (RelativeLayout) findViewById(R.id.share_button);
        modifyButton = (RelativeLayout) findViewById(R.id.modify_button);
        saveButton = (RelativeLayout) findViewById(R.id.save_button);
        removeButton = (RelativeLayout) findViewById(R.id.remove_button);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("type", TYPE_SHARE);
                i.putExtra("all_url",all_url);
                setResult(SHARE_RESULT_CODE, i);
                finish();
            }
        });
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("type", TYPE_MODIFY);
                i.putExtra("id", id);
                i.putExtra("url", url);
                i.putExtra("time", time);
                i.putExtra("write", write);
                i.putExtra("weather", weather);
                setResult(SHARE_RESULT_CODE, i);
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("type", TYPE_SAVE);
                i.putExtra("all_url",all_url);
                setResult(SHARE_RESULT_CODE, i);
                finish();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("type", TYPE_REMOVE);
                i.putExtra("id", id);
                setResult(SHARE_RESULT_CODE, i);
                finish();
            }
        });


        cancelBtn = (RelativeLayout) findViewById(R.id.cancelButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("type", TYPE_CANCEL);
                setResult(SHARE_RESULT_CODE, i);
                finish();
            }
        });
    }

}
