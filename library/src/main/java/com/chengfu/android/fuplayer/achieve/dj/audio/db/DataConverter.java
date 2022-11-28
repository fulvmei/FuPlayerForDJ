package com.chengfu.android.fuplayer.achieve.dj.audio.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class DataConverter {
    @TypeConverter
    public static Bitmap revertBitmap(byte[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(value, 0, value.length);
    }

    @TypeConverter
    public static byte[] converterBitmap(Bitmap value) {
        if (value == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        value.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @TypeConverter
    public static Bundle revertBundle(byte[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(value, 0, value.length);
        parcel.setDataPosition(0);

        return Bundle.CREATOR.createFromParcel(parcel);
    }

    @TypeConverter
    public static Uri revertUri(byte[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(value, 0, value.length);
        parcel.setDataPosition(0);

        Uri uri=null;
        try {
            uri=Uri.CREATOR.createFromParcel(parcel);
        }catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    @TypeConverter
    public static byte[] converterParcelable(Parcelable value) {
        if (value == null) {
            return null;
        }
        Parcel parcel = Parcel.obtain();
        value.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }
}
