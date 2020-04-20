package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;

import java.util.ArrayList;
import java.util.List;

public class QueueListUtil {

    public static int addToCurrentPlay(@NonNull ConcatenatingMediaSource mediaSource, @NonNull List<MediaSessionCompat.QueueItem> items, long activeQueueItemId, MediaDescriptionCompat media, @NonNull DataSource.Factory dataSourceFactory) {
        System.out.println("activeQueueItemId=" + getPositionById(items,activeQueueItemId));
        return 1;
    }

    public static int addQueueItem(@NonNull ConcatenatingMediaSource mediaSource, @NonNull List<MediaSessionCompat.QueueItem> items, MediaDescriptionCompat media, @NonNull DataSource.Factory dataSourceFactory) {
        return addQueueItem(mediaSource, items, items.size(), media, dataSourceFactory);
    }

    public static int addQueueItem(@NonNull ConcatenatingMediaSource mediaSource, @NonNull List<MediaSessionCompat.QueueItem> items, int index, MediaDescriptionCompat media, @NonNull DataSource.Factory dataSourceFactory) {
        if (mediaSource.getSize() != items.size()
                || media == null) {
            return 0;
        }
        if (index > items.size()) {
            index = items.size();
        }
        if (index < 0) {
            index = 0;
        }
        MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(media, findMaxItemId(items) + 1);
        mediaSource.addMediaSource(index,ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), "", dataSourceFactory, item));
        items.add(index, item);
        return 1;
    }

    public static int removeQueueItem(@NonNull ConcatenatingMediaSource mediaSource, @NonNull List<MediaSessionCompat.QueueItem> items, MediaDescriptionCompat media) {
        if (mediaSource.getSize() != items.size()
                || media == null) {
            return 0;
        }
        int index = getPositionByMediaId(items, media.getMediaId());
        if (index == -1) {
            return 0;
        }
        items.remove(index);
        mediaSource.removeMediaSource(index);
        return 1;
    }

    public static int addQueueItems(@NonNull ConcatenatingMediaSource mediaSource, @NonNull List<MediaSessionCompat.QueueItem> items, List<MediaDescriptionCompat> medias, @NonNull DataSource.Factory dataSourceFactory) {
        return addQueueItems(mediaSource, items, items.size(), medias, dataSourceFactory);
    }

    public static int addQueueItems(@NonNull ConcatenatingMediaSource mediaSource, @NonNull List<MediaSessionCompat.QueueItem> items, int index, List<MediaDescriptionCompat> medias, @NonNull DataSource.Factory dataSourceFactory) {
        if (mediaSource.getSize() != items.size()
                || medias == null || medias.size() == 0) {
            return 0;
        }
        if (index > items.size()) {
            index = items.size();
        }
        if (index < 0) {
            index = 0;
        }
        long maxItemId = findMaxItemId(items);
        List<MediaSessionCompat.QueueItem> tempItems = new ArrayList<>();
        List<MediaSource> tempMediaSources = new ArrayList<>();
        for (int i = 0; i < medias.size(); i++) {
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(medias.get(i), maxItemId + i + 1);
            tempItems.add(item);
            tempMediaSources.add(ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), "", dataSourceFactory, item));
        }
        items.addAll(index, tempItems);
        mediaSource.addMediaSources(index, tempMediaSources);
        return medias.size();
    }


    public static long findMaxItemId(@NonNull List<MediaSessionCompat.QueueItem> items) {
        long maxId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
        for (MediaSessionCompat.QueueItem item : items) {
            if (item.getQueueId() > maxId) {
                maxId = item.getQueueId();
            }
        }
        return maxId;
    }

    public static int getPositionById(@NonNull List<MediaSessionCompat.QueueItem> items, long itemId) {
        for (int i = 0; i < items.size(); i++) {
            MediaSessionCompat.QueueItem item = items.get(i);
            if (item.getQueueId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public static int getPositionByMediaId(@NonNull List<MediaSessionCompat.QueueItem> items, String mediaId) {
        if (mediaId == null) {
            return -1;
        }
        for (int i = 0; i < items.size(); i++) {
            MediaSessionCompat.QueueItem item = items.get(i);
            if (TextUtils.equals(mediaId, item.getDescription().getMediaId())) {
                return i;
            }
        }
        return -1;
    }
}
