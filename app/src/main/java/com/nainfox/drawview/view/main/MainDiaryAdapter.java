package com.nainfox.drawview.view.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nainfox.drawview.R;
import com.nainfox.drawview.util.BitmapHelper;

import java.util.ArrayList;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class MainDiaryAdapter extends RecyclerView.Adapter<MainDiaryAdapter.ViewHolder> {
    private final String TAG = "### MainDiaryAdapter";
    private BitmapHelper bitmapHelper;

    private Context context;
    private ArrayList<byte[]> all_urls;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout layout;
        public ViewHolder(View view){
            super(view);
            this.layout = (LinearLayout) view.findViewById(R.id.item_maindiary_canvas);
        }
    }

    public MainDiaryAdapter(Context context, ArrayList<byte[]> all_urls){
        this.context = context;
        this.all_urls = all_urls;
        bitmapHelper = new BitmapHelper();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_diary, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Log.d(TAG, position + " url : " + all_urls.get(position));

                BitmapHelper bitmapHelper = new BitmapHelper();

                MainCanvasView mainCanvasView = new MainCanvasView(context);
                Bitmap drawBitmap = bitmapHelper.byteArrayToBitmap(all_urls.get(position)).copy(Bitmap.Config.ARGB_8888, true);
                mainCanvasView.setBitmap(drawBitmap);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(drawBitmap.getWidth(), drawBitmap.getHeight());
                holder.layout.setLayoutParams(params);
                holder.layout.addView(mainCanvasView);


        }catch (Exception e){
            Log.e(TAG, "error : " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return all_urls.size();
    }




}
