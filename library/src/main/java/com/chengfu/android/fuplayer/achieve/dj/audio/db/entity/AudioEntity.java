package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "audio")
public class AudioEntity {
    @PrimaryKey()
    @NonNull
    public String id;
//    public String uid;
    public String data;//绝对路径
    public String name;//文件名
    public long size;//文件大小，单位 byte
    public long duration;// 时长
    @ColumnInfo(name = "artist_id")
    public int artistId;// 艺术家 id
    @ColumnInfo(name = "album_id")
    public int albumId;// 专辑 id
    @ColumnInfo(name = "date_added")
    public long dateAdded;//添加到数据库的时间
    @ColumnInfo(name = "date_modified")
    public long dateModified;//文件最后修改时间
    public String tag;

    @Ignore
    public AudioEntity(@NonNull String id) {
        this.id = id;
    }

    @Ignore
    public AudioEntity(@NonNull String id,String data, String name) {
        this.id = id;
        this.data = data;
        this.name = name;
    }

    @Ignore
    public AudioEntity(@NonNull String id, String data, String name, long size, long duration, String tag) {
        this.id = id;
        this.data = data;
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.tag = tag;
    }

    public AudioEntity(@NonNull String id, String data, String name, long size, long duration, int artistId, int albumId, long dateAdded, long dateModified, String tag) {
        this.id = id;
        this.data = data;
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.artistId = artistId;
        this.albumId = albumId;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "AudioEntity{" +
                "id='" + id + '\'' +
                ", data='" + data + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", artistId=" + artistId +
                ", albumId=" + albumId +
                ", dateAdded=" + dateAdded +
                ", dateModified=" + dateModified +
                ", tag='" + tag + '\'' +
                '}';
    }
}
