package com.nainfox.drawview.view.main;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nainfox.drawview.R;
import com.nainfox.drawview.database.Database;
import com.nainfox.drawview.database.SQLHelper;
import com.nainfox.drawview.util.BitmapHelper;
import com.nainfox.drawview.util.DateFactory;
import com.nainfox.drawview.view.add.AddDiaryActivity;
import com.nainfox.drawview.view.add.ModifyDiaryActivity;
import com.nainfox.drawview.view.common.BasicActivity;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends BasicActivity{
    private final String TAG = "### MainActivity";
    private final int SHARE_RESULT_CODE = 13;

    private final int TYPE_SHARE = 100;
    private final int TYPE_MODIFY = 101;
    private final int TYPE_SAVE = 102;
    private final int TYPE_REMOVE = 103;
    private final int TYPE_CANCEL = 104;

    private int size = 0;

    private RecyclerView mRecyclerView;
    private MainDiaryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<Integer> ids;
    private ArrayList<byte[]> all_urls;
    private ArrayList<byte[]> urls;
    private ArrayList<String> times;
    private ArrayList<String> titles;
    private ArrayList<String> writes;
    private ArrayList<String> weathers;

    private ImageView addButton;

    private LinearLayout mainView;
    private FrameLayout parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        ids = new ArrayList<>();
        all_urls = new ArrayList<>();
        urls = new ArrayList<>();
        times = new ArrayList<>();
        writes = new ArrayList<>();
        weathers = new ArrayList<>();
        titles = new ArrayList<>();

        addButton = (ImageView) findViewById(R.id.titlebar_addbutton);
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddDiaryActivity.class);
                startActivity(i);
            }
        });

        mainView = (LinearLayout) findViewById(R.id.main_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.diary_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        parentView = (FrameLayout) findViewById(R.id.parentView);
        checkPermissions();
    }

    private void removeList(){
        ids.clear();
        times.clear();
        all_urls.clear();
        urls.clear();
        writes.clear();
        weathers.clear();
        titles.clear();
    }

    private void initRecyclerView(){
        //Log.d(TAG, "all_urls size : " + all_urls.size());
//        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MainDiaryAdapter(this, ids, all_urls, urls, titles, times, writes, weathers);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.invalidate();

        if(size == 1){
            Log.d(TAG ,"size == 1");
            parentView.invalidate();
        }
    }

    private void checkPermissions(){
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("파일 저장을 위해 권한을 설정해주세요.")
                .setDeniedMessage("거부하셨습니다.\n[설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // 권한 허가 -> 블루투스 통신
//            Toast.makeText(MainActivity.this, "파일 저장이 가능합니다.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부로 인하여 파일 저장을 사용할 수 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    };


    private void getDiary(){
        try {
            removeList();

            SQLHelper sqlHelper = new SQLHelper(this);
            Cursor cursor = sqlHelper.SELECTALL();

            Log.d(TAG, "cursor size : " + cursor.getCount());
            size = cursor.getCount();
            if(size == 0){
                Log.d(TAG, "cursor size is null");
                mainView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mRecyclerView.setAdapter(null);
                return;
            }else{
                mainView.setVisibility(View.INVISIBLE);
                cursor.moveToFirst();

                setData(cursor);
                if(cursor != null){
                    while(cursor.moveToNext()){
                        setData(cursor);
                    }
                }

                initRecyclerView();
            }

        }catch (Exception e){
            Log.d(TAG, "getDiary error : " + e.getMessage());
        }
    }

    private void setData(Cursor cursor){
        int _id = cursor.getInt(cursor.getColumnIndex(Database.Entry._ID));
        String time = cursor.getString(cursor.getColumnIndex(Database.Entry.TIME));
        String weather = cursor.getString(cursor.getColumnIndex(Database.Entry.WEATHER));
        byte[] url = cursor.getBlob(cursor.getColumnIndex(Database.Entry.URL));
        byte[] all_url = cursor.getBlob(cursor.getColumnIndex(Database.Entry.ALL_URL));
        String write = cursor.getString(cursor.getColumnIndex(Database.Entry.WRITE));

        String title = getTitle(write);

        ids.add(_id);
        times.add(time);
        all_urls.add(all_url);
        urls.add(url);
        titles.add(title);
        writes.add(write);
        weathers.add(weather);
    }

    private String getTitle(String write){
        StringBuffer title = new StringBuffer();

        int i = 0;
        int j = 0;

        try {
            while (i < 4) {
                if (write.charAt(j) != ' ') {
                    title.append(write.charAt(j));
                    i++;
                }
                j++;
                if(j >= 20){
                    break;
                }
            }
        }catch (Exception e){
//            Log.d(TAG, "getTitle error : " + e.getMessage());
        }

        return title.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume()");
        getDiary();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult()");
        if(resultCode == SHARE_RESULT_CODE){
            int type = data.getIntExtra("type", TYPE_CANCEL);
            byte[] all_url, url;
            String write, weather;

            switch (type){
                case TYPE_SHARE:
                    all_url = data.getByteArrayExtra("all_url");
                    share(all_url);
                    break;
                case TYPE_SAVE:
                    all_url = data.getByteArrayExtra("all_url");
                    saveToFile(all_url);
                    break;
                case TYPE_MODIFY:
                    modify(data);
                    break;
                case TYPE_REMOVE:
                    int position = data.getIntExtra("position", -1);
                    int id = data.getIntExtra("id", -1);
                    SQLHelper sqlHelper = new SQLHelper(this);
                    sqlHelper.DELETE(id+"");
//                    sqlHelper.DELETE_ALL();
//                    mAdapter.notifyItemRemoved(position);
//                    mRecyclerView.invalidate();
//                    mAdapter.removeItem(position);
//                    mAdapter.notifyDataSetChanged();
//                    mRecyclerView.invalidate();
                    break;
                case TYPE_CANCEL:

                    break;
            }
        }
    }

    // 공유
    private void share(byte[] url){
        BitmapHelper bitmapHelper = new BitmapHelper();
        Bitmap drawBitmap = bitmapHelper.byteArrayToBitmap(url).copy(Bitmap.Config.ARGB_8888, true);

        File file = new BitmapHelper().BitmapToFile(drawBitmap,"diery_sample.png");
        Uri uri = Uri.fromFile(file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent,"공유"));
    }

    // 저장
    private void saveToFile(byte[] url){
        BitmapHelper bitmapHelper = new BitmapHelper();
        Bitmap drawBitmap = bitmapHelper.byteArrayToBitmap(url).copy(Bitmap.Config.ARGB_8888, true);

        String fileName = "Diary" + new DateFactory().getTime() + ".png";
        Log.d(TAG, "fileName : " + fileName);
        File screemShot = new BitmapHelper().BitmapToFile(drawBitmap, fileName);
        if(screemShot != null){
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screemShot)));
        }else{
            Log.d(TAG, "file null..");
        }
    }

    private void modify(Intent data){
        int id = data.getIntExtra("id", -1);
        byte[] url = data.getByteArrayExtra("url");
        String time = data.getStringExtra("time");
        String weather = data.getStringExtra("weather");
        String write = data.getStringExtra("write");

        Log.d(TAG, "modify time : " + time);

        Intent i = new Intent(MainActivity.this, ModifyDiaryActivity.class);
        i.putExtra("id", id);
        i.putExtra("url", url);
        i.putExtra("time", time);
        i.putExtra("weather", weather);
        i.putExtra("write", write);
        startActivity(i);

    }
}
