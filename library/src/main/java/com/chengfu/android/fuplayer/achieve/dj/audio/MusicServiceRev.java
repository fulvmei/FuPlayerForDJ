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
import android.os.ResultReceiver;
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
import androidx.media.MediaBrowserServiceCompat;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.ext.mediasession.MediaSessionConnector;
import com.chengfu.android.fuplayer.ext.mediasession.TimelineQueueNavigator;
import com.chengfu.android.fuplayer.ui.PlayerNotificationManager;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MusicServiceRev extends MediaBrowserServiceCompat {

    public static final String TAG = "MusicService";
    public static final float VOLUME_DUCK = 0.2f;

    public static final String COMMAND_ADD_QUEUE_ITEM =
            "android.support.v4.media.session.command.ADD_QUEUE_ITEM";

    private static final String UAMP_USER_AGENT = "uamp.next";
    private static final String UAMP_BROWSABLE_ROOT = "/";
    private static final String UAMP_EMPTY_ROOT = "@empty@";

    private MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaController;
    private BecomingNoisyReceiver becomingNoisyReceiver;
    private NotificationManagerCompat notificationManager;
    private NotificationBuilder notificationBuilder;
    private MusicSource musicSource;
    private MediaSessionConnector mediaSessionConnector;
    private PackageValidator packageValidator;

    private boolean isForegroundService;
    private FuPlayer exoPlayer;
    private PlayerNotificationManager playerNotificationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        // Create a new MediaSession.
        Picasso picasso = null;
        try {
            picasso = Picasso.get();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (picasso == null) {
            Picasso.setSingletonInstance(new Picasso.Builder(getApplicationContext()).build());
        }

        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);
        mediaSession.setActive(true);
        /**
         * In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
         * a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
         *
         * It is possible to wait to set the session token, if required for a specific use-case.
         * However, the token *must* be set by the time [MediaBrowserServiceCompat.onGetRoot]
         * returns, or the connection will fail silently. (The system will not even call
         * [MediaBrowserCompat.ConnectionCallback.onConnectionFailed].)
         */
        setSessionToken(mediaSession.getSessionToken());

        // Because ExoPlayer will manage the MediaSession, add the service as a callback for
        // state changes.
        mediaController = new MediaControllerCompat(this, mediaSession);
//        mediaController.registerCallback(new MediaControllerCallback());

        notificationBuilder = new NotificationBuilder(this);
        notificationManager = NotificationManagerCompat.from(this);

        try {
            becomingNoisyReceiver =
                    new BecomingNoisyReceiver(this, mediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        musicSource = new MusicSource();

        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = new MediaSessionConnector(mediaSession);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, UAMP_USER_AGENT), null);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();

        exoPlayer = new FuExoPlayerFactory(this).create();

        exoPlayer.getAudioComponent().setAudioAttributes(attributes, true);

        MediaSessionConnector.PlaybackPreparer playbackPreparer = new UampPlaybackPreparer(exoPlayer, dataSourceFactory, musicSource);


        mediaSessionConnector.setPlayer(exoPlayer);

        mediaSessionConnector.setControlDispatcher(new UampPlaybackController());
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer);
        mediaSessionConnector.setQueueNavigator(new UampQueueNavigator(mediaSession));
        mediaSessionConnector.setQueueEditor(new UampQueueEditor());

        packageValidator = new PackageValidator(this, R.xml.allowed_media_browser_callers);

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(this, "com.chengfu.android.media.NOW_PLAYING", R.string.fu_notification_channel, 100110, new PlayerNotificationManager.MediaDescriptionAdapter() {

            @Override
            public String getCurrentContentTitle(FuPlayer player) {
                MediaDescriptionCompat description = (MediaDescriptionCompat) player.getCurrentTag();
                return description != null && description.getTitle() != null ? description.getTitle().toString() : null;
            }

            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(FuPlayer player) {
                Intent sessionIntent = new Intent();
                sessionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MediaDescriptionCompat description = (MediaDescriptionCompat) player.getCurrentTag();
                sessionIntent.putExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS, description != null ? description.getExtras() : null);
                ComponentName componentName = new ComponentName(MusicServiceRev.this, getApplication().getPackageName() + ".FuSessionActivity");
                sessionIntent.setComponent(componentName);
                PendingIntent sessionActivityPendingIntent = PendingIntent.getActivity(MusicServiceRev.this, MusicContract.REQUEST_CODE_SESSION_ACTIVITY, sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                return sessionActivityPendingIntent;
            }

            @Nullable
            @Override
            public String getCurrentContentText(FuPlayer player) {
                MediaDescriptionCompat description = (MediaDescriptionCompat) player.getCurrentTag();
                return description != null && description.getSubtitle() != null ? description.getSubtitle().toString() : null;
            }

            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(FuPlayer player, PlayerNotificationManager.BitmapCallback callback) {
                MediaDescriptionCompat description = (MediaDescriptionCompat) player.getCurrentTag();
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
                        .resize(256, 256)
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
        });

        playerNotificationManager.setPlaybackPreparer(() -> {
            if (exoPlayer.getPlaybackState() == FuPlayer.STATE_IDLE && exoPlayer.getPlaybackError() != null) {
                exoPlayer.retry();
            }
        });

        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                FuLog.d(TAG, "onNotificationCancelled  notificationId=" + notificationId + ",dismissedByUser=" + dismissedByUser);
                if (isForegroundService) {
                    becomingNoisyReceiver.unregister();
                    stopForeground(true);
                    isForegroundService = false;
                }
            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                FuLog.d(TAG, "onNotificationPosted  notificationId=" + notificationId + ",notification=" + notification);
                if (!isForegroundService) {
                    becomingNoisyReceiver.register();

                    ContextCompat.startForegroundService(
                            getApplicationContext(),
                            new Intent(getApplicationContext(), MusicServiceRev.class));

                    startService(new Intent(getApplicationContext(), MusicServiceRev.class));
                    startForeground(notificationId, notification);
                    isForegroundService = true;
                }
            }
        });

        playerNotificationManager.setPlayer(exoPlayer);
        playerNotificationManager.setUseStopAction(true);
        playerNotificationManager.setUseNavigationActions(true);
        playerNotificationManager.setFastForwardIncrementMs(0);
        playerNotificationManager.setRewindIncrementMs(0);
        playerNotificationManager.setUseChronometer(false);
        playerNotificationManager.setUseNavigationActionsInCompactView(true);
    }

    /**
     * This is the code that causes UAMP to stop playing when swiping it away from recents.
     * The choice to do this is app specific. Some apps stop playback, while others allow playback
     * to continue and allow uses to stop it with the notification.
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        FuLog.d(TAG, "onTaskRemoved");
        /**
         * By stopping playback, the player will transition to [Player.STATE_IDLE]. This will
         * cause a state change in the MediaSession, and (most importantly) call
         * [MediaControllerCallback.onPlaybackStateChanged]. Because the playback state will
         * be reported as [PlaybackStateCompat.STATE_NONE], the service will first remove
         * itself as a foreground service, and will then call [stopSelf].
         */
        exoPlayer.stop(true);
    }

    @Override
    public void onDestroy() {
        FuLog.d(TAG, "onDestroy");
        exoPlayer.stop(true);
        mediaSession.setActive(false);
        mediaSession.release();
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaItem]s to browse/play.
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (packageValidator.isKnownCaller(clientPackageName, clientUid)) {
            // The caller is allowed to browse, so return the root.
            return new BrowserRoot(UAMP_BROWSABLE_ROOT, null);
        } else {
            /**
             * Unknown caller. There are two main ways to handle this:
             * 1) Return a root without any content, which still allows the connecting client
             * to issue commands.
             * 2) Return `null`, which will cause the system to disconnect the app.
             *
             * UAMP takes the first approach for a variety of reasons, but both are valid
             * options.
             */
            return new BrowserRoot(UAMP_EMPTY_ROOT, null);
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        FuLog.d(TAG, "onLoadChildren : parentId=" + parentId + ",result=" + result);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        for (MediaDescriptionCompat media : musicSource.getMediaList(parentId)) {
            mediaItems.add(new MediaBrowserCompat.MediaItem(media, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE | MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }

        result.sendResult(mediaItems);
    }

    /**
     * Removes the [NOW_PLAYING_NOTIFICATION] notification.
     * <p>
     * Since `stopForeground(false)` was already called (see
     * [MediaControllerCallback.onPlaybackStateChanged], it's possible to cancel the notification
     * with `notificationManager.cancel(NOW_PLAYING_NOTIFICATION)` if minSdkVersion is >=
     * [Build.VERSION_CODES.LOLLIPOP].
     * <p>
     * Prior to [Build.VERSION_CODES.LOLLIPOP], notifications associated with a foreground
     * service remained marked as "ongoing" even after calling [Service.stopForeground],
     * and cannot be cancelled normally.
     * <p>
     * Fortunately, it's possible to simply call [Service.stopForeground] a second time, this
     * time with `true`. This won't change anything about the service's state, but will simply
     * remove the notification.
     */
    private void removeNowPlayingNotification() {
        stopForeground(true);
    }

    private void updateSessionData(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }

        Intent sessionIntent = new Intent();
        sessionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sessionIntent.putExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS, metadata.getBundle());
        ComponentName componentName = new ComponentName(this, getApplication().getPackageName() + ".FuSessionActivity");
        sessionIntent.setComponent(componentName);
        PendingIntent sessionActivityPendingIntent = PendingIntent.getActivity(this, MusicContract.REQUEST_CODE_SESSION_ACTIVITY, sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(sessionActivityPendingIntent);
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            FuLog.d(TAG, "onMetadataChanged  metadata=" + metadata);
//            try {
//                updateNotification(mediaController.getPlaybackState());
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }

            updateSessionData(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            FuLog.d(TAG, "onPlaybackStateChanged  state=" + state);
//            try {
//                updateNotification(state);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }


        private void updateNotification(PlaybackStateCompat state) throws RemoteException {

            int updatedState = state.getState();
            Notification notification = null;
            if (mediaController.getMetadata() == null) {
                FuLog.d(TAG, "updateNotification : metadata=null");
                becomingNoisyReceiver.unregister();
                if (isForegroundService) {
                    stopForeground(false);
                    isForegroundService = false;

                    // If playback has ended, also stop the service.
                    if (updatedState == PlaybackStateCompat.STATE_NONE) {
                        stopSelf();
                    }

                    if (notification != null) {
                        notificationManager.notify(NotificationBuilder.NOW_PLAYING_NOTIFICATION, notification);
                    } else {
                        removeNowPlayingNotification();
                    }
                }
                return;
            }

            // Skip building a notification when state is "none".

            if (updatedState != PlaybackStateCompat.STATE_NONE) {
                notification = notificationBuilder.buildNotification(mediaSession.getSessionToken(),null);
            }
            if (updatedState == PlaybackStateCompat.STATE_BUFFERING || updatedState == PlaybackStateCompat.STATE_PLAYING) {
                becomingNoisyReceiver.register();
                /**
                 * This may look strange, but the documentation for [Service.startForeground]
                 * notes that "calling this method does *not* put the service in the started
                 * state itself, even though the name sounds like it."
                 */
                if (!isForegroundService) {
                    startService(new Intent(getApplicationContext(), MusicServiceRev.class));
                    startForeground(NotificationBuilder.NOW_PLAYING_NOTIFICATION, notification);
                    isForegroundService = true;
                } else if (notification != null) {
                    notificationManager.notify(NotificationBuilder.NOW_PLAYING_NOTIFICATION, notification);
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
                        notificationManager.notify(NotificationBuilder.NOW_PLAYING_NOTIFICATION, notification);
                    } else {
                        removeNowPlayingNotification();
                    }
                }
            }
        }
    }

    private class UampPlaybackController extends DefaultControlDispatcher {
        @Override
        public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
            if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                exoPlayer.seekTo(0);
            }
            if (player.getPlaybackState() == Player.STATE_IDLE) {
                exoPlayer.retry();
            }
            return super.dispatchSetPlayWhenReady(player, playWhenReady);
        }
    }


    private class UampQueueNavigator extends TimelineQueueNavigator {
        private Timeline.Window window = new Timeline.Window();

        public UampQueueNavigator(MediaSessionCompat mediaSession) {
            super(mediaSession);
        }

        @Override
        public MediaDescriptionCompat getMediaDescription(FuPlayer player, int windowIndex) {
            player.getCurrentTimeline().getWindow(windowIndex, window, true);
            return (MediaDescriptionCompat) window.tag;
        }

        @Override
        public long getSupportedQueueNavigatorActions(FuPlayer player) {
            boolean enableSkipTo = false;
            boolean enablePrevious = false;
            boolean enableNext = false;
            Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty() && !player.isPlayingAd()) {
                timeline.getWindow(player.getCurrentWindowIndex(), window);
                enableSkipTo = timeline.getWindowCount() > 1;
                enablePrevious = player.hasPrevious();
                enableNext =  player.hasNext();
            }

            long actions = 0;
            if (enableSkipTo) {
                actions |= PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
            }
            if (enablePrevious) {
                actions |= PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
            }
            if (enableNext) {
                actions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
            }
            return actions;
        }

        @Override
        public void onSkipToPrevious(FuPlayer player, ControlDispatcher controlDispatcher) {
            Log.d("gggg","onSkipToPrevious");
            player.previous();
//            super.onSkipToPrevious(player, controlDispatcher);
        }
    }

    private class UampQueueEditor implements MediaSessionConnector.QueueEditor {

        @Override
        public void onAddQueueItem(FuPlayer player, MediaDescriptionCompat description) {
            FuLog.d(TAG, "onAddQueueItem : description=" + description);
            musicSource.add(description);
        }

        @Override
        public void onAddQueueItem(FuPlayer player, MediaDescriptionCompat description, int index) {
            musicSource.add(index, description);
        }

        @Override
        public void onRemoveQueueItem(FuPlayer player, MediaDescriptionCompat description) {
            musicSource.remove(description);
        }

//        @Override
//        public String[] getCommands() {
//            FuLog.d(TAG, "getCommands : ");
//            return new String[]{MusicContract.COMMAND_SET_QUEUE_ITEMS, MusicContract.COMMAND_CLEAR_QUEUE_ITEMS};
//        }

//        @Override
//        public void onCommand(Player player, String command, Bundle extras, ResultReceiver cb) {
//            FuLog.d(TAG, "onCommand : command=" + command + ",extras=" + extras);
//            musicSource.clear();
//            if (MusicContract.COMMAND_SET_QUEUE_ITEMS.equals(command)) {
//                if (extras != null) {
//                    extras.setClassLoader(getClass().getClassLoader());
//                    ArrayList<MediaDescriptionCompat> list = extras.getParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS);
//                    if (list != null) {
//                        musicSource.addAll(list);
//                    }
//                }
//            } else {
//                musicSource.clear();
//            }
//        }

        @Override
        public boolean onCommand(FuPlayer player, ControlDispatcher controlDispatcher, String command, Bundle extras, ResultReceiver cb) {
            FuLog.d(TAG, "onCommand : command=" + command + ",extras=" + extras);
            musicSource.clear();
            if (MusicContract.COMMAND_SET_QUEUE_ITEMS.equals(command)) {
                if (extras != null) {
                    extras.setClassLoader(getClass().getClassLoader());
                    ArrayList<MediaDescriptionCompat> list = extras.getParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS);
                    if (list != null) {
                        musicSource.addAll(list);
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Helper class for listening for when headphones are unplugged (or the audio
     * will otherwise cause playback to become "noisy").
     */
    private class BecomingNoisyReceiver extends BroadcastReceiver {
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
}
