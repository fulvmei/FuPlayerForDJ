package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName = "current_play", indices = @Index(value = "media_id"),
        primaryKeys = "media_id",
        foreignKeys = @ForeignKey(entity = MediaEntity.class, parentColumns = "media_id", childColumns = "media_id", onDelete = ForeignKey.CASCADE))
public class CurrentPlayEntity {
    @NonNull
    public String media_id;
    public int position;
    public boolean playing;

    @Ignore
    public CurrentPlayEntity(@NonNull String media_id) {
        this.media_id = media_id;
    }

    @Ignore
    public CurrentPlayEntity(@NonNull String media_id, int position) {
        this.media_id = media_id;
        this.position = position;
    }

    public CurrentPlayEntity(@NonNull String media_id, int position, boolean playing) {
        this.media_id = media_id;
        this.position = position;
        this.playing = playing;
    }
}
