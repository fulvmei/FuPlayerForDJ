package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class AudioPlayClient {
    @NonNull
    private final Context context;
    private final ConnectionCallback connectionCallback;
    private final MediaBrowserCompat mediaBrowser;
    @Nullable
    private MediaControllerCompat mediaController;

    private final ArrayList<MediaDescriptionCompat> pendingList;
    private boolean pendingAutoPlay;
    private String pendingPlayMediaId;

    private final MutableLiveData<Boolean> connected;

    public AudioPlayClient(@NonNull Context context) {
        this.context = context;
        pendingList = new ArrayList<>();
        connected = new MutableLiveData<>();

        connectionCallback = new ConnectionCallback();
        mediaBrowser = new MediaBrowserCompat(context,
                new ComponentName(context, MusicService.class), connectionCallback, null);
    }

    public LiveData<Boolean> getConnected() {
        return connected;
    }

    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    @Nullable
    public MediaControllerCompat getMediaController() {
        return mediaController;
    }

    public void connect() {
        if (!mediaBrowser.isConnected()) {
            mediaBrowser.connect();
        }
    }

    public void disconnect() {
        if (mediaBrowser.isConnected()) {
            mediaBrowser.disconnect();
        }
    }

    public void setPlayListDelay(ArrayList<MediaDescriptionCompat> list, boolean autoPlay) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            setPlayList(list, autoPlay);
        } else {
            pendingAutoPlay = autoPlay;
            pendingList.addAll(list);
        }
    }

    public void setPlayList(ArrayList<MediaDescriptionCompat> list, boolean autoPlay) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS, list);
            mediaController.sendCommand(MusicContract.COMMAND_SET_QUEUE_ITEMS, bundle, null);
            if (autoPlay) {
                mediaController.getTransportControls().play();
            }
        }
        pendingList.clear();
        pendingAutoPlay = false;
    }

    public void appendPlayListDelay(ArrayList<MediaDescriptionCompat> list, boolean autoPlay) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            appendPlayList(list, autoPlay);
        } else {
            pendingAutoPlay = autoPlay;
            pendingList.addAll(list);
        }
    }

    public void appendPlayList(ArrayList<MediaDescriptionCompat> list, boolean autoPlay) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS, list);
            mediaController.sendCommand(MusicContract.COMMAND_ADD_QUEUE_ITEMS, bundle, null);
            mediaController.getTransportControls().play();
            if (autoPlay) {
                mediaController.getTransportControls().play();
            }
        }
        pendingAutoPlay = false;
    }

    public void clearPlayList() {
        if (mediaBrowser.isConnected() && mediaController != null) {
            Bundle bundle = new Bundle();
            mediaController.sendCommand(MusicContract.COMMAND_CLEAR_QUEUE_ITEMS, bundle, null);
        }
    }

    public void playFromMediaIdDelay(String mediaId) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            playFromMediaId(mediaId);
        } else {
            pendingPlayMediaId = mediaId;
        }
    }

    public void playFromMediaId(String mediaId) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            mediaController.getTransportControls().playFromMediaId(mediaId, null);
        }
        pendingPlayMediaId = null;
    }

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
            try {
                mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
                connected.postValue(true);
                if (pendingList.size() > 0) {
                    setPlayList(pendingList, pendingAutoPlay);
                }
                if (!TextUtils.isEmpty(pendingPlayMediaId)) {
                    playFromMediaId(pendingPlayMediaId);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended() {
            connected.postValue(false);
        }

        @Override
        public void onConnectionFailed() {
            connected.postValue(false);
        }
    }
}
