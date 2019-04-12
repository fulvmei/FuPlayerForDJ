package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.chengfu.android.fuplayer.achieve.dj.R;


public class NotificationBuilder {

    public static final String NOW_PLAYING_CHANNEL = "com.chengfu.android.media.NOW_PLAYING";
    public static final int NOW_PLAYING_NOTIFICATION = 0xb339;

    private Context context;

    private final NotificationManager platformNotificationManager;

    private NotificationCompat.Action skipToPreviousAction;
    private NotificationCompat.Action playAction;
    private NotificationCompat.Action pauseAction;
    private NotificationCompat.Action skipToNextAction;
    private PendingIntent stopPendingIntent;

    public NotificationBuilder(Context context) {
        this.context = context;

        skipToPreviousAction = new NotificationCompat.Action(
                R.drawable.default_icon_previous,
                context.getString(R.string.notification_skip_to_previous),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
        playAction = new NotificationCompat.Action(
                R.drawable.default_icon_play,
                context.getString(R.string.notification_play),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY));
        pauseAction = new NotificationCompat.Action(
                R.drawable.default_icon_pause,
                context.getString(R.string.notification_pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE));
        skipToNextAction = new NotificationCompat.Action(
               R.drawable.default_icon_next,
                context.getString(R.string.notification_skip_to_next),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        stopPendingIntent = new MediaButtonReceiver().buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP);

        platformNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Notification buildNotification(MediaSessionCompat.Token sessionToken) throws RemoteException {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel();
        }

        MediaControllerCompat controller = new MediaControllerCompat(context, sessionToken);

        MediaDescriptionCompat description = controller.getMetadata().getDescription();
        PlaybackStateCompat playbackState = controller.getPlaybackState();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL);

        // Only add actions for skip back, play/pause, skip forward, based on what's enabled.
        int[] actions = new int[]{0};
//        if (PlaybackStateCompatExt.isSkipToPreviousEnabled(playbackState)) {
//            builder.addAction(skipToPreviousAction);
//            actions = new int[]{0, 1};
//        }
        if (PlaybackStateCompatExt.isPlaying(playbackState)) {
            builder.addAction(pauseAction);
        } else {
            builder.addAction(playAction);
        }
        if (PlaybackStateCompatExt.isSkipToNextEnabled(playbackState)) {
            builder.addAction(skipToNextAction);
            actions = new int[]{0, 1};
        }

        MediaStyle mediaStyle = new MediaStyle()
                .setCancelButtonIntent(stopPendingIntent)
                .setMediaSession(sessionToken)
                .setShowActionsInCompactView(actions)
                .setShowCancelButton(true);

        return builder.setContentIntent(controller.getSessionActivity())
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(description.getSubtitle())
                .setContentTitle(description.getTitle())
                .setShowWhen(false)
                .setDeleteIntent(stopPendingIntent)
                .setLargeIcon(description.getIconBitmap())
                .setOnlyAlertOnce(true)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

    }

    private boolean shouldCreateNowPlayingChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private boolean nowPlayingChannelExists() {
        return platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNowPlayingChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(NOW_PLAYING_CHANNEL,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription(context.getString(R.string.notification_channel_description));

        platformNotificationManager.createNotificationChannel(notificationChannel);
    }
}
