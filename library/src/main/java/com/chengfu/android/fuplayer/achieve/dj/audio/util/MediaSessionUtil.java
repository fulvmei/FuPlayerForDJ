package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MediaSessionUtil {

    public static int getIndexById(@NonNull List<MediaSessionCompat.QueueItem> items, long itemId) {
        for (int i = 0; i < items.size(); i++) {
            MediaSessionCompat.QueueItem item = items.get(i);
            if (item.getQueueId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public static long maxId(List<MediaSessionCompat.QueueItem> list) {
        long maxId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
        if (list == null || list.size() == 0) {
            return maxId;
        }

        for (MediaSessionCompat.QueueItem item : list) {
            if (maxId < item.getQueueId()) {
                maxId = item.getQueueId();
            }
        }
        return maxId;
    }

    public static int search(List<MediaSessionCompat.QueueItem> list, MediaSessionCompat.QueueItem item) {
        if (list == null || list.size() == 0 || item == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i), item) || item.getQueueId() == list.get(i).getQueueId()) {
                return i;
            }
        }
        return -1;
    }

    public static int search(List<MediaSessionCompat.QueueItem> list, MediaDescriptionCompat description) {
        if (list == null || list.size() == 0 || description == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            MediaSessionCompat.QueueItem item = list.get(i);
            if (areItemsTheSame(item.getDescription(), description)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean areItemsTheSame(MediaDescriptionCompat a, MediaDescriptionCompat b) {
        if (a == b) {
            return true;
        }
        if (a != null &&
                b != null &&
                Objects.equals(a.getMediaId(), b.getMediaId())) {
            return true;
        }
        return false;
    }

    public static boolean areContentsTheSame(MediaDescriptionCompat a, MediaDescriptionCompat b) {
        if (a == b) {
            return true;
        }
        if (a != null &&
                b != null &&
                Objects.equals(a.getMediaId(), b.getMediaId()) &&
                Objects.equals(a.getMediaUri(), b.getMediaUri())) {
            return true;
        }
        return false;
    }
}
