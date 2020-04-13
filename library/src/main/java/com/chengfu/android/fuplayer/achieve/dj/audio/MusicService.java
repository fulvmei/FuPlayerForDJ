package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.android.fuplayer.ui.PlayerNotificationManager;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import static com.chengfu.android.fuplayer.achieve.dj.audio.NotificationBuilder.NOW_PLAYING_NOTIFICATION;

public class MusicService extends MediaBrowserServiceCompat implements LifecycleOwner {
    public static final String TAG = "MusicService";
    public static final String ACTION_NOTIFICATION_CLOSED = "com.chengfu.android.fuplayer.achieve.dj.audio.ACTION_NOTIFICATION_CLOSED";

    private LifecycleRegistry lifecycle;
    private boolean isForegroundService;

    private MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaController;

    private NotificationBuilder notificationBuilder;
    private NotificationManagerCompat notificationManager;

    private BecomingNoisyReceiver becomingNoisyReceiver;

    private MediaSessionPlayer mediaSessionPlayer;
    private QueueAdapter queueAdapter;

    private MediaControllerCallback mediaControllerCallback;
    private NotificationBroadcastReceiver notificationBroadcastReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        FuLog.DEBUG = true;
        FuLog.d(TAG, "onCreate");
        lifecycle = new LifecycleRegistry(this);
        lifecycle.setCurrentState(Lifecycle.State.RESUMED);

        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        registerReceiver(notificationBroadcastReceiver, new IntentFilter(ACTION_NOTIFICATION_CLOSED));

        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken());

        mediaController = new MediaControllerCompat(this, mediaSession);
        notificationBuilder = new NotificationBuilder(this);
        notificationManager = NotificationManagerCompat.from(this);

        mediaSessionPlayer = new MediaSessionPlayer(this, mediaSession);
        queueAdapter = new QueueAdapterImpl();
        mediaSessionPlayer.setQueueAdapter(queueAdapter);

        mediaSession.setSessionActivity(getSessionActivity(mediaSessionPlayer));

        AudioPlayManager.getCurrentPlayList(this).observe(this, new Observer<List<CurrentPlay>>() {
            @Override
            public void onChanged(List<CurrentPlay> currentPlays) {
                Log.d("fff", "onChanged size" + currentPlays.size());
                queueAdapter.addAll(ConverterUtil.currentPlayListToQueueItemList(currentPlays));
            }
        });

        mediaController = new MediaControllerCompat(this, mediaSession);
        mediaControllerCallback = new MediaControllerCallback();
        mediaController.registerCallback(mediaControllerCallback);

        notificationBuilder = new NotificationBuilder(this);
        notificationManager = NotificationManagerCompat.from(this);

        try {
            becomingNoisyReceiver =
                    new BecomingNoisyReceiver(this, mediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private PendingIntent getSessionActivity(MediaSessionPlayer mediaSessionPlayer) {
        Intent sessionIntent = new Intent();
        sessionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MediaDescriptionCompat description = (MediaDescriptionCompat) mediaSessionPlayer.getPlayer().getCurrentTag();
        sessionIntent.putExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS, description != null ? description.getExtras() : null);
        ComponentName componentName = new ComponentName(MusicService.this, getApplication().getPackageName() + ".FuSessionActivity");
        sessionIntent.setComponent(componentName);
        return PendingIntent.getActivity(MusicService.this, MusicContract.REQUEST_CODE_SESSION_ACTIVITY, sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void updateNotification(PlaybackStateCompat state) {
        int updatedState = state.getState();
        // Skip building a notification when state is "none" and metadata is null.
        Notification notification = null;
        if (mediaController.getMetadata() != null
                && updatedState != PlaybackStateCompat.STATE_NONE) {
            try {
                notification = notificationBuilder.buildNotification(mediaSession.getSessionToken(),getSessionActivity(mediaSessionPlayer));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (updatedState == PlaybackStateCompat.STATE_BUFFERING ||
                updatedState == PlaybackStateCompat.STATE_PLAYING) {
            becomingNoisyReceiver.register();
            if (notification != null) {
                notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification);

                if (!isForegroundService) {
                    ContextCompat.startForegroundService(
                            getApplicationContext(),
                            new Intent(getApplicationContext(), MusicService.class));
                    startForeground(NOW_PLAYING_NOTIFICATION, notification);
                    isForegroundService = true;
                }
            }
        } else {
            becomingNoisyReceiver.unregister();
            if (isForegroundService) {
                stopForeground(false);
                isForegroundService = false;
                // If playback has ended, also stop the service.
                if (updatedState == PlaybackStateCompat.STATE_NONE) {
                    stopSelf();
                }

                if (notification != null) {
                    notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification);
                } else {
                    stopForeground(true);
                }
            }
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        FuLog.d(TAG, "onGetRoot : clientPackageName=" + clientPackageName + ",clientUid=" + clientUid + ",rootHints=" + rootHints);
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        FuLog.d(TAG, "onLoadChildren : parentId=" + parentId);
//        LiveData<List<CurrentPlay>> currentPlayList = AudioPlayManager.getCurrentPlayList(this);
//        currentPlayList.observe(this, new Observer<List<CurrentPlay>>() {
//            @Override
//            public void onChanged(List<CurrentPlay> currentPlays) {
//                FuLog.d(TAG, "onLoadChildren : onChanged=");
//                result.sendResult(new ArrayList<>());
//            }
//        });
        result.sendResult(new ArrayList<>());
//        result.detach();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.setCurrentState(Lifecycle.State.DESTROYED);
        unregisterReceiver(notificationBroadcastReceiver);
        mediaSession.setActive(false);
        mediaSession.release();
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            updateNotification(mediaController.getPlaybackState());
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            updateNotification(state);
        }

    }

    private static class BecomingNoisyReceiver extends BroadcastReceiver {
        private Context context;
        private MediaSessionCompat.Token sessionToken;
        private IntentFilter noisyIntentFilter;
        private MediaControllerCompat controller;
        private boolean registered = false;

        public BecomingNoisyReceiver(Context context,
                                     MediaSessionCompat.Token sessionToken) throws RemoteException {
            this.context = context;
            this.sessionToken = sessionToken;

            noisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            controller = new MediaControllerCompat(context, sessionToken);
        }

        public void register() {
            if (!registered) {
                context.registerReceiver(this, noisyIntentFilter);
                registered = true;
            }
        }

        public void unregister() {
            if (registered) {
                context.unregisterReceiver(this);
                registered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            FuLog.d(TAG, "onReceive : context=" + context + ",intent=" + intent);
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                controller.getTransportControls().pause();
            }
        }
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            stopForeground(true);
            if (mediaSessionPlayer != null) {
                mediaSessionPlayer.stop();
//                mediaSessionPlayer.setPlayList(new ArrayList<>());
            }
            if (mediaSession != null && mediaSession.isActive()) {
//                mediaSession.sendSessionEvent();
            }
        }
    }
}
