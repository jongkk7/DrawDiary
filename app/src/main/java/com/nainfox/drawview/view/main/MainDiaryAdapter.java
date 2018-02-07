package com.nainfox.drawview.view.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.nainfox.drawview.R;
import com.nainfox.drawview.util.BitmapHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class MainDiaryAdapter extends RecyclerView.Adapter<MainDiaryAdapter.ViewHolder> {
    private final String TAG = "### MainDiaryAdapter";
    private final int SHARE_RESULT_CODE = 13;
    private final int TYPE_SHARE = 100;
    private final int TYPE_MODIFY = 101;
    private final int TYPE_SAVE = 102;
    private final int TYPE_CANCEL = 103;

    private Activity context;
    private ArrayList<Integer> ids;
    private ArrayList<byte[]> all_urls;
    private ArrayList<byte[]> urls;
    private ArrayList<String> times;
    private ArrayList<String> titles;
    private ArrayList<String> writes;
    private ArrayList<String> weathers;



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView layout;
        public TextView title_textview;
        public Button button;

        public ViewHolder(View view){
            super(view);
            this.layout = (ImageView) view.findViewById(R.id.item_maindiary_canvas);
            this.title_textview = (TextView) view.findViewById(R.id.item_maindiary_title);
            this.button = (Button) view.findViewById(R.id.item_maindiary_button);
        }
    }

    public MainDiaryAdapter(Activity context, ArrayList<Integer> ids, ArrayList<byte[]> all_urls, ArrayList<byte[]> urls, ArrayList<String> titles, ArrayList<String> times, ArrayList<String> writes, ArrayList<String> weathers){
        this.context = context;
        this.ids = ids;
        this.all_urls = all_urls;
        this.urls = urls;
        this.times = times;
        this.titles = titles;
        this.writes = writes;
        this.weathers = weathers;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_diary, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            BitmapHelper bitmapHelper = new BitmapHelper();
            Bitmap drawBitmap = bitmapHelper.byteArrayToBitmap(all_urls.get(position)).copy(Bitmap.Config.ARGB_8888, true);

            holder.layout.setImageBitmap(drawBitmap);
            holder.title_textview.setText(titles.get(position));
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SharePopup.class);
                    intent.putExtra("position",position);
                    intent.putExtra("id", ids.get(position));
                    intent.putExtra("all_url", all_urls.get(position));
                    intent.putExtra("url", urls.get(position));
                    intent.putExtra("time",times.get(position));
                    intent.putExtra("write",writes.get(position));
                    intent.putExtra("weather",weathers.get(position));
                    context.startActivityForResult(intent, SHARE_RESULT_CODE);
                }
            });
        }catch (Exception e){
            Log.e(TAG, "error : " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return all_urls.size();
    }


    public void removeItem(int position){
        try{
            notifyItemRemoved(position);
        }catch (Exception e){
            Log.d(TAG, "remove error : " + e.getMessage());
        }
    }


}
