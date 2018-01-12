package com.nainfox.drawview.view.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.nainfox.drawview.R;


public class LoadingPopup extends Dialog {
    public LoadingPopup(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.popup_loading);
    }
}
