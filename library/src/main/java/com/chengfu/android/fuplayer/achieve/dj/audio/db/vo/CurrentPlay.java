package com.chengfu.android.fuplayer.achieve.dj.audio.db.vo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.AudioEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.CurrentPlayEntity;

public class CurrentPlay {

    @Embedded
    public CurrentPlayEntity entity;

    @Relation(
            parentColumn = "audio_id",
            entityColumn = "id",
            entity = AudioEntity.class)
    public AudioEntity audio;

    @Ignore
    public CurrentPlay() {
    }

    public CurrentPlay(CurrentPlayEntity entity, AudioEntity audio) {
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
