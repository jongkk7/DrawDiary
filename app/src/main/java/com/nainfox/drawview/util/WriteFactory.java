package com.nainfox.drawview.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nainfox.drawview.R;

/**
 * Created by yjk on 2018. 1. 4..
 */

public class WriteFactory {
    private final String TAG = "### WriteFactory";

    private TextView[] textViews;

    public WriteFactory(){

    }

    /**
     * 디스플레이의 가로길이 반환
     * @param activity
     * @return
     */
    private int getDisplayWidth(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }


    /**
     * 글씨 적는 공간 ( 동적 생성 )
     * @param activity
     * @param column
     * @param count
     * @return
     */
    public RelativeLayout createWritePlace(Activity activity, int column ,int count){
        RelativeLayout layout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        Typeface typeface = ResourcesCompat.getFont(activity, R.font.misaeng);

        int size = getDisplayWidth(activity) / count;

        textViews = new TextView[count * column + 1];

        for(int j=0 ; j<column ; j++) {
            for (int i = 1; i <= count; i++) {
                int id = j * count + i;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);

                TextView textView = new TextView(activity);
                textView.setId(id);
                textView.setGravity(Gravity.CENTER);
                textView.setText("");
                textView.setTextSize(25);
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(typeface);
                textView.setBackgroundResource(R.drawable.shape_write_place);

                if (i != 1) {
                    params.addRule(RelativeLayout.RIGHT_OF, textViews[id - 1].getId());
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }

                if (j != 0) {
                    params.addRule(RelativeLayout.BELOW, textViews[id - count].getId());
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                }

                textView.setLayoutParams(params);
                textViews[id] = textView;
                layout.addView(textView);

            }
        }
        return layout;
    }


    public void setString(String text){
        try {
            Log.d(TAG , " text : " + text);

            for(int i=1 ; i < textViews.length-1 ; i++){
                textViews[i].setText("");
            }

            for (int i = 1; i <= text.length(); i++) {
                textViews[i].setText(""+text.charAt(i-1));
            }
        }catch (Exception e){
            Log.d(TAG, " error : " + e.getMessage());
        }
    }
}
