package com.chengfu.android.fuplayer.achieve.dj.audio.notification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;

import com.chengfu.android.fuplayer.achieve.dj.audio.MusicServiceRev;
import com.chengfu.android.fuplayer.achieve.dj.audio.NotificationBuilder;
import com.chengfu.android.fuplayer.util.FuLog;

public class AudioNotificationManager {
    public static final String TAG = "AudioNotificationManager";
    private final MediaControllerCompat mediaController;
    private final MediaControllerCallback mediaControllerCallback;

    public AudioNotificationManager(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {

        mediaController = new MediaControllerCompat(context, mediaSession);
        mediaControllerCallback = new MediaControllerCallback();
        mediaController.registerCallback(mediaControllerCallback);
    }

    private static class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
        }
    }
}
