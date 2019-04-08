package com.chengfu.android.fuplayer.dj.audio.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;

import com.chengfu.android.fuplayer.audio.FuLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicProvider extends ContentProvider {

    private static final String TAG = "MusicProvider";

    private final Map<String, List<MediaDescriptionCompat>> musicMapData = new HashMap<>();

//    private final MatrixCursor matrixCursor=new MatrixCursor();

    @Override
    public boolean onCreate() {
        FuLog.d(TAG, "onCreate  " + this);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        FuLog.d(TAG, "query  " + this);
        return null;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
