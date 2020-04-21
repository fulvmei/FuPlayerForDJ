package com.chengfu.music.player.ui.player;

import android.support.v4.media.session.MediaSessionCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class AudioPlayViewModel extends ViewModel {

    private final MutableLiveData<List<MediaSessionCompat.QueueItem>> queueItemsLiveData;
    private final MutableLiveData<Long> activeQueueItemIdLiveData;

    public AudioPlayViewModel() {
        queueItemsLiveData = new MutableLiveData<>();
        activeQueueItemIdLiveData = new MutableLiveData<>();
    }

    public LiveData<List<MediaSessionCompat.QueueItem>> getQueueItems() {
        return queueItemsLiveData;
    }

    public void setPlayList(List<MediaSessionCompat.QueueItem> queueItems) {
        queueItemsLiveData.postValue(queueItems);
    }

    public LiveData<Long> getActiveQueueItemId() {
        return activeQueueItemIdLiveData;
    }

    public void setActiveQueueItemId(long activeQueueItemId) {
        activeQueueItemIdLiveData.postValue(activeQueueItemId);
    }
}
