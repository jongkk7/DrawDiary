package com.nainfox.drawview.view.main;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nainfox.drawview.R;
import com.nainfox.drawview.database.Database;
import com.nainfox.drawview.database.SQLHelper;
import com.nainfox.drawview.view.AddDiaryActivity;
import com.nainfox.drawview.view.common.BasicActivity;

import java.util.ArrayList;

public class MainActivity extends BasicActivity {
        //implements PathRedoUndoCountChangeListener, PathDrawnListener{
    private final String TAG = "### MainActivity";
//    private final int WRITE_RESULT_CODE = 10;
//
//    private DrawView drawView;
//    private RelativeLayout writeLayout, layout;
//    private LoadingPopup popup;
//    private WriteFactory writeFactory;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<byte[]> all_urls;

    private ImageView addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getDiary();
    }

    private void init(){
        all_urls = new ArrayList<>();

        addButton = (ImageView) findViewById(R.id.titlebar_addbutton);
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddDiaryActivity.class);
                startActivity(i);
            }
        });

        checkPermissions();
    }

    private void initRecyclerView(ArrayList<byte[]> all_urls){
        //Log.d(TAG, "all_urls size : " + all_urls.size());
        mRecyclerView = (RecyclerView) findViewById(R.id.diary_recyclerview);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MainDiaryAdapter(this, all_urls);
        mRecyclerView.setAdapter(mAdapter);
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
            Toast.makeText(MainActivity.this, "파일 저장이 가능합니다.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부로 인하여 파일 저장을 사용할 수 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            finish();
        }
    };


    private void getDiary(){
        try {
            all_urls = new ArrayList<>();
            SQLHelper sqlHelper = new SQLHelper(this);
            Cursor cursor = sqlHelper.SELECTALL();

            //Log.d(TAG, "cursor size : " + cursor.getCount());
            cursor.moveToFirst();

            setData(cursor);
            if(cursor != null){
                while(cursor.moveToNext()){
                    setData(cursor);
                }
            }

            initRecyclerView(all_urls);

//            BitmapHelper bitmapHelper = new BitmapHelper();
//
//            LinearLayout layout = (LinearLayout) findViewById(R.id.main_view);
//
//            MainCanvasView mainCanvasView = new MainCanvasView(this);
//            Bitmap drawBitmap = bitmapHelper.byteArrayToBitmap(all_urls.get(0)).copy(Bitmap.Config.ARGB_8888, true);
//            Canvas canvas = new Canvas(drawBitmap);
//            mainCanvasView.setBitmap(drawBitmap);
//
//            layout.addView(mainCanvasView);
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
        all_urls.add(all_url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDiary();
    }
}
