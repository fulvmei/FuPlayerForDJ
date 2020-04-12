package com.chengfu.android.fuplayer.achieve.dj.audio.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.MediaDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.CurrentPlayDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.PlayListDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.PlayListMapDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.RecentPlayDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.QueueItemEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistMediaXRef;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.RecentPlayEntity;


@Database(entities = {MediaEntity.class, PlaylistEntity.class, PlaylistMediaXRef.class, QueueItemEntity.class, RecentPlayEntity.class}, exportSchema = false, version = 1)
@TypeConverters(DataConverter.class)
public abstract class AudioDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "audio_database";

    private static AudioDatabase instance;

    public static synchronized AudioDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), AudioDatabase.class, DATABASE_NAME)
                    .enableMultiInstanceInvalidation()
                    .build();
        }
        return instance;
    }

    public static void switchToInMemory(Context context) {
        instance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AudioDatabase.class)
                .enableMultiInstanceInvalidation()
                .build();
    }


    public abstract MediaDao audioDao();

    public abstract PlayListDao playListDao();

    public abstract PlayListMapDao playListMapDao();

    public abstract RecentPlayDao recentPlayDao();

    public abstract CurrentPlayDao currentPlayDao();

}
