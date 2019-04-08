package com.chengfu.android.fuplayer.dj.audio.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // 数据库名
    private static final String DATABASE_NAME = "music.db";

    // 表名
    public static final String MUSIC_TABLE_NAME = "music";

    //数据库版本号
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "id";

    private static final String COLUMN_MEDIA_ID = "media_Id";

    private static final String COLUMN_TITLE = "title";

    private static final String COLUMN_SUBTITLE = "subtitle";

    private static final String COLUMN_DESCRIPTION = "description";

    private static final String COLUMN_ICON = "icon";

    private static final String COLUMN_ICON_URI = "icon_uri";

    private static final String COLUMN_EXTRAS = "extras";

    private static final String COLUMN_MEDIA_URI = "media_uri";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MUSIC_TABLE_NAME +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MEDIA_ID + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_SUBTITLE + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_ICON + " TEXT," +
                COLUMN_ICON_URI + " TEXT," +
                COLUMN_EXTRAS + " TEXT," +
                COLUMN_MEDIA_URI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
