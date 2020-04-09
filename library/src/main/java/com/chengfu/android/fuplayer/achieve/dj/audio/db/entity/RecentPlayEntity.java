package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_play",
        foreignKeys = @ForeignKey(entity = AudioEntity.class, parentColumns = "id", childColumns = "audio_id", onDelete = ForeignKey.CASCADE))
public class RecentPlayEntity {
    @PrimaryKey()
    @ColumnInfo(name = "audio_id")
    @NonNull
    public String audioId;
    @ColumnInfo(name = "play_order")
    public int playOrder;
    @ColumnInfo(name = "date_added")
    public long dateAdded;//添加到数据库的时间
    @ColumnInfo(name = "date_modified")
    public long dateModified;//文件最后修改时间

    @Ignore
    public RecentPlayEntity() {
    }

    @Ignore
    public RecentPlayEntity(@NonNull String audioId, int playOrder) {
        this.audioId = audioId;
        this.playOrder = playOrder;
    }

    public RecentPlayEntity(@NonNull String audioId, int playOrder, long dateAdded, long dateModified) {
        this.audioId = audioId;
        this.playOrder = playOrder;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }

}
