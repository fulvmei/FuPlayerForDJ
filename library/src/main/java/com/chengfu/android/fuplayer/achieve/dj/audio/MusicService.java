package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.media.MediaBrowserServiceCompat;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.audio.notification.AudioNotificationManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.player.MediaSessionPlayer1;
import com.chengfu.android.fuplayer.achieve.dj.audio.receiver.BecomingNoisyReceiver;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat implements LifecycleOwner, MediaSessionPlayer1.MediaLoadProvider {
    public static final String TAG = "MusicService";

    private MusicService service = this;
    private LifecycleRegistry lifecycle;
    private boolean isForegroundService;

    private MediaSessionCompat mediaSession;

    private BecomingNoisyReceiver becomingNoisyReceiver;

    private MediaSessionPlayer1 mediaSessionPlayer;

    private AudioNotificationManager audioNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        FuLog.d(TAG, "onCreate");
        lifecycle = new LifecycleRegistry(this);
        lifecycle.setCurrentState(Lifecycle.State.RESUMED);

        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken());

        audioNotificationManager = new AudioNotificationManager(this, mediaSession.getSessionToken());

        Picasso picasso = null;
        try {
            picasso = Picasso.get();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (picasso == null) {
            Picasso.setSingletonInstance(new Picasso.Builder(getApplicationContext()).build());
        }

        audioNotificationManager.setNotificationListener(new NotificationListener());

        mediaSessionPlayer = new MediaSessionPlayer1(this, mediaSession, createPlayer());

        mediaSessionPlayer.setMediaLoadProvider(this);

        becomingNoisyReceiver = new BecomingNoisyReceiver(this, mediaSession.getSessionToken());
    }

    protected FuPlayer createPlayer() {
        FuPlayer player = new FuExoPlayerFactory(this).create();
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();
        if (player.getAudioComponent() != null) {
            player.getAudioComponent().setAudioAttributes(attributes, true);
        }
        player.setPauseAtEndOfMediaItems(true);
        return player;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
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
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (MediaSessionCompat.QueueItem item : mediaSessionPlayer.getQueueItemList()) {
            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(item.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
            mediaItems.add(mediaItem);
        }
        result.sendResult(mediaItems);
    }

    public PendingIntent createCurrentContentIntent(MediaMetadataCompat metadata) {
        return null;
    }

    public Bitmap getCurrentLargeIcon(MediaDescriptionCompat description, AudioNotificationManager.BitmapCallback callback) {
        if (description == null) {
            return BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon);
        }
        if (description.getIconBitmap() != null) {
            return description.getIconBitmap();
        }
        if (description.getIconUri() == null) {
            return BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon);
        }
        Picasso.get().load(description.getIconUri())
                .centerCrop()
                .resize(336, 336)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        callback.onBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        callback.onBitmap(BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        callback.onBitmap(BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon));
                    }
                });
        return BitmapFactory.decodeResource(getResources(), getApplicationInfo().icon);
    }

    @Override
    public void onLoadMedia(MediaDescriptionCompat description, MediaSessionPlayer1.MediaLoadCallback callback) {
        callback.onCompleted(description);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FuLog.d(TAG, "onDestroy");
        lifecycle.setCurrentState(Lifecycle.State.DESTROYED);
        mediaSessionPlayer.release();
        mediaSession.setActive(false);
        mediaSession.release();
        if (becomingNoisyReceiver != null) {
            becomingNoisyReceiver.unregister();
        }
        audioNotificationManager.onDestroy();
    }


    class NotificationListener implements AudioNotificationManager.NotificationListener {

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(MediaMetadataCompat metadata) {
            FuLog.d(TAG, "createCurrentContentIntent  metadata=" + metadata);
            return service.createCurrentContentIntent(metadata);
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(MediaDescriptionCompat description, AudioNotificationManager.BitmapCallback callback) {
            return service.getCurrentLargeIcon(description, callback);
        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification) {
            FuLog.d(TAG, "onNotificationPosted  notification=" + notification);
            if (!isForegroundService) {
                becomingNoisyReceiver.register();

                ContextCompat.startForegroundService(
                        getApplicationContext(),
                        new Intent(getApplicationContext(), service.getClass()));
                startService(new Intent(getApplicationContext(), service.getClass()));
                startForeground(notificationId, notification);
                isForegroundService = true;
            }
        }

        @Override
        public void onNotificationCancelled() {
            FuLog.d(TAG, "onNotificationCancelled");
            if (isForegroundService) {
                becomingNoisyReceiver.unregister();
                stopForeground(true);
                isForegroundService = false;
            }
            mediaSessionPlayer.release();
        }
    }

}
