package com.chengfu.android.fuplayer.achieve.dj.audio.db.entity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "media",primaryKeys = "media_id")
public class MediaEntity {
    @NonNull
    public String media_id;
    public String title;
    public String sub_title;
    public String description;
    public Bitmap icon;
    public Uri icon_uri;
    public Bundle extras;
    public Uri media_uri;

    @Ignore
    public MediaEntity(@NonNull String media_id) {
        this.media_id = media_id;
    }

    @Ignore
    public MediaEntity(@NonNull String media_id, String title) {
        this.media_id = media_id;
        this.title = title;
    }

    public MediaEntity(@NonNull String media_id, String title, String sub_title, String description, Bitmap icon, Uri icon_uri, Bundle extras, Uri media_uri) {
        this.media_id = media_id;
        this.title = title;
        this.sub_title = sub_title;
        this.description = description;
        this.icon = icon;
        this.icon_uri = icon_uri;
        this.extras = extras;
        this.media_uri = media_uri;
    }
}
