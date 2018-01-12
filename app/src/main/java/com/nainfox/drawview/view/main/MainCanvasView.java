package com.nainfox.drawview.view.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class MainCanvasView extends View {
    private Bitmap bitmap;

    public MainCanvasView(Context context) {
        super(context);
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 예전 그림 그려주기
        if(bitmap != null){
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }
}
