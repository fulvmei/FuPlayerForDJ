package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.RemoteException;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.support.v4.media.MediaDescriptionCompat;

import androidx.media.app.FuMediaStyle;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media.session.MediaButtonReceiver;

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

//        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP);
        stopPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(MusicService.ACTION_NOTIFICATION_CLOSED), 0);

        platformNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Notification buildNotification(MediaSessionCompat.Token sessionToken, PendingIntent contentIntent) throws RemoteException {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel();
        }

        MediaControllerCompat controller = new MediaControllerCompat(context, sessionToken);

        MediaDescriptionCompat description = controller.getMetadata().getDescription();
        PlaybackStateCompat playbackState = controller.getPlaybackState();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL);


//        boolean hasPreviousAction = false;
//        boolean hasNextAction = false;
//        if (PlaybackStateCompatExt.isSkipToPreviousEnabled(playbackState)) {
//            builder.addAction(skipToPreviousAction);
//            hasPreviousAction = true;
//        }
//        if (PlaybackStateCompatExt.isPlaying(playbackState)) {
//            builder.addAction(pauseAction);
//        } else {
//            builder.addAction(playAction);
//        }
//        if (PlaybackStateCompatExt.isSkipToNextEnabled(playbackState)) {
//            builder.addAction(skipToNextAction);
//            hasNextAction = true;
//        }
//
//        int[] actions = new int[]{1};
//        if (hasPreviousAction && hasNextAction) {
//            actions = new int[]{1, 2};
//        }else if (hasNextAction) {
//            actions = new int[]{0, 1};
//        }else if (hasPreviousAction) {
//            actions = new int[]{1};
//        }

        builder.addAction(skipToPreviousAction);
        if (PlaybackStateCompatExt.isPlaying(playbackState)) {
            builder.addAction(pauseAction);
        } else {
            builder.addAction(playAction);
        }
        builder.addAction(skipToNextAction);

        MediaStyle mediaStyle = new FuMediaStyle()
                .setCancelButtonIntent(stopPendingIntent)
                .setMediaSession(sessionToken)
                .setShowCancelButton(true)
                .setShowActionsInCompactView(1);

        return builder.setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(description.getSubtitle())
                .setColorized(false)
                .setShowWhen(false)
                .setContentTitle(description.getTitle())
                .setOngoing(true)
                .setDeleteIntent(stopPendingIntent)
                .setLargeIcon(description.getIconBitmap())
//                .setLargeIcon(AppIconHelper.getAppIconBitmap(context.getPackageManager(), context.getApplicationInfo().packageName))
                .setOnlyAlertOnce(true)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPublicVersion(builder.build())
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
                context.getString(R.string.fu_notification_channel),
                NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription(context.getString(R.string.fu_notification_channel_description));

        platformNotificationManager.createNotificationChannel(notificationChannel);
    }
}
