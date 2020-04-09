package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;


@Entity(tableName = "playlist_map",
        primaryKeys = {"audio_id","playlist_id"},
        foreignKeys = {@ForeignKey(entity = AudioEntity.class, parentColumns = "id", childColumns = "audio_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PlaylistEntity.class, parentColumns = "id", childColumns = "playlist_id", onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "audio_id"), @Index(value = "playlist_id"), @Index(value = {"audio_id", "playlist_id"}, unique = true)})
public class PlaylistMapEntity {
    @ColumnInfo(name = "audio_id")
    @NonNull
    public String audioId;
    @ColumnInfo(name = "playlist_id")
    @NonNull
    public String playlistId;
    @ColumnInfo(name = "play_order")
    public int playOrder;

    @Ignore
    public PlaylistMapEntity() {
    }

    @Ignore
    public PlaylistMapEntity( @NonNull String audioId, @NonNull String playlistId) {
        this.audioId = audioId;
        this.playlistId = playlistId;
    }

    public PlaylistMapEntity( @NonNull String audioId, @NonNull String playlistId, int playOrder) {
        this.audioId = audioId;
        this.playlistId = playlistId;
        this.playOrder = playOrder;
    }
}
