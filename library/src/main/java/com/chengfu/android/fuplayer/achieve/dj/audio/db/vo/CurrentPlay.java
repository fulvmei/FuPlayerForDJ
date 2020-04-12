package com.chengfu.android.fuplayer.achieve.dj.audio.db.vo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.QueueItemEntity;

public class CurrentPlay {

    @Embedded
    public QueueItemEntity entity;

    @Relation(
            parentColumn = "mediaId",
            entityColumn = "mediaId",
            entity = MediaEntity.class)
    public MediaEntity audio;

    @Ignore
    public CurrentPlay() {
    }

    public CurrentPlay(QueueItemEntity entity, MediaEntity audio) {
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
