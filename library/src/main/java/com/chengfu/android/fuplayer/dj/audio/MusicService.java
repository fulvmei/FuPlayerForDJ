package com.chengfu.android.fuplayer.dj.audio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.chengfu.android.fuplayer.dj.R;
import com.chengfu.android.fuplayer.dj.audio.library.MusicSource;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.DefaultPlaybackController;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {

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
    private SimpleExoPlayer exoPlayer;


    @Override
    public void onCreate() {
        super.onCreate();

        // Build a PendingIntent that can be used to launch the UI.
        Intent sessionIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent sessionActivityPendingIntent = PendingIntent.getActivity(this, 0, sessionIntent, 0);

        // Create a new MediaSession.
        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setSessionActivity(sessionActivityPendingIntent);
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
        mediaController.registerCallback(new MediaControllerCallback());

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
        mediaSessionConnector = new MediaSessionConnector(mediaSession, new UampPlaybackController());
        mediaSessionConnector.setQueueNavigator(new UampQueueNavigator(mediaSession));

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, UAMP_USER_AGENT), null);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        exoPlayer.setAudioAttributes(attributes, true);
        MediaSessionConnector.PlaybackPreparer playbackPreparer = new UampPlaybackPreparer(exoPlayer, dataSourceFactory, musicSource);

        mediaSessionConnector.setPlayer(exoPlayer, playbackPreparer);
        mediaSessionConnector.setQueueNavigator(new UampQueueNavigator(mediaSession));
        mediaSessionConnector.setQueueEditor(new UampQueueEditor());

        packageValidator = new PackageValidator(this, R.xml.allowed_media_browser_callers);
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

//        List<Music> musics = MusicUtil.getMusicList(this);

//        FuLog.d(TAG, "onLoadChildren : parentId=" + MusicSource.musicMapData.get("111"));

//        for (Music music : musics) {
//            mediaItems.add(TypeCorver.MusicToMediaItem(music));
//        }

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


    private class MediaControllerCallback extends MediaControllerCompat.Callback {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.d(TAG,"onMetadataChanged  state="+metadata);
            try {
                updateNotification(mediaController.getPlaybackState());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d(TAG,"onPlaybackStateChanged  state="+state);
            try {
                updateNotification(state);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
                notification = notificationBuilder.buildNotification(mediaSession.getSessionToken());
            }
            if (updatedState == PlaybackStateCompat.STATE_BUFFERING || updatedState == PlaybackStateCompat.STATE_PLAYING) {
                becomingNoisyReceiver.register();
                /**
                 * This may look strange, but the documentation for [Service.startForeground]
                 * notes that "calling this method does *not* put the service in the started
                 * state itself, even though the name sounds like it."
                 */
                if (!isForegroundService) {
                    startService(new Intent(getApplicationContext(), MusicService.class));
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

    private class UampPlaybackController extends DefaultPlaybackController {
        @Override
        public void onPlay(Player player) {
            if (exoPlayer == null) {
                return;
            }
            if (exoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                exoPlayer.seekTo(0);
            }
            if (player.getPlaybackState() == Player.STATE_IDLE) {
                exoPlayer.retry();
            }
            super.onPlay(player);
        }

        @Override
        public void onStop(Player player) {
            super.onStop(player);

        }
    }


    private class UampQueueNavigator extends TimelineQueueNavigator {
        private Timeline.Window window = new Timeline.Window();

        public UampQueueNavigator(MediaSessionCompat mediaSession) {
            super(mediaSession);
        }

        @Override
        public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
            return musicSource.getMediaList("").get(windowIndex);
        }
    }

    private class UampQueueEditor implements MediaSessionConnector.QueueEditor {

        @Override
        public void onAddQueueItem(Player player, MediaDescriptionCompat description) {
            FuLog.d(TAG, "onAddQueueItem : description=" + description);
            musicSource.add(description);
        }

        @Override
        public void onAddQueueItem(Player player, MediaDescriptionCompat description, int index) {
            musicSource.add(index, description);
        }

        @Override
        public void onRemoveQueueItem(Player player, MediaDescriptionCompat description) {
            musicSource.remove(description);
        }

        @Override
        public String[] getCommands() {
            FuLog.d(TAG, "getCommands : ");
            return new String[]{MusicContract.COMMAND_SET_QUEUE_ITEMS, MusicContract.COMMAND_CLEAR_QUEUE_ITEMS};
        }

        @Override
        public void onCommand(Player player, String command, Bundle extras, ResultReceiver cb) {
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
            } else {
                musicSource.clear();
            }
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
            if (intent.getAction() == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                controller.getTransportControls().pause();
            }
        }
    }


}