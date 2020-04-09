package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "current_play", indices = @Index(value = "audio_id"),
        foreignKeys = @ForeignKey(entity = AudioEntity.class, parentColumns = "id", childColumns = "audio_id", onDelete = ForeignKey.CASCADE))
public class CurrentPlayEntity {
    @PrimaryKey()
    @ColumnInfo(name = "audio_id")
    @NonNull
    public String audioId;
    @ColumnInfo(name = "play_order")
    public int playOrder;
    @ColumnInfo(name = "playing")
    public boolean playing;
    @ColumnInfo(name = "date_added")
    public long dateAdded;//添加到数据库的时间
    @ColumnInfo(name = "date_modified")
    public long dateModified;//文件最后修改时间

    @Ignore
    public CurrentPlayEntity() {
    }

    @Ignore
    public CurrentPlayEntity(@NonNull String audioId, int playOrder) {
        this.audioId = audioId;
        this.playOrder = playOrder;
    }

    @Ignore
    public CurrentPlayEntity(@NonNull String audioId, int playOrder, boolean playing) {
        this.audioId = audioId;
        this.playOrder = playOrder;
        this.playing = playing;
    }

    public CurrentPlayEntity(@NonNull String audioId, int playOrder, boolean playing, long dateAdded, long dateModified) {
        this.audioId = audioId;
        this.playOrder = playOrder;
        this.playing = playing;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }
}
