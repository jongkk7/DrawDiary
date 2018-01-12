package com.nainfox.drawview.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class FileHelper {

    //화면 캡쳐하기
    public File screenShot(View view){
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        File root = new File(Environment.getExternalStorageDirectory()+"/DrawClass");
        if(!root.exists()){
            root.mkdirs();
        }
        DateFactory dateFactory = new DateFactory();
        String filename = "screenshot"+ dateFactory.getTime() +".png";
        File file = new File(Environment.getExternalStorageDirectory()+"/DrawClass", filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }
}
