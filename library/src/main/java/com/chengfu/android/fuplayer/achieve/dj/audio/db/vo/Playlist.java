package com.chengfu.android.fuplayer.achieve.dj.audio.db.vo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;


import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistMapEntity;

import java.util.List;

public class Playlist {

    @Embedded
    public PlaylistEntity playlist;

    @Relation(
            parentColumn = "playlist_id",
            entity = MediaEntity.class,
            entityColumn = "media_id",
            associateBy = @Junction(
                    value = PlaylistMapEntity.class,
                    parentColumn = "playlist_id",
                    entityColumn = "media_id"))
    public List<MediaEntity> audioList;

    @Ignore
    public Playlist() {
    }

    public Playlist(PlaylistEntity playlist, List<MediaEntity> audioList) {
        this.playlist = playlist;
        this.audioList = audioList;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlist=" + playlist +
                ", audioList=" + audioList +
                '}';
    }
}
