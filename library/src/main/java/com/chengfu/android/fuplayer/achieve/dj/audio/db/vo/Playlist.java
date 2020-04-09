package com.chengfu.android.fuplayer.achieve.dj.audio.db.vo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;


import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.AudioEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.PlaylistMapEntity;

import java.util.List;

public class Playlist {

    @Embedded
    public PlaylistEntity playlist;

    @Relation(
            parentColumn = "id",
            entity = AudioEntity.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value = PlaylistMapEntity.class,
                    parentColumn = "playlist_id",
                    entityColumn = "audio_id"))
    public List<AudioEntity> audioList;

    @Ignore
    public Playlist() {
    }

    public Playlist(PlaylistEntity playlist, List<AudioEntity> audioList) {
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
