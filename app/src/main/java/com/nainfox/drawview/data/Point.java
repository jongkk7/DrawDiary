package com.nainfox.drawview.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by yjk on 2018. 1. 2..
 */

public class Point implements Parcelable, Serializable {
    static final long serialVersionUID = 42L;

    public float x, y;

    public Point(){
        x = y = -1;
    }

    private Point(Parcel in){
        x = in.readFloat();
        y = in.readFloat();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(x);
        parcel.writeFloat(y);
    }
}
