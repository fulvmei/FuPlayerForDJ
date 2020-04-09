package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist")
public class PlaylistEntity {
    @PrimaryKey()
    @NonNull
    public String id;
    public String name;
    @ColumnInfo(name = "date_added")
    public long dateAdded;//添加到数据库的时间
    @ColumnInfo(name = "date_modified")
    public long dateModified;//文件最后修改时间

    @Ignore
    public PlaylistEntity() {
    }

    @Ignore
    public PlaylistEntity(String name) {
        this.name = name;
    }

    public PlaylistEntity(String name, long dateAdded, long dateModified) {
        this.name = name;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }

    @Override
    public String toString() {
        return "PlaylistEntity{" +
                "name='" + name + '\'' +
                ", dateAdded=" + dateAdded +
                ", dateModified=" + dateModified +
                '}';
    }
}
