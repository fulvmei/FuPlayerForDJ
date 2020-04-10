package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist",primaryKeys = "playlist_id")
public class PlaylistEntity {
    @NonNull
    public String playlist_id;
    public String title;

    @Ignore
    public PlaylistEntity(@NonNull String playlist_id) {
        this.playlist_id = playlist_id;
    }

    public PlaylistEntity(@NonNull String playlist_id, String title) {
        this.playlist_id = playlist_id;
        this.title = title;
    }
}
