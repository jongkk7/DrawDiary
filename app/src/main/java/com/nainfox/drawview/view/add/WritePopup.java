package com.nainfox.drawview.view.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.nainfox.drawview.R;
import com.nainfox.drawview.view.common.BasicActivity;

public class WritePopup extends Activity{
    private final String TAG = "### WritePopup";
    private final int WRITE_RESULT_CODE = 10;

    private Button addBtn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_write);

        init();
    }

    private void init(){
        editText = (EditText) findViewById(R.id.text);
        addBtn = (Button) findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                Intent i = new Intent();
                i.putExtra("text", text);
                setResult(WRITE_RESULT_CODE, i);
                finish();
            }
        });
    }

}
