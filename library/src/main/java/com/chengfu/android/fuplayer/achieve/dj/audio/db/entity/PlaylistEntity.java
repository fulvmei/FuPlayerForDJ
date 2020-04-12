package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity()
public class PlaylistEntity {
    @PrimaryKey
    @NonNull
    public String playlistId;
    public String title;

    @Ignore
    public PlaylistEntity(@NonNull String playlistId) {
        this.playlistId = playlistId;
    }

    public PlaylistEntity(@NonNull String playlistId, String title) {
        this.playlistId = playlistId;
        this.title = title;
    }
}
