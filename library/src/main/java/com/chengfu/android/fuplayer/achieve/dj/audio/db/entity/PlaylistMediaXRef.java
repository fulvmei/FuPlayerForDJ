package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;


@Entity(primaryKeys = {"mediaId", "playlistId"},
        foreignKeys = {@ForeignKey(entity = MediaEntity.class, parentColumns = "mediaId", childColumns = "mediaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PlaylistEntity.class, parentColumns = "playlistId", childColumns = "playlistId", onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "mediaId"), @Index(value = "playlistId"), @Index(value = {"mediaId", "playlistId"}, unique = true)})
public class PlaylistMediaXRef {
    @NonNull
    public String mediaId;
    @NonNull
    public String playlistId;
    public int order;

    @Ignore
    public PlaylistMediaXRef(@NonNull String mediaId, @NonNull String playlistId) {
        this.mediaId = mediaId;
        this.playlistId = playlistId;
    }

    public PlaylistMediaXRef(@NonNull String mediaId, @NonNull String playlistId, int order) {
        this.mediaId = mediaId;
        this.playlistId = playlistId;
        this.order = order;
    }
}
