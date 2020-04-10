package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;


@Entity(tableName = "playlist_map",
        primaryKeys = {"media_id", "playlist_id"},
        foreignKeys = {@ForeignKey(entity = MediaEntity.class, parentColumns = "media_id", childColumns = "media_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PlaylistEntity.class, parentColumns = "playlist_id", childColumns = "playlist_id", onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "media_id"), @Index(value = "playlist_id"), @Index(value = {"media_id", "playlist_id"}, unique = true)})
public class PlaylistMapEntity {
    @NonNull
    public String media_id;
    @NonNull
    public String playlist_id;
    public int order;

    @Ignore
    public PlaylistMapEntity(@NonNull String media_id, @NonNull String playlist_id) {
        this.media_id = media_id;
        this.playlist_id = playlist_id;
    }

    public PlaylistMapEntity(@NonNull String media_id, @NonNull String playlist_id, int order) {
        this.media_id = media_id;
        this.playlist_id = playlist_id;
        this.order = order;
    }
}
