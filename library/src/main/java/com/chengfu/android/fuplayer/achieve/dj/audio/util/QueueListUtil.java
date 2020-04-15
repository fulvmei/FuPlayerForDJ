package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.List;

public class QueueListUtil {

    public static int addMedia(List<MediaSessionCompat.QueueItem> items, MediaDescriptionCompat media) {
        if (items == null || media == null) {
            return 0;
        }
        return addMedia(items, items.size(), media);
    }

    public static int addMedia(List<MediaSessionCompat.QueueItem> items, int index, MediaDescriptionCompat media) {
        if (items == null || index > items.size() || index < 0 || media == null) {
            return 0;
        }
        MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(media, findMaxItemId(items) + 1);
        items.add(index, item);
        return 1;
    }

    public static int addAllMedias(List<MediaSessionCompat.QueueItem> items, List<MediaDescriptionCompat> medias) {
        if (items == null || medias == null || medias.size() == 0) {
            return 0;
        }
        return addAllMedias(items, items.size(), medias);
    }

    public static int addAllMedias(List<MediaSessionCompat.QueueItem> items, int index, List<MediaDescriptionCompat> medias) {
        if (items == null || index > items.size() || index < 0 || medias == null || medias.size() == 0) {
            return 0;
        }
        long maxItemId = findMaxItemId(items);
        List<MediaSessionCompat.QueueItem> temp = new ArrayList<>();
        for (int i = 0; i < medias.size(); i++) {
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(medias.get(i), maxItemId + i + 1);
            temp.add(item);
        }
        items.addAll(index, temp);
        return medias.size();
    }

    public static long findMaxItemId(List<MediaSessionCompat.QueueItem> items) {
        long maxId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
        if (items == null) {
            return maxId;
        }
        for (MediaSessionCompat.QueueItem item : items) {
            if (item.getQueueId() > maxId) {
                maxId = item.getQueueId();
            }
        }
        return maxId;
    }

}
