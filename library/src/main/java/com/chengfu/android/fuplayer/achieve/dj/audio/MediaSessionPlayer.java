package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.chengfu.android.fuplayer.ext.mediasession.MediaSessionConnector;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public final class MediaSessionPlayer {
    public static final String TAG = "MediaSessionPlayer";

    @LongDef(
            flag = true,
            value = {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE,
                    PlaybackStateCompat.ACTION_PLAY,
                    PlaybackStateCompat.ACTION_PAUSE,
                    PlaybackStateCompat.ACTION_SEEK_TO,
                    PlaybackStateCompat.ACTION_FAST_FORWARD,
                    PlaybackStateCompat.ACTION_REWIND,
                    PlaybackStateCompat.ACTION_STOP,
                    PlaybackStateCompat.ACTION_SET_REPEAT_MODE,
                    PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlaybackActions {
    }

    public static final long ALL_PLAYBACK_ACTIONS =
            PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_SEEK_TO
                    | PlaybackStateCompat.ACTION_FAST_FORWARD
                    | PlaybackStateCompat.ACTION_REWIND
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                    | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE;

    long ACTIONS =
            PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

    public static final long BASE_PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            | PlaybackStateCompat.ACTION_SET_REPEAT_MODE;

    public static final long DEFAULT_PLAYBACK_ACTIONS = ALL_PLAYBACK_ACTIONS;
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;


    private static final MediaMetadataCompat METADATA_EMPTY =
            new MediaMetadataCompat.Builder().build();

    private static final PlaybackStateCompat INITIAL_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build();

    private final PlayerEventListener playerEventListener;
    private final MediaSessionCallback mediaSessionCallback;

    private final Context context;
    //    private final Looper looper;
    private final FuPlayer player;
    //        private final List<MediaSessionCompat.QueueItem> queueItemList;
    private int currentQueueItemIndex;
    private int oldWindowIndex;
//    private ConcatenatingMediaSource mediaSource;

    private final MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaController;

    //    private final List<MediaSessionCompat.QueueItem> mPlaylist;
    private final DataChangedListener dataChangedListener;
    private QueueAdapter queueAdapter;
    private long activeItemId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
    private boolean activePlayingAd = false;
    private long activeDuration = -1;

    private MediaMetadataCompat mPreparedMedia;
    private int mCurrentPlayIndex;


    private MediaSource mediaSource;
    private final DefaultDataSourceFactory dataSourceFactory;

    private long enabledPlaybackActions;
    private int rewindMs;
    private int fastForwardMs;


    public MediaSessionPlayer(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {
        this.context = context;
//        queueAdapter.getActiveItem();
        //init playQueue
//        queueItemList = new ArrayList<>();
        mCurrentPlayIndex = -1;

        //init player
        player = new FuExoPlayerFactory(context).create();
        playerEventListener = new PlayerEventListener();
        player.addListener(playerEventListener);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();
        if (player.getAudioComponent() != null) {
            player.getAudioComponent().setAudioAttributes(attributes, true);
        }
        dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, context.getApplicationInfo().packageName), null);


        //init mediaSession
        this.mediaSession = mediaSession;
        mediaController = mediaSession.getController();
        mediaSessionCallback = new MediaSessionCallback();
        mediaSession.setCallback(mediaSessionCallback, new Handler(Util.getLooper()));

        dataChangedListener = new DataChangedListener();

        enabledPlaybackActions = DEFAULT_PLAYBACK_ACTIONS;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
//        mediaSession.setMetadata(METADATA_EMPTY);
//        mediaSession.setPlaybackState(INITIAL_PLAYBACK_STATE);
    }

    public FuPlayer getPlayer() {
        return player;
    }

    public QueueAdapter getQueueAdapter() {
        return queueAdapter;
    }

    public void setQueueAdapter(QueueAdapter queueAdapter) {
        if (this.queueAdapter == queueAdapter) {
            return;
        }
        if (this.queueAdapter != null) {
            this.queueAdapter.removeDataChangedListener(dataChangedListener);
        }
        this.queueAdapter = queueAdapter;
        if (queueAdapter != null) {
            queueAdapter.addDataChangedListener(dataChangedListener);
        }
    }

    public void setEnabledPlaybackActions(@PlaybackActions long enabledPlaybackActions) {
        enabledPlaybackActions &= ALL_PLAYBACK_ACTIONS;
        if (this.enabledPlaybackActions != enabledPlaybackActions) {
            this.enabledPlaybackActions = enabledPlaybackActions;
            updateMediaSessionPlaybackState();
        }
    }

    public void setRewindIncrementMs(int rewindMs) {
        if (this.rewindMs != rewindMs) {
            this.rewindMs = rewindMs;
            updateMediaSessionPlaybackState();
        }
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        if (this.fastForwardMs != fastForwardMs) {
            this.fastForwardMs = fastForwardMs;
            updateMediaSessionPlaybackState();
        }
    }

    private void updateMediaSessionMetadata() {
        Log.d("TAG", "updateMediaSessionMetadata");
        long duration = player.isCurrentWindowDynamic() || player.getDuration() == C.TIME_UNSET || player.getDuration() <= 0 ? -1 : player.getDuration();
        long activeItemId = queueAdapter != null && queueAdapter.getActiveItem() != null ? queueAdapter.getActiveItem().getQueueId() : MediaSessionCompat.QueueItem.UNKNOWN_ID;
        if (this.activeItemId != activeItemId
                || activePlayingAd != player.isPlayingAd()
                || activeDuration != duration) {
            MediaDescriptionCompat description = queueAdapter != null && queueAdapter.getActiveItem() != null ? queueAdapter.getActiveItem().getDescription() : null;
            mediaSession.setMetadata(getMetadata(description));
        }
        this.activeItemId = activeItemId;
    }

    private MediaMetadataCompat getMetadata(MediaDescriptionCompat description) {
        if (description == null) {
            return METADATA_EMPTY;
        }
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        activePlayingAd = player.isPlayingAd();
        if (activePlayingAd) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_ADVERTISEMENT, 1);
        }
        activeDuration = player.isCurrentWindowDynamic() || player.getDuration() == C.TIME_UNSET ? -1 : player.getDuration();
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                activeDuration);


        Bundle extras = description.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                if (value instanceof String) {
                    builder.putString(key, (String) value);
                } else if (value instanceof CharSequence) {
                    builder.putText(key, (CharSequence) value);
                } else if (value instanceof Long) {
                    builder.putLong(key, (Long) value);
                } else if (value instanceof Integer) {
                    builder.putLong(key, (Integer) value);
                } else if (value instanceof Bitmap) {
                    builder.putBitmap(key, (Bitmap) value);
                } else if (value instanceof RatingCompat) {
                    builder.putRating(key, (RatingCompat) value);
                }
            }
        }
        CharSequence title = description.getTitle();
        if (title != null) {
            String titleString = String.valueOf(title);
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, titleString);
            builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, titleString);
        }
        CharSequence subtitle = description.getSubtitle();
        if (subtitle != null) {
            builder.putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, String.valueOf(subtitle));
        }
        CharSequence displayDescription = description.getDescription();
        if (displayDescription != null) {
            builder.putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                    String.valueOf(displayDescription));
        }
        Bitmap iconBitmap = description.getIconBitmap();
        if (iconBitmap != null) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, iconBitmap);
        }
        Uri iconUri = description.getIconUri();
        if (iconUri != null) {
            builder.putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, String.valueOf(iconUri));
        }
        String mediaId = description.getMediaId();
        if (mediaId != null) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId);
        }

        Uri mediaUri = description.getMediaUri();
        if (mediaUri != null) {
            builder.putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI, String.valueOf(mediaUri));
        }
        return builder.build();
    }

    private void updateMediaSessionPlaybackState() {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setActions(buildPlaybackActions());
        builder.setState(mapPlaybackState(), player.getCurrentPosition(),
                player.getPlaybackParameters().speed,
                SystemClock.elapsedRealtime());
        builder.setBufferedPosition(player.getBufferedPosition());
        mediaSession.setPlaybackState(builder.build());
    }

    private int mapPlaybackState() {
        switch (player.getPlaybackState()) {
            case FuPlayer.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case FuPlayer.STATE_READY:
                return player.getPlayWhenReady() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            case FuPlayer.STATE_ENDED:
                return PlaybackStateCompat.STATE_STOPPED;
            default:
                if (player.getPlaybackError() != null) {
                    return PlaybackStateCompat.STATE_ERROR;
                }
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    private long buildPlaybackActions() {
        boolean enableSeeking = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableSetRating = false;
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty() && !player.isPlayingAd()) {
            enableSeeking = player.isCurrentWindowDynamic() || player.getDuration() == C.TIME_UNSET || player.getDuration() <= 0 || player.isCurrentWindowSeekable();
            enableRewind = enableSeeking && rewindMs > 0;
            enableFastForward = enableSeeking && fastForwardMs > 0;
            enableSetRating = true;
        }

        long playbackActions = BASE_PLAYBACK_ACTIONS;
        if (enableSeeking) {
            playbackActions |= PlaybackStateCompat.ACTION_SEEK_TO;
        }
        if (enableFastForward) {
            playbackActions |= PlaybackStateCompat.ACTION_FAST_FORWARD;
        }
        if (enableRewind) {
            playbackActions |= PlaybackStateCompat.ACTION_REWIND;
        }
        playbackActions &= enabledPlaybackActions;

        if (queueAdapter != null && queueAdapter.hasNext()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        }
        if (queueAdapter != null && queueAdapter.hasPrevious()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        }
        if (enableSetRating) {
            playbackActions |= PlaybackStateCompat.ACTION_SET_RATING;
        }
        if (queueAdapter != null && queueAdapter.getItemCount() > 0) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
        }
        return playbackActions;
    }

    public void stop() {
        player.stop();
    }

    private class PlayerEventListener implements FuPlayer.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            FuLog.d(TAG, "onTimelineChanged : timeline=" + timeline + ",manifest=" + manifest + ",reason=" + reason);
            updateMediaSessionMetadata();
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            FuLog.d(TAG, "onLoadingChanged : isLoading=" + isLoading);
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            FuLog.d(TAG, "onPlayerStateChanged : playWhenReady=" + playWhenReady + ",playbackState=" + playbackState);
            if (playbackState == FuPlayer.STATE_ENDED) {
                mediaSessionCallback.onSkipToNext();
            }
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            FuLog.d(TAG, "onIsPlayingChanged : isPlaying=" + isPlaying);
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            FuLog.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            FuLog.d(TAG, "onShuffleModeEnabledChanged : shuffleModeEnabled=" + shuffleModeEnabled);
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            FuLog.d(TAG, "onPlaybackParametersChanged : playbackParameters=" + playbackParameters);
            updateMediaSessionPlaybackState();
        }

        @Override
        public void onSeekProcessed() {
            updateMediaSessionPlaybackState();
        }
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
        }

        @Override
        public void onPrepare() {
//            MediaSessionCompat.QueueItem item = queueAdapter != null ? queueAdapter.getActiveItem() : null;
//            if (item != null) {
//                mediaSource = ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), null, dataSourceFactory, item.getDescription());
//                player.prepare(mediaSource);
//            }
            if (queueAdapter != null) {
                queueAdapter.setActivePosition(0);
            }
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
//            MediaSessionCompat.QueueItem item = queueAdapter != null ? queueAdapter.skipToMediaId(mediaId) : null;
//            if (item != null) {
//                mediaSource = ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), null, dataSourceFactory, item.getDescription());
//                player.prepare(mediaSource);
//            }

            if (queueAdapter != null) {
                queueAdapter.skipToMediaId(mediaId);
            }
        }

        @Override
        public void onPlay() {
            if (player.getPlaybackState() == FuPlayer.STATE_IDLE) {
                onPrepare();
            } else if (player.getPlaybackState() == FuPlayer.STATE_ENDED) {
                onSeekTo(0);
            }
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
//            if (player.getPlaybackState() == FuPlayer.STATE_IDLE) {
//                onPrepareFromMediaId(mediaId, extras);
//            }
//            player.setPlayWhenReady(true);

            if (queueAdapter != null) {
                MediaSessionCompat.QueueItem item = queueAdapter.getActiveItem();
                if (item != null && mediaId.equals(item.getDescription().getMediaId())) {
                    onPlay();
                } else {
                    queueAdapter.skipToMediaId(mediaId);
                    player.setPlayWhenReady(true);
                }
            }
        }

        @Override
        public void onSkipToNext() {
//            MediaSessionCompat.QueueItem item = queueAdapter != null ? queueAdapter.skipToNext() : null;
//            if (item != null) {
//                mediaSource = ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), null, dataSourceFactory, item.getDescription());
//                player.prepare(mediaSource);
//            }

            if (queueAdapter != null) {
                queueAdapter.skipToNext();
                player.setPlayWhenReady(true);
            }
        }

        @Override
        public void onSkipToPrevious() {
//            MediaSessionCompat.QueueItem item = queueAdapter != null ? queueAdapter.skipToPrevious() : null;
//            if (item != null) {
//                mediaSource = ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), null, dataSourceFactory, item.getDescription());
//                player.prepare(mediaSource);
//            }

            if (queueAdapter != null) {
                queueAdapter.skipToPrevious();
                player.setPlayWhenReady(true);
            }
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }

        @Override
        public void onRewind() {
            player.seekTo(5000);
        }

        @Override
        public void onFastForward() {
            player.seekTo(player.getDuration() - 5000);
        }

        @Override
        public void onSeekTo(long pos) {
            player.seekTo(pos);
        }

        @Override
        public void onStop() {
            super.onStop();
        }
    }

    private class DataChangedListener implements QueueAdapter.DataChangedListener {

        @Override
        public void onDataChanged(List<MediaSessionCompat.QueueItem> list) {
            if (mediaSession != null && mediaSession.isActive()) {
                mediaSession.setQueue(list);
            }
        }

        @Override
        public void onActiveItemChanged(MediaSessionCompat.QueueItem item) {
            if (item != null) {
                mediaSource = ExoMediaSourceUtil.buildMediaSource(item.getDescription().getMediaUri(), null, dataSourceFactory, item.getDescription());
                player.prepare(mediaSource);
            }
            updateMediaSessionMetadata();
        }
    }
}
