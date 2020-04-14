package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;

public interface QueueAdapter {

    interface DataChangedListener {
        void onDataChanged(List<MediaSessionCompat.QueueItem> list);

        void onActiveItemChanged(MediaSessionCompat.QueueItem item);
    }

    void addDataChangedListener(DataChangedListener listener);

    void removeDataChangedListener(DataChangedListener listener);

    void add(MediaSessionCompat.QueueItem item);

    void add(int index, MediaSessionCompat.QueueItem item);

    void addAll(Collection<MediaSessionCompat.QueueItem> items);

    void addMedia(MediaDescriptionCompat media);

    void addMedia(int index,MediaDescriptionCompat media);

    void addAllMedias(List<MediaDescriptionCompat> medias);

    void addAllMedias(int index,List<MediaDescriptionCompat> medias);

    void addAll(int index, Collection<MediaSessionCompat.QueueItem> items);

    boolean remove(int position);

    boolean remove(MediaSessionCompat.QueueItem item);

    boolean removeAll(Collection<MediaSessionCompat.QueueItem> items);

    void clear();

    long getItemId(int position);

    MediaSessionCompat.QueueItem getItem(int position);

    int getPositionByItemId(long itemId);

    int getPositionByMediaId(String mediaId);

    int getItemCount();

    List<MediaSessionCompat.QueueItem> getCurrentList();

    long getActiveId();

    void setActiveId(long activeId);

    MediaSessionCompat.QueueItem getActiveItem();

    void setActiveItem(MediaSessionCompat.QueueItem item);

    void setActivePosition(int position);

    MediaSessionCompat.QueueItem skipToItemId(long itemId);

    MediaSessionCompat.QueueItem skipToMediaId(@NonNull String mediaId);

    boolean hasNext();

    MediaSessionCompat.QueueItem skipToNext();

    boolean hasPrevious();

    MediaSessionCompat.QueueItem skipToPrevious();

    boolean checkIndex(int position);

     long findMaxItemId();

}
