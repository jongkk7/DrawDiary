package com.nainfox.drawview.view.add;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nainfox.drawview.R;
import com.nainfox.drawview.data.ColorData;
import com.nainfox.drawview.database.SQLHelper;
import com.nainfox.drawview.draw.DrawView;
import com.nainfox.drawview.draw.PathDrawnListener;
import com.nainfox.drawview.draw.PathRedoUndoCountChangeListener;
import com.nainfox.drawview.util.BitmapHelper;
import com.nainfox.drawview.util.DateFactory;
import com.nainfox.drawview.util.SizeFactory;
import com.nainfox.drawview.util.WriteFactory;
import com.nainfox.drawview.view.common.BasicActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ModifyDiaryActivity extends BasicActivity implements PathRedoUndoCountChangeListener, PathDrawnListener{
    private final String TAG = "### ModifyDiaryActivity";

    private final int WRITE_RESULT_CODE = 10;
    private final int EXIT_RESULT_CODE = 12;
    private final int SAVE_RESULT_CODE = 14;

    private final String TEXT_KEY = "text";
    private final String EXIT_KEY = "exit";
    private final String SAVE_KEY = "save";

    private final String YES = "yes";
    private final String NO = "no";

    private final int PEN_STYLE = 1;
    private final int CRAYON_STYLE = 2;
    private final int ERASER_STYLE = 3;
    private final int COLOR_STYLE = 4;

    private final int WEATHER01 = 1;
    private final int WEATHER02 = 2;
    private final int WEATHER03 = 3;
    private final int WEATHER04 = 4;

    private final String gray = "#eeeeee";
    private final String darkgray = "#aaaaaa";

    // 글씨 판
    private final int row = 10;
    private final int colume = 2;

    // 현재 선택된 색 , 기존의 색 ( 지우개 사용시 사용 )
    private String currentColor = ColorData.color10; // black;

    // 뒤로가기, 저장하기
    private ImageView backButton;
    private TextView saveButton;

    // 그림일기 레이아웃
    private FrameLayout diaryLayout;

    // 날짜
    private LinearLayout dateLayout;
    private TextView yearTextView, monthTextView, dayTextView, dateTextView;

    // 날씨
    private ImageView weatherButton, weatherButton01, weatherButton02, weatherButton03, weatherButton04;
    private LinearLayout weatherLayout;


    // 그림판
    private DrawView drawView;

    // 글씨
    private RelativeLayout writeLayout, layout;
    private WriteFactory writeFactory;

    // 하단 버튼들
    private LinearLayout bottomLayout;
    private SeekBar seekbar, eraserSeekbar;
    private ImageView redoButton, undoButton;
    private ImageView pen1, pen2;
    private ImageView eraserButton;
    private ImageView[] colorButtons;
    private ImageView colorButton;
    private Button eraserAllButton;
    private LinearLayout colorLayout, eraserLayout;


    private int curruntPen = 1;
    private Animation upAnim1, upAnim2, upAnim3, downAnim1, downAnim2, downAnim3;


    private BitmapHelper bitmapHelper;
    private int id = -1;
    private byte[] url;
    private String weather, time, write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        init();
    }

    private void init(){
        Intent data = getIntent();

        id = data.getIntExtra("id", -1);
        url = data.getByteArrayExtra("url");
        time = data.getStringExtra("time");
        weather = data.getStringExtra("weather");
        write = data.getStringExtra("write");

        bitmapHelper = new BitmapHelper();

        // 그림일기 세로 사이즈 조정
        diaryLayout = (FrameLayout) findViewById(R.id.fragment_diary_layout);
        SizeFactory sizeFactory = new SizeFactory();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeFactory.getWindowWidth(this), sizeFactory.getWindowWidth(this));
        diaryLayout.setLayoutParams(params);

        backButton = (ImageView) findViewById(R.id.titlebar_backbutton);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ModifyDiaryActivity.this, ExitPopup.class);
                startActivityForResult(i, EXIT_RESULT_CODE);
            }
        });

        saveButton = (TextView) findViewById(R.id.titlebar_savebutton);
        saveButton.setVisibility(View.VISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherLayout.setVisibility(View.INVISIBLE);
                Intent i = new Intent(ModifyDiaryActivity.this, SavePopup.class);
                startActivityForResult(i, SAVE_RESULT_CODE);
            }
        });

        drawView = (DrawView) findViewById(R.id.drawview);
        drawView.setOnPathDrawnListener(this);
        drawView.setPathRedoUndoCountChangeListener(this);
        drawView.setOldBitmap(bitmapHelper.byteArrayToBitmap(url));

        MotionEvent motionEvent = MotionEvent.obtain(
                0,
                100,
                MotionEvent.ACTION_DOWN,
                1,
                1,
                0
        );
        drawView.dispatchTouchEvent(motionEvent);


        initWriteLayout();
        initDateLayout();
        initWeatherButton();
        initBottomLayout();
    }


    // 글씨 작성하는 레이아웃 초기화
    private void initWriteLayout(){
        writeLayout = (RelativeLayout) findViewById(R.id.writeLayout);

        writeFactory = new WriteFactory();
        layout = writeFactory.createWritePlace(this, colume, row);
        writeLayout.addView(layout);
        writeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ModifyDiaryActivity.this, WritePopup.class);
                startActivityForResult(i, WRITE_RESULT_CODE);
//                try {
//                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.RESULT_UNCHANGED_SHOWN);
//
//                }catch (Exception e){
//                    Log.d(TAG, "e : " + e.getMessage());
//                }
            }
        });
        writeFactory.setString(write);
    }

    // 날짜 선택 레이아웃
    private void initDateLayout(){
        yearTextView = (TextView) findViewById(R.id.date_year_textview);
        monthTextView = (TextView) findViewById(R.id.date_month_textview);
        dayTextView = (TextView) findViewById(R.id.date_day_textview);
        dateTextView = (TextView) findViewById(R.id.date_date_textview);

        Log.d(TAG, "time : " + time);
        final DateFactory dateFactory = new DateFactory();
        String[] times = time.split("_");

        final int mYear = Integer.parseInt(times[0]);
        final int mMonth = Integer.parseInt(times[1]);
        final int mDay = Integer.parseInt(times[2]);

        yearTextView.setText(mYear+"");
        monthTextView.setText(mMonth+"");
        dayTextView.setText(mDay+"");
        dateTextView.setText(dateFactory.getDayOfWeek());

        dateLayout = (LinearLayout) findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ModifyDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateFactory.setCalendar(year, month, day);

                        yearTextView.setText(year+"");
                        monthTextView.setText((month+1)+"");
                        dayTextView.setText(day+"");
                        dateTextView.setText(dateFactory.getDayOfWeek());
                    }
                },mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    // 날씨 선택 초기화
    private void initWeatherButton(){
        weatherLayout = (LinearLayout) findViewById(R.id.weather_layout);
        weatherButton = (ImageView) findViewById(R.id.weather_button);
        weatherButton01 = (ImageView) findViewById(R.id.weather_button01);
        weatherButton02 = (ImageView) findViewById(R.id.weather_button02);
        weatherButton03 = (ImageView) findViewById(R.id.weather_button03);
        weatherButton04 = (ImageView) findViewById(R.id.weather_button04);

        choice(Integer.parseInt(weather));
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "weather Button Clicked");
                weatherLayout.setVisibility(View.VISIBLE);
                weatherLayout.setZ(999);
            }
        });

        weatherButton01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER01);
            }
        });
        weatherButton02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER02);
            }
        });
        weatherButton03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER03);
            }
        });
        weatherButton04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice(WEATHER04);
            }
        });
    }

    // 해당 날씨에 맞춰 날씨 변경
    private void choice(int type){
        switch (type){
            case 1:
                weatherButton.setBackgroundResource(R.drawable.draw_weather_sun_icon);
                weather = "1";
                break;
            case 2:
                weatherButton.setBackgroundResource(R.drawable.draw_weather_cloud_icon);
                weather = "2";
                break;
            case 3:
                weatherButton.setBackgroundResource(R.drawable.draw_weather_snow_icon);
                weather = "3";
                break;
            case 4:
                weatherButton.setBackgroundResource(R.drawable.draw_weather_rain_icon);
                weather = "4";
                break;
        }
        weatherLayout.setVisibility(View.INVISIBLE);
    }

    // 팬 스타일, 지우개, 배경색, redo & undo , 굵기, 색
    private void initBottomLayout(){
        // 애니메이션 초기화
        upAnim1 = AnimationUtils.loadAnimation(this, R.anim.anim_up);
        upAnim2 = AnimationUtils.loadAnimation(this, R.anim.anim_up);
        upAnim3 = AnimationUtils.loadAnimation(this, R.anim.anim_up);
        downAnim1 = AnimationUtils.loadAnimation(this, R.anim.anim_down);
        downAnim2 = AnimationUtils.loadAnimation(this, R.anim.anim_down);
        downAnim3 = AnimationUtils.loadAnimation(this, R.anim.anim_down);

        colorLayout = (LinearLayout) findViewById(R.id.colorLayout);
        eraserLayout = (LinearLayout) findViewById(R.id.eraserLayout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_button_layout);
        seekbar = (SeekBar) findViewById(R.id.text_size_seekbar);
        seekbar.getThumb().setColorFilter(Color.parseColor("#FF000000"), PorterDuff.Mode.SRC_IN);
        eraserSeekbar = (SeekBar) findViewById(R.id.eraser_seekbar);
        eraserSeekbar.getThumb().setColorFilter(Color.parseColor("#FF000000"), PorterDuff.Mode.SRC_IN);
        eraserAllButton = (Button) findViewById(R.id.eraser_all_button);

        initColorButtons();

        pen1 = (ImageView) findViewById(R.id.bottom_pen1_button);
        pen2 = (ImageView) findViewById(R.id.bottom_pen2_button);
        eraserButton = (ImageView) findViewById(R.id.bottom_pen3_button);
        redoButton = (ImageView) findViewById(R.id.bottom_redo_button);
        undoButton = (ImageView) findViewById(R.id.bottom_undo_button);
        colorButton = (ImageView) findViewById(R.id.bottom_color_button);
        colorButton.setColorFilter(Color.parseColor("#FF000000"));

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                drawView.setPaintWidthDp(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        eraserSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                drawView.setPaintWidthDp(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        pen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curruntPen != PEN_STYLE){
                    setLayoutToPen(PEN_STYLE);
                }
            }
        });
        pen2.startAnimation(downAnim2);
        pen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curruntPen != CRAYON_STYLE){
                    setLayoutToPen(CRAYON_STYLE);
                }
            }
        });

        eraserButton.startAnimation(downAnim3);
        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curruntPen != ERASER_STYLE){
                    setLayoutToPen(ERASER_STYLE);
                }
            }
        });
        eraserAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clearAll();
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.undoLast();
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.redoLast();
            }
        });

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curruntPen != COLOR_STYLE){
                    setLayoutToPen(COLOR_STYLE);
                }
            }
        });
    }

    // 컬러 버튼 초기화
    private void initColorButtons(){
        colorButtons = new ImageView[12];

        colorButtons[0] = (ImageView) findViewById(R.id.color00);
        colorButtons[1] = (ImageView) findViewById(R.id.color01);
        colorButtons[2] = (ImageView) findViewById(R.id.color02);
        colorButtons[3] = (ImageView) findViewById(R.id.color03);
        colorButtons[4] = (ImageView) findViewById(R.id.color04);
        colorButtons[5] = (ImageView) findViewById(R.id.color05);
        colorButtons[6] = (ImageView) findViewById(R.id.color06);
        colorButtons[7] = (ImageView) findViewById(R.id.color07);
        colorButtons[8] = (ImageView) findViewById(R.id.color08);
        colorButtons[9] = (ImageView) findViewById(R.id.color09);
        colorButtons[10] = (ImageView) findViewById(R.id.color10);
        colorButtons[11] = (ImageView) findViewById(R.id.color11);

        ColorData colorData = new ColorData();
        colorButtons[0].setOnClickListener(new ColorButtonClickListener(colorData.color00));
        colorButtons[1].setOnClickListener(new ColorButtonClickListener(colorData.color01));
        colorButtons[2].setOnClickListener(new ColorButtonClickListener(colorData.color02));
        colorButtons[3].setOnClickListener(new ColorButtonClickListener(colorData.color03));
        colorButtons[4].setOnClickListener(new ColorButtonClickListener(colorData.color04));
        colorButtons[5].setOnClickListener(new ColorButtonClickListener(colorData.color05));
        colorButtons[6].setOnClickListener(new ColorButtonClickListener(colorData.color06));
        colorButtons[7].setOnClickListener(new ColorButtonClickListener(colorData.color07));
        colorButtons[8].setOnClickListener(new ColorButtonClickListener(colorData.color08));
        colorButtons[9].setOnClickListener(new ColorButtonClickListener(colorData.color09));
        colorButtons[10].setOnClickListener(new ColorButtonClickListener(colorData.color10));
        colorButtons[11].setOnClickListener(new ColorButtonClickListener(colorData.color11));

    }


    private class ColorButtonClickListener implements View.OnClickListener{

        private String color;
        public ColorButtonClickListener(String color){
            this.color = color;
        }

        @Override
        public void onClick(View view) {
//            Log.d(TAG, " color : " + color);
            currentColor = color;
            drawView.setPaintColor(Color.parseColor(color));
            colorButton.setColorFilter(Color.parseColor(color));
        }
    }

    /**
     * 현재 선택된 팬에 따라 레이아웃 변경
     * 1: 연필
     * 2: 크래파스
     * 3: 지우개
     * 4: 컬러
     */
    private void setLayoutToPen(int pen){
        //Log.d(TAG, "current pen : " + curruntPen + " , pen : " + pen);
        setDownAnim(curruntPen);

        bottomLayout.setBackgroundColor(Color.parseColor(gray));
        colorButton.setBackgroundColor(Color.parseColor("#00000000"));

        switch (pen){
            case 1:
                drawView.setPaintStyle(pen);
                drawView.setPaintColor(Color.parseColor(currentColor));
                pen1.startAnimation(upAnim1);

                seekbar.setVisibility(View.VISIBLE);
                drawView.setPaintWidthDp(seekbar.getProgress());
                colorLayout.setVisibility(View.INVISIBLE);
                eraserLayout.setVisibility(View.INVISIBLE);
                break;
            case 2:
                drawView.setPaintStyle(pen);
                drawView.setPaintColor(Color.parseColor(currentColor));
                pen2.startAnimation(upAnim2);

                seekbar.setVisibility(View.VISIBLE);
                drawView.setPaintWidthDp(seekbar.getProgress());
                colorLayout.setVisibility(View.INVISIBLE);
                eraserLayout.setVisibility(View.INVISIBLE);
                break;
            case 3:
                drawView.setPaintStyle(pen);
                eraserButton.startAnimation(upAnim3);

                seekbar.setVisibility(View.INVISIBLE);
                drawView.setPaintWidthDp(eraserSeekbar.getProgress());
                colorLayout.setVisibility(View.INVISIBLE);
                eraserLayout.setVisibility(View.VISIBLE);
                break;
            case 4:
                // color
                bottomLayout.setBackgroundColor(Color.parseColor(darkgray));
                colorButton.setBackgroundColor(Color.parseColor(gray));
                seekbar.setVisibility(View.INVISIBLE);
                drawView.setPaintWidthDp(seekbar.getProgress());
                colorLayout.setVisibility(View.VISIBLE);
                eraserLayout.setVisibility(View.INVISIBLE);
                break;
        }

        this.curruntPen = pen;
    }
    private void setDownAnim(int pen){
        switch (pen){
            case 1:
                Log.d(TAG, "pen1 down");
                pen1.startAnimation(downAnim1);
                break;
            case 2:
                Log.d(TAG, "pen2 down");
                pen2.startAnimation(downAnim2);
                break;
            case 3:
                Log.d(TAG, "eraser down");
                eraserButton.startAnimation(downAnim3);
                break;
        }
    }



    /**
     * 날짜, 날씨, 그림, 일기 저장
     * SQLite로 저장 후 메인화면에서 View
      */
    private void save(){
        BitmapHelper bitmapHelper = new BitmapHelper();

        // drawView ( canvas만 )
        String time = yearTextView.getText().toString() + "_" + monthTextView.getText().toString() +  "_" + dayTextView.getText().toString();
        Bitmap parentviewBitmap = bitmapHelper.viewToBitmap(diaryLayout);
        Bitmap childViewBitap = bitmapHelper.viewToBitmap(drawView);
        byte[] all_url = bitmapHelper.bitmapToByteArray(parentviewBitmap);
        byte[] url = bitmapHelper.bitmapToByteArray(childViewBitap);

        if(write == null){
            Toast.makeText(this, "글씨를 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SQLHelper sqlHelper = new SQLHelper(this);
            long result = sqlHelper.UPDATE(String.valueOf(id), time, weather, url, all_url, write);
            if(result < 0){
                Toast.makeText(this, "저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "저장 완료.", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){
            Toast.makeText(this, "저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToFile(){
        String fileName = "Diary" + new DateFactory().getTime();
        File screemShot = makeFileToBitmap(diaryLayout , fileName);
        if(screemShot != null){
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screemShot)));
        }
    }

    private File makeFileToBitmap(View view, String fileName){
        view.setDrawingCacheEnabled(true);

        Bitmap bitmap = view.getDrawingCache();

        File file = new File(Environment.getExternalStorageDirectory()+"/Diary");
        if(!file.exists()){
            file.mkdir();
        }
        File diaryFile = new File(Environment.getExternalStorageDirectory()+"/Diary"+fileName);
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(diaryFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
        }catch (IOException e){
            Log.d(TAG, "make file error : " + e.getMessage());
        }
        view.setDrawingCacheEnabled(false);
        return diaryFile;
    }

    private void share(){
        File file = saveFileToView(diaryLayout);
        Uri uri = Uri.fromFile(file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent,"공유"));
    }

    // view로부터 bitmap을 얻어 파일을 저장한다.
    private File saveFileToView(View view){
        view.setDrawingCacheEnabled(true);

        Bitmap screenBitmap = view.getDrawingCache();

        String filename = "screenshot.png";
        File root_file = new File(Environment.getExternalStorageDirectory() + "/Diary");
        if(!root_file.exists()){
            root_file.mkdir();
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/Diary", filename);
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
        }catch (IOException e){
            Log.d(TAG, "IOException : " + e.getMessage());
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(ModifyDiaryActivity.this, ExitPopup.class);
        startActivityForResult(i, EXIT_RESULT_CODE);
    }

    /**
     * 1. 작성한 글씨 받아서 writeLayout에 뿌려준다.
     * 2. 날씨 선택
     * 3. 저장 / 공유 / 수정
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == WRITE_RESULT_CODE){
            write = data.getStringExtra(TEXT_KEY);
            writeFactory.setString(write);
        }else if(resultCode == EXIT_RESULT_CODE){
            String exit = data.getStringExtra(EXIT_KEY);
            if(exit.equals("yes")){
                finish();
            }else{

            }
        }else if(resultCode == SAVE_RESULT_CODE){
            String save = data.getStringExtra(SAVE_KEY);
            if(save.equals(YES)){
                save();
            }else{

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPathStart() {

    }

    @Override
    public void onNewPathDrawn() {

    }

    @Override
    public void onUndoCountChanged(int undoCount) {

    }

    @Override
    public void onRedoCountChanged(int redoCount) {

    }
}
