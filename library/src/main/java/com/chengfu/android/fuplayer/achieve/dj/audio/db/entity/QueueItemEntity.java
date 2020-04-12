package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = @Index(value = "mediaId"),
        foreignKeys = @ForeignKey(entity = MediaEntity.class, parentColumns = "mediaId", childColumns = "mediaId", onDelete = ForeignKey.CASCADE))
public class QueueItemEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String mediaId;
    public int position;

    public QueueItemEntity() {
    }

    @Ignore
    public QueueItemEntity(@NonNull String media_id) {
        this.mediaId = media_id;
    }

    @Ignore
    public QueueItemEntity(@NonNull String media_id, int position) {
        this.mediaId = media_id;
        this.position = position;
    }



}
