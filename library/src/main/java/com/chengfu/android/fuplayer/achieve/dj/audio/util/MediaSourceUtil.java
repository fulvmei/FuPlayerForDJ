package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;

import java.util.ArrayList;
import java.util.List;

public class MediaSourceUtil {

    public static List<MediaSource> buildMediaSource(List<MediaSessionCompat.QueueItem> items, @NonNull DataSource.Factory dataSourceFactory) {
        List<MediaSource> mediaSources = new ArrayList<>();
        if (items == null) {
            return mediaSources;
        }
        for (MediaSessionCompat.QueueItem item : items) {
            MediaDescriptionCompat description = item.getDescription();
            MediaSource mediaSource = ExoMediaSourceUtil.buildMediaSource(description.getMediaUri(), "", dataSourceFactory, item);
            mediaSources.add(mediaSource);
        }
        return mediaSources;
    }
}
