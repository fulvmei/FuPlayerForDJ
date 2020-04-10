package com.chengfu.android.fuplayer.achieve.dj.audio.db.vo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.CurrentPlayEntity;

public class CurrentPlay {

    @Embedded
    public CurrentPlayEntity entity;

    @Relation(
            parentColumn = "media_id",
            entityColumn = "media_id",
            entity = MediaEntity.class)
    public MediaEntity audio;

    @Ignore
    public CurrentPlay() {
    }

    public CurrentPlay(CurrentPlayEntity entity, MediaEntity audio) {
        this.entity = entity;
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "CurrentPlay{" +
                "entity=" + entity +
                ", audio=" + audio +
                '}';
    }
}
