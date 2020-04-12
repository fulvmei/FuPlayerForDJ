package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media.session.MediaButtonReceiver;

import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.AppIconHelper;

public class NotificationBuilderRev {

    public static final String NOW_PLAYING_CHANNEL = "com.chengfu.android.media.NOW_PLAYING";
    public static final int NOW_PLAYING_NOTIFICATION = 0xb339;

    private Context context;

    private final NotificationManager platformNotificationManager;

    private NotificationCompat.Action skipToPreviousAction;
    private NotificationCompat.Action playAction;
    private NotificationCompat.Action pauseAction;
    private NotificationCompat.Action skipToNextAction;
    private PendingIntent stopPendingIntent;

    public NotificationBuilderRev(Context context) {
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
                .setLargeIcon(AppIconHelper.getAppIconBitmap(context.getPackageManager(), context.getApplicationInfo().packageName))
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
