package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

public class MediaSessionUtil {

    public static long maxId(List<MediaSessionCompat.QueueItem> list) {
        MediaSessionCompat.QueueItem result = Collections.max(list, (o1, o2) -> (int) (o1.getQueueId() - o2.getQueueId()));
        return result != null ? result.getQueueId() : MediaSessionCompat.QueueItem.UNKNOWN_ID;
    }

    public static int search(List<MediaSessionCompat.QueueItem> list, MediaDescriptionCompat description) {
        MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, MediaSessionCompat.QueueItem.UNKNOWN_ID);
        return Collections.binarySearch(list, queueItem, (o1, o2) -> TextUtils.equals(o1.getDescription().getMediaId(), o2.getDescription().getMediaId()) ? 0 : -1);
    }
}
