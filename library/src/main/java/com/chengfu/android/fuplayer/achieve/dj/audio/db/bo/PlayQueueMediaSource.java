package com.chengfu.android.fuplayer.achieve.dj.audio.db.bo;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class PlayQueueMediaSource {

    private final List<MediaSessionCompat.QueueItem> mediaSourceList;
    private int currentIndex;

    public PlayQueueMediaSource() {
        mediaSourceList = new ArrayList<>();
    }

    public List<MediaSessionCompat.QueueItem> getQueueItemList() {
        return mediaSourceList;
    }

    public int getCount() {
        return mediaSourceList.size();
    }

    public void addMediaSource(MediaSessionCompat.QueueItem mediaSource) {
        addMediaSource(mediaSourceList.size(), mediaSource);
    }

    public void addMediaSource(int index, MediaSessionCompat.QueueItem mediaSource) {
        addMediaSource(index, Collections.singletonList(mediaSource));
    }

    public void addMediaSource(Collection<MediaSessionCompat.QueueItem> mediaSources) {
        addMediaSource(mediaSourceList.size(), mediaSources);
    }

    public void clear() {
        mediaSourceList.clear();
    }

    public void addMediaSource(int index, Collection<MediaSessionCompat.QueueItem> mediaSources) {
        mediaSourceList.addAll(index, mediaSources);
        if (getCount() != 0) {
            currentIndex = 0;
        }
    }

    public MediaSessionCompat.QueueItem getCurrent() {
        if (!checkIndex(currentIndex)) {
            return null;
        }
        return mediaSourceList.get(currentIndex);
    }

    public MediaSessionCompat.QueueItem skipToMediaId(String mediaId) {
        for (int i = 0; i < getCount(); i++) {
            MediaSessionCompat.QueueItem item = mediaSourceList.get(i);
            if (mediaId.endsWith(item.getDescription().getMediaId())) {
                return item;
            }
        }
        return null;
    }

    public boolean hasNext() {
        return currentIndex < getCount() - 1;
    }

    public MediaSessionCompat.QueueItem skipToNext() {
        if (!hasNext()) {
            return null;
        }
        currentIndex++;
        if (!checkIndex(currentIndex)) {
            return null;
        }
        return mediaSourceList.get(currentIndex);
    }

    public boolean hasPrevious() {
        return currentIndex > 0;
    }


    public MediaSessionCompat.QueueItem skipToPrevious() {
        if (!hasPrevious()) {
            return null;
        }
        currentIndex--;
        if (!checkIndex(currentIndex)) {
            return null;
        }
        return mediaSourceList.get(currentIndex);
    }

    private boolean checkIndex(int index) {
        if (index >= 0 && index <= mediaSourceList.size() - 1) {
            return true;
        }
        return false;
    }
}
