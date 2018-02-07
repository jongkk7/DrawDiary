package com.nainfox.drawview.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yjk on 2018. 1. 9..
 */

public class BitmapHelper {
    private final String TAG = "## BitmapHelper";

    // view --> bitmap
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (view instanceof SurfaceView) {
            SurfaceView surfaceView = (SurfaceView) view;
            surfaceView.setZOrderOnTop(true);
            surfaceView.draw(canvas);
            surfaceView.setZOrderOnTop(false);
            return bitmap;
        } else {
            //For ViewGroup & View
            view.draw(canvas);
            return bitmap;
        }
    }

    // bitmap --> byte array
    public byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        return bytes;
    }

    // byte array --> bitmap
    public Bitmap byteArrayToBitmap(byte[] bytes){
        Bitmap bitmap = null;
        bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    // bitmap -> file
    public File BitmapToFile(Bitmap bitmap, String fileName){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Diary";
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        File file = null;
        try{
            file = new File(path, fileName);
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        }catch (IOException e){
            Log.d(TAG, "BitmapToFile Error : " + e.getMessage());
        }catch (Exception e){
            Log.d(TAG, "BitmapToFile Error : " + e.getMessage());
        }

        return file;
    }
}
