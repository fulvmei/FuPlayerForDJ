package com.chengfu.android.fuplayer.achieve.dj.audio.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.AudioDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.CurrentPlayDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.PlayListDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.PlayListMapDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.dao.RecentPlayDao;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.AudioEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.CurrentPlayEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistMapEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.RecentPlayEntity;


@Database(entities = {AudioEntity.class, PlaylistEntity.class, PlaylistMapEntity.class, CurrentPlayEntity.class, RecentPlayEntity.class}, exportSchema = false, version = 1)
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


    public abstract AudioDao audioDao();

    public abstract PlayListDao playListDao();

    public abstract PlayListMapDao playListMapDao();

    public abstract RecentPlayDao recentPlayDao();

    public abstract CurrentPlayDao currentPlayDao();

}
