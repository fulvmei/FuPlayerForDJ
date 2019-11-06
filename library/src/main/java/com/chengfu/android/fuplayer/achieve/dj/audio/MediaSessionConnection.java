package com.chengfu.android.fuplayer.achieve.dj.audio;

import androidx.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

public class MediaSessionConnection {

    private static final PlaybackStateCompat EMPTY_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build();

    private static final MediaMetadataCompat NOTHING_PLAYING = new MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build();

    private Context context;
    private ComponentName serviceComponent;

    private MutableLiveData<Boolean> connected = new MutableLiveData<>();


    private final MutableLiveData<PlaybackStateCompat> playbackState = new MutableLiveData<>();

    private final MutableLiveData<MediaMetadataCompat> nowPlaying = new MutableLiveData<>();

    private MediaBrowserConnectionCallback mediaBrowserConnectionCallback;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;

    public MediaSessionConnection(Context context, ComponentName serviceComponent) {
        this.context = context;
        this.serviceComponent = serviceComponent;

        connected.postValue(false);

        mediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();

        mediaBrowser = new MediaBrowserCompat(context,
                serviceComponent,
                mediaBrowserConnectionCallback, null);

        mediaBrowser.connect();
    }

    public MutableLiveData<Boolean> getConnected() {
        return connected;
    }

    public MutableLiveData<PlaybackStateCompat> getPlaybackState() {
        return playbackState;
    }

    public MutableLiveData<MediaMetadataCompat> getNowPlaying() {
        return nowPlaying;
    }

    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    public MediaControllerCompat getMediaController() {
        return mediaController;
    }

    public void subscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
        mediaBrowser.subscribe(parentId, callback);
    }

    public void unsubscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
        mediaBrowser.unsubscribe(parentId, callback);
    }

    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
            try {
                mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
                mediaController.registerCallback(new MediaControllerCallback());
                connected.postValue(true);
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

    private class MediaControllerCallback extends MediaControllerCompat.Callback {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            playbackState.postValue(state != null ? state : EMPTY_PLAYBACK_STATE);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            nowPlaying.postValue(metadata != null ? metadata : NOTHING_PLAYING);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }

        @Override
        public void onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended();
        }
    }
}
