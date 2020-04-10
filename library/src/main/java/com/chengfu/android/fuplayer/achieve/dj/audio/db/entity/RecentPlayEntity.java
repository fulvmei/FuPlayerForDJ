package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(tableName = "recent_play",
        primaryKeys = "media_id",
        foreignKeys = @ForeignKey(entity = MediaEntity.class, parentColumns = "media_id", childColumns = "media_id", onDelete = ForeignKey.CASCADE))
public class RecentPlayEntity {
    @NonNull
    public String media_id;
    public int position;

    @Ignore
    public RecentPlayEntity(@NonNull String media_id) {
        this.media_id = media_id;
    }

    public RecentPlayEntity(@NonNull String media_id, int position) {
        this.media_id = media_id;
        this.position = position;
    }
}
