package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "media")
public class MediaEntity {
    @PrimaryKey
    @NonNull
    public String mediaId;
    public String title;
    public String subTitle;
    public String description;
    public Bitmap icon;
    public Uri iconUri;
    public Bundle extras;
    public Uri mediaUri;

    @Ignore
    public MediaEntity(@NonNull String mediaId) {
        this.mediaId = mediaId;
    }

    @Ignore
    public MediaEntity(@NonNull String mediaId, String title) {
        this.mediaId = mediaId;
        this.title = title;
    }

    public MediaEntity(@NonNull String mediaId, String title, String subTitle, String description, Bitmap icon, Uri iconUri, Bundle extras, Uri mediaUri) {
        this.mediaId = mediaId;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.icon = icon;
        this.iconUri = iconUri;
        this.extras = extras;
        this.mediaUri = mediaUri;
    }
}
