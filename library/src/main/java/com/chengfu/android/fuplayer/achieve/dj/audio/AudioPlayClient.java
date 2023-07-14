package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.RecentPlay;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;

import java.util.ArrayList;
import java.util.List;

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

    public AudioPlayClient(@NonNull Context context, @NonNull Class<?> service) {
        this(context, service.getName());
    }

    public AudioPlayClient(@NonNull Context context, @NonNull String service) {
        this.context = context;
        pendingList = new ArrayList<>();
        connected = new MutableLiveData<>();

        connectionCallback = new ConnectionCallback();
        mediaBrowser = new MediaBrowserCompat(context,
                new ComponentName(context, service), connectionCallback, null);
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

    public void addToCurrentPlay(MediaDescriptionCompat media) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MusicContract.KEY_QUEUE_ITEM, media);
            mediaController.sendCommand(MusicContract.COMMAND_ADD_TO_TO_FRONT_OF_CURRENT_PLAY, bundle, null);
            mediaController.getTransportControls().playFromMediaId(media.getMediaId(), null);
        }
    }

    public void addToNextPlay(MediaDescriptionCompat media) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MusicContract.KEY_QUEUE_ITEM, media);
            mediaController.sendCommand(MusicContract.COMMAND_ADD_AFTER_CURRENT_PLAY, bundle, null);
        }
    }

    public void addItem(MediaDescriptionCompat media) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MusicContract.KEY_QUEUE_ITEM, media);
            mediaController.addQueueItem(media);
        }
    }

    public void addItem(MediaDescriptionCompat media,int index) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            mediaController.addQueueItem(media,index);
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

    public void playFromItemId(long id) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            mediaController.getTransportControls().skipToQueueItem(id);
        }
        pendingPlayMediaId = null;
    }

    public void playFromMediaId(String mediaId) {
        if (mediaBrowser.isConnected() && mediaController != null) {
            mediaController.getTransportControls().playFromMediaId(mediaId, null);
        }
        pendingPlayMediaId = null;
    }

    public static void addToRecentList(@NonNull Context context, @NonNull MediaDescriptionCompat media) {
        new Thread(() -> {
            MediaEntity entity = ConverterUtil.mediaDescriptionToMediaEntity(media);
            DataBaseManager.addToRecentList(context, entity);
        }).start();
    }

    public static LiveData<List<MediaDescriptionCompat>> getRecentList(@NonNull Context context) {
        LiveData<List<RecentPlay>> recentPlayList = AudioDatabase.getInstance(context).recentPlayDao().getRecentPlayList();
        return Transformations.map(recentPlayList, input -> {
            List<MediaDescriptionCompat> medias = new ArrayList<>();
            for (RecentPlay item : input) {
                medias.add(ConverterUtil.mediaEntityToMediaDescription(item.audio));
            }
            return medias;
        });
    }

    public static LiveData<List<MediaDescriptionCompat>> getRecentList(@NonNull Context context,int limit) {
        LiveData<List<RecentPlay>> recentPlayList = AudioDatabase.getInstance(context).recentPlayDao().getRecentPlayList(limit);
        return Transformations.map(recentPlayList, input -> {
            List<MediaDescriptionCompat> medias = new ArrayList<>();
            for (RecentPlay item : input) {
                medias.add(ConverterUtil.mediaEntityToMediaDescription(item.audio));
            }
            return medias;
        });
    }

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
//            try {
                mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
                connected.postValue(true);
                if (pendingList.size() > 0) {
                    setPlayList(pendingList, pendingAutoPlay);
                }
                if (!TextUtils.isEmpty(pendingPlayMediaId)) {
                    playFromMediaId(pendingPlayMediaId);
                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
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
