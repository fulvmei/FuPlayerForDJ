package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;


public class QueueAdapterImpl implements QueueAdapter {

    private final CopyOnWriteArraySet<DataChangedListener> dataChangedListeners;

    private final List<MediaSessionCompat.QueueItem> queueItemList;
    private int activePosition;

    public QueueAdapterImpl() {
        dataChangedListeners = new CopyOnWriteArraySet<>();
        queueItemList = new ArrayList<>();
        activePosition = -1;
    }

    @Override
    public void addDataChangedListener(DataChangedListener listener) {
        dataChangedListeners.add(listener);
    }

    @Override
    public void removeDataChangedListener(DataChangedListener listener) {
        dataChangedListeners.remove(listener);
    }

    @Override
    public void add(MediaSessionCompat.QueueItem item) {
        add(getItemCount(), item);
    }

    @Override
    public void add(int index, MediaSessionCompat.QueueItem item) {
        if (checkIndex(index)) {
            queueItemList.add(index, item);
            notifyDataChanged();
        }
    }

    @Override
    public void addAll(Collection<MediaSessionCompat.QueueItem> items) {
        addAll(getItemCount(), items);
    }

    @Override
    public void addMedia(MediaDescriptionCompat media) {
        addMedia(media);
    }

    @Override
    public void addMedia(int index, MediaDescriptionCompat media) {
        if (media == null) {
            return;
        }
        MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(media, findMaxItemId() + 1);
        add(index, item);
    }

    @Override
    public void addAllMedias(List<MediaDescriptionCompat> medias) {
        addAllMedias(0, medias);
    }

    @Override
    public void addAllMedias(int index, List<MediaDescriptionCompat> medias) {
        if (medias == null || medias.size() == 0) {
            return;
        }
        List<MediaSessionCompat.QueueItem> items = new ArrayList<>();
        long maxItemId = findMaxItemId();
        for (int i = 0; i < medias.size(); i++) {
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(medias.get(i), findMaxItemId() + i + 1);
            items.add(item);
        }
        addAll(index, items);
    }

    @Override
    public void addAll(int index, Collection<MediaSessionCompat.QueueItem> items) {
        if (items != null && items.size() > 0) {
            queueItemList.addAll(index, items);
            notifyDataChanged();
        }
    }

    @Override
    public boolean remove(int position) {
        if (checkIndex(position)) {
            if (queueItemList.remove(position) != null) {
                notifyDataChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(MediaSessionCompat.QueueItem item) {
        if (queueItemList.remove(item)) {
            notifyDataChanged();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<MediaSessionCompat.QueueItem> items) {
        if (queueItemList.remove(items)) {
            notifyDataChanged();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (!queueItemList.isEmpty()) {
            queueItemList.clear();
            notifyDataChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        if (!checkIndex(position)) {
            return MediaSessionCompat.QueueItem.UNKNOWN_ID;
        }
        return queueItemList.get(position).getQueueId();
    }

    @Override
    public MediaSessionCompat.QueueItem getItem(int position) {
        if (!checkIndex(position)) {
            return null;
        }
        return queueItemList.get(position);
    }

    @Override
    public int getPositionByItemId(long itemId) {
        for (int i = 0; i < queueItemList.size(); i++) {
            MediaSessionCompat.QueueItem item = queueItemList.get(i);
            if (itemId == item.getQueueId()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getPositionByMediaId(String mediaId) {
        for (int i = 0; i < queueItemList.size(); i++) {
            MediaSessionCompat.QueueItem item = queueItemList.get(i);
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return queueItemList.size();
    }

    @Override
    public List<MediaSessionCompat.QueueItem> getCurrentList() {
        return queueItemList;
    }

    @Override
    public long getActiveId() {
        if (!checkIndex(activePosition)) {
            return MediaSessionCompat.QueueItem.UNKNOWN_ID;
        }
        return getItemId(activePosition);
    }

    @Override
    public void setActiveId(long activeId) {
        int position = getPositionByItemId(activeId);
        if (position != activePosition) {
            notifyActiveItemChanged(position);
        }
    }

    @Override
    public MediaSessionCompat.QueueItem getActiveItem() {
        if (!checkIndex(activePosition)) {
            return null;
        }
        return getItem(activePosition);
    }

    @Override
    public void setActiveItem(@NonNull MediaSessionCompat.QueueItem item) {
        int position = getPositionByItemId(item.getQueueId());
        if (position != activePosition) {
            notifyActiveItemChanged(position);
        }
    }

    @Override
    public void setActivePosition(int position) {
        if (checkIndex(position)) {
            notifyActiveItemChanged(position);
        }
    }

    @Override
    public MediaSessionCompat.QueueItem skipToItemId(long itemId) {
        int position = getPositionByItemId(itemId);
        notifyActiveItemChanged(position);
        return getItem(position);
    }

    @Override
    public MediaSessionCompat.QueueItem skipToMediaId(String mediaId) {
        int position = getPositionByMediaId(mediaId);
        notifyActiveItemChanged(position);
        return getItem(position);
    }

    @Override
    public boolean hasNext() {
        return activePosition < getItemCount() - 1;
    }

    @Override
    public MediaSessionCompat.QueueItem skipToNext() {
        if (!hasNext()) {
            return null;
        }
        int position = activePosition + 1;
        notifyActiveItemChanged(position);
        return getItem(position);
    }

    @Override
    public boolean hasPrevious() {
        return activePosition > 0;
    }

    @Override
    public MediaSessionCompat.QueueItem skipToPrevious() {
        if (!hasPrevious()) {
            return null;
        }
        int position = activePosition - 1;
        notifyActiveItemChanged(position);
        return getItem(position);
    }

    @Override
    public boolean checkIndex(int position) {
        if (position >= 0 && position <= getItemCount() - 1) {
            return true;
        }
        return false;
    }

    @Override
    public long findMaxItemId() {
        long maxId = 0;
        for (MediaSessionCompat.QueueItem item : queueItemList) {
            if (item.getQueueId() > maxId) {
                maxId = item.getQueueId();
            }
        }
        return maxId;
    }

    private void notifyDataChanged() {
        for (DataChangedListener l : dataChangedListeners) {
            l.onDataChanged(queueItemList);
        }
    }

    private void notifyActiveItemChanged(int position) {
        if (activePosition == position) {
            return;
        }
        activePosition = position;
        for (DataChangedListener l : dataChangedListeners) {
            l.onActiveItemChanged(getActiveItem());
        }
    }

}
