package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(entity = MediaEntity.class, parentColumns = "mediaId", childColumns = "mediaId", onDelete = ForeignKey.CASCADE))
public class RecentPlayEntity {
    @PrimaryKey
    @NonNull
    public String mediaId;
    public long modifiedTime;

    @Ignore
    public RecentPlayEntity(@NonNull String mediaId) {
        this.mediaId = mediaId;
        modifiedTime = System.currentTimeMillis();
    }

    public RecentPlayEntity(@NonNull String mediaId, long modifiedTime) {
        this.mediaId = mediaId;
        this.modifiedTime = modifiedTime;
    }
}
