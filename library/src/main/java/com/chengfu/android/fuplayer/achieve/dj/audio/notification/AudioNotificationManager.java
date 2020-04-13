package com.chengfu.android.fuplayer.achieve.dj.audio.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.FuMediaStyle;
import androidx.media.session.MediaButtonReceiver;

import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.PlaybackStateCompatExt;

public class AudioNotificationManager {

    public interface NotificationListener {
        @Nullable
        PendingIntent createCurrentContentIntent(MediaDescriptionCompat description);

        @Nullable
        Bitmap getCurrentLargeIcon(MediaDescriptionCompat description, BitmapCallback callback);

        void onNotificationPosted(int notificationId, Notification notification);

        void onNotificationCancelled();
    }


    public final class BitmapCallback {
        private final int notificationTag;

        private BitmapCallback(int notificationTag) {
            this.notificationTag = notificationTag;
        }

        public void onBitmap(final Bitmap bitmap) {
            if (bitmap != null) {
                mainHandler.post(
                        () -> {
                            if (notificationTag == currentNotificationTag
                                    && isNotificationStarted) {
                                updateNotification(bitmap);
                            }
                        });
            }
        }
    }

    public static final String TAG = "AudioNotificationManager";
    public static final String NOW_PLAYING_CHANNEL = "com.chengfu.android.media.NOW_PLAYING";
    public static final String ACTION_NOTIFICATION_DISMISS = "com.chengfu.android.fuplayer.achieve.dj.audio.ACTION_NOTIFICATION_ACTION_DISMISS";
    public static final int NOTIFICATION_ID = 0xb339;

    private final Context context;
    private final MediaSessionCompat mediaSession;

    private final IntentFilter intentFilter;
    private final NotificationBroadcastReceiver notificationBroadcastReceiver;

    private final Handler mainHandler;
    private final MediaControllerCompat mediaController;
    private final MediaControllerCallback mediaControllerCallback;

    private final NotificationManagerCompat notificationManager;
    private NotificationListener notificationListener;

    private final NotificationCompat.Action skipToPreviousAction;
    private final NotificationCompat.Action playAction;
    private final NotificationCompat.Action pauseAction;
    private final NotificationCompat.Action skipToNextAction;

    private final PendingIntent cancelPendingIntent;

    private boolean isNotificationStarted;
    private int currentNotificationTag;


    public AudioNotificationManager(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {
        this.context = context;
        this.mediaSession = mediaSession;

        this.intentFilter = new IntentFilter(ACTION_NOTIFICATION_DISMISS);
        this.notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        mainHandler = new Handler(Looper.getMainLooper());

        mediaController = new MediaControllerCompat(context, mediaSession);
        mediaControllerCallback = new MediaControllerCallback();
        mediaController.registerCallback(mediaControllerCallback);

        notificationManager = NotificationManagerCompat.from(context);

        skipToPreviousAction = new NotificationCompat.Action(
                R.drawable.fu_ic_skip_previous,
                context.getString(R.string.fu_notification_skip_to_previous),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
        playAction = new NotificationCompat.Action(
                R.drawable.fu_ic_play,
                context.getString(R.string.fu_notification_play),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY));
        pauseAction = new NotificationCompat.Action(
                R.drawable.fu_ic_pause,
                context.getString(R.string.fu_notification_pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE));
        skipToNextAction = new NotificationCompat.Action(
                R.drawable.fu_ic_skip_next,
                context.getString(R.string.fu_notification_skip_to_next),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        cancelPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_NOTIFICATION_DISMISS), 0);

    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    private NotificationCompat.Builder getNotificationBuilder(@Nullable Bitmap largeIcon) {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL);

        MediaDescriptionCompat description = mediaController.getMetadata().getDescription();
        PlaybackStateCompat playbackState = mediaController.getPlaybackState();

        builder.addAction(skipToPreviousAction);
        if (PlaybackStateCompatExt.isPlaying(playbackState)) {
            builder.addAction(pauseAction);
        } else {
            builder.addAction(playAction);
        }
        builder.addAction(skipToNextAction);

        androidx.media.app.NotificationCompat.MediaStyle mediaStyle = new FuMediaStyle()
                .setCancelButtonIntent(cancelPendingIntent)
                .setMediaSession(mediaSession.getSessionToken())
                .setShowCancelButton(true)
                .setShowActionsInCompactView(1);

        builder.setPriority(Notification.PRIORITY_MAX)
                .setContentText(description.getSubtitle())
                .setColorized(false)
                .setShowWhen(false)
                .setContentTitle(description.getTitle())
                .setOngoing(true)
                .setDeleteIntent(cancelPendingIntent)
//                .setLargeIcon(description.getIconBitmap())
                .setOnlyAlertOnce(true)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPublicVersion(builder.build());

        if (largeIcon == null && notificationListener != null) {
            largeIcon = notificationListener.getCurrentLargeIcon(description, new BitmapCallback(++currentNotificationTag));
        }
        builder.setLargeIcon(largeIcon);

        if (notificationListener != null) {
            builder.setContentIntent(notificationListener.createCurrentContentIntent(description));
        }
        return builder;
    }

    private Notification updateNotification() {
        return updateNotification(null);
    }

    private Notification updateNotification(Bitmap largeIcon) {
        NotificationCompat.Builder builder = getNotificationBuilder(largeIcon);
        int updatedState = mediaController.getPlaybackState() != null ?
                mediaController.getPlaybackState().getState() : PlaybackStateCompat.STATE_NONE;
        if (updatedState == PlaybackStateCompat.STATE_NONE) {
            stopNotification(/* dismissedByUser= */ false);
            return null;
        }

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);

        if (!isNotificationStarted) {
            isNotificationStarted = true;
            context.registerReceiver(notificationBroadcastReceiver, intentFilter);
        }
        if (notificationListener != null) {
            notificationListener.onNotificationPosted(NOTIFICATION_ID, notification);
        }
        return builder.build();
    }

    private void stopNotification(boolean dismissedByUser) {
        if (isNotificationStarted) {
            isNotificationStarted = false;
            notificationManager.cancel(NOTIFICATION_ID);
            context.unregisterReceiver(notificationBroadcastReceiver);
            if (notificationListener != null) {
                notificationListener.onNotificationCancelled();
            }
        }
    }

    private boolean shouldCreateNowPlayingChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private boolean nowPlayingChannelExists() {
        return notificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNowPlayingChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(NOW_PLAYING_CHANNEL,
                context.getString(R.string.fu_notification_channel),
                NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription(context.getString(R.string.fu_notification_channel_description));

        notificationManager.createNotificationChannel(notificationChannel);
    }

    public void onDestroy(){
        context.unregisterReceiver(notificationBroadcastReceiver);
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.d("ttt", "onMetadataChanged metadata=" + metadata);
            updateNotification();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d("ttt", "onPlaybackStateChanged state=" + state);
            updateNotification();
        }
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_NOTIFICATION_DISMISS.equals(action)) {
                stopNotification(true);
            }
        }
    }
}
