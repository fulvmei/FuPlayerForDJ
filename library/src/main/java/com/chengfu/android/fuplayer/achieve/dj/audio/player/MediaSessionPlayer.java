package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.content.Context;
import android.content.Intent;
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

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.QueueListUtil;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
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

    public static final long BASE_PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            | PlaybackStateCompat.ACTION_SET_REPEAT_MODE;

    public static final long DEFAULT_PLAYBACK_ACTIONS = ALL_PLAYBACK_ACTIONS;
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;


    private static final MediaMetadataCompat METADATA_EMPTY =
            new MediaMetadataCompat.Builder().build();

    private static final PlaybackStateCompat INITIAL_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
            .build();

    private final PlayerEventListener playerEventListener;
    private final MediaSessionCallback mediaSessionCallback;

    private Context context;
    private final FuPlayer player;

    private final MediaSessionCompat mediaSession;

    private final ConcatenatingMediaSource concatenatingMediaSource;
    private final DefaultDataSourceFactory dataSourceFactory;

    private long enabledPlaybackActions;
    private int rewindMs;
    private int fastForwardMs;

    private final MetadataInfo metadataInfo;
    private final PlaybackStateInfo playbackStateInfo;

    private final List<MediaSessionCompat.QueueItem> queueItemList;

    public MediaSessionPlayer(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {
        this.context = context;
        metadataInfo = new MetadataInfo();
        playbackStateInfo = new PlaybackStateInfo();
        enabledPlaybackActions = DEFAULT_PLAYBACK_ACTIONS;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, context.getApplicationInfo().packageName), null);

        //init source
        queueItemList = new ArrayList<>();
        concatenatingMediaSource = new ConcatenatingMediaSource();

        //init mediaSession
        this.mediaSession = mediaSession;
        mediaSessionCallback = new MediaSessionCallback();
        mediaSession.setCallback(mediaSessionCallback, new Handler(Util.getLooper()));
        mediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
        mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
        mediaSession.setMetadata(METADATA_EMPTY);
        mediaSession.setPlaybackState(INITIAL_PLAYBACK_STATE);

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
    }

    public List<MediaSessionCompat.QueueItem> getQueueItemList() {
        return queueItemList;
    }

    public FuPlayer getPlayer() {
        return player;
    }

    private void invalidateMediaSessionMetadata() {
        boolean isPlayingAd = player.isPlayingAd();
        long duration = player.isCurrentWindowDynamic() || player.getDuration() == C.TIME_UNSET || player.getDuration() <= 0 ? -1 : player.getDuration();

        MediaSessionCompat.QueueItem tag = (player.getCurrentTag() instanceof MediaSessionCompat.QueueItem) ?
                (MediaSessionCompat.QueueItem) player.getCurrentTag() : null;

        if (metadataInfo.duration != duration
                || metadataInfo.isPlayingAd != isPlayingAd
                || metadataInfo.tag != tag) {
            metadataInfo.set(isPlayingAd, duration, tag);
            mediaSession.setMetadata(getMetadata(metadataInfo));
        } else {
            metadataInfo.set(isPlayingAd, duration, tag);
        }
    }

    private MediaMetadataCompat getMetadata(@NonNull MetadataInfo metadataInfo) {
        if (metadataInfo.tag == null || metadataInfo.tag.getDescription() == null) {
            return METADATA_EMPTY;
        }
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putLong(MediaMetadataCompat.METADATA_KEY_ADVERTISEMENT, metadataInfo.isPlayingAd ? 1 : 0);
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataInfo.duration);

        MediaDescriptionCompat description = metadataInfo.tag.getDescription();

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

    private int mapRepeatMode() {
        int repeatMode = player.getRepeatMode();
        return repeatMode == FuPlayer.REPEAT_MODE_ONE
                ? PlaybackStateCompat.REPEAT_MODE_ONE
                : repeatMode == FuPlayer.REPEAT_MODE_ALL
                ? PlaybackStateCompat.REPEAT_MODE_ALL
                : PlaybackStateCompat.REPEAT_MODE_NONE;
    }

    private int mapShuffleMode() {
        return player.getShuffleModeEnabled()
                ? PlaybackStateCompat.SHUFFLE_MODE_ALL
                : PlaybackStateCompat.SHUFFLE_MODE_NONE;
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

    private boolean enableSeeking() {
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty() && !player.isPlayingAd()) {
            return !player.isCurrentWindowDynamic() && player.getDuration() > 0 && player.isCurrentWindowSeekable();
        }
        return false;
    }

    private long buildPlaybackActions() {
        boolean enableSeeking = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableSetRating = false;
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty() && !player.isPlayingAd()) {
            enableSeeking = enableSeeking();
            enableRewind = enableSeeking && rewindMs > 0;
            enableFastForward = enableSeeking && fastForwardMs > 0;
            enableSetRating = true;
        }

        long playbackActions = BASE_PLAYBACK_ACTIONS;

        boolean playWhenReady = player.getPlayWhenReady();
        if (playWhenReady) {
            playbackActions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            playbackActions |= PlaybackStateCompat.ACTION_PLAY;
        }
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

        if (player.hasNext()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        }
        if (player.hasPrevious()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        }
        if (enableSetRating) {
            playbackActions |= PlaybackStateCompat.ACTION_SET_RATING;
        }
        if (queueItemList.size() > 0) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
        }
        return playbackActions;
    }

    private void invalidateMediaSessionPlaybackState() {
        long actions = buildPlaybackActions();
        long bufferedPosition = enableSeeking() ? player.getBufferedPosition() : 0;
        long currentPosition = player.getCurrentPosition();
        float playbackSpeed = player.getPlaybackParameters().speed;
        int state = mapPlaybackState();
        int repeatMode = mapRepeatMode();
        int shuffleMode = mapShuffleMode();
        MediaSessionCompat.QueueItem tag = (player.getCurrentTag() instanceof MediaSessionCompat.QueueItem) ?
                (MediaSessionCompat.QueueItem) player.getCurrentTag() : null;

        if (playbackStateInfo.actions != actions ||
                playbackStateInfo.bufferedPosition != bufferedPosition ||
                playbackStateInfo.state != mapPlaybackState() ||
                playbackStateInfo.playbackSpeed != player.getPlaybackParameters().speed ||
                playbackStateInfo.position != player.getCurrentPosition() ||
                playbackStateInfo.repeatMode != repeatMode ||
                playbackStateInfo.shuffleMode != shuffleMode ||
                playbackStateInfo.tag != tag) {

            if (playbackStateInfo.tag != tag && tag != null) {
                invalidateRecentList(tag.getDescription());
            }
            playbackStateInfo.set(actions, bufferedPosition, state, currentPosition, playbackSpeed, repeatMode, shuffleMode, tag);
            mediaSession.setPlaybackState(getPlaybackState(playbackStateInfo));
        } else {
            playbackStateInfo.set(actions, bufferedPosition, state, currentPosition, playbackSpeed, repeatMode, shuffleMode, tag);
        }
    }

    public PlaybackStateCompat getPlaybackState(PlaybackStateInfo playbackStateInfo) {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setActions(playbackStateInfo.actions);
        builder.setState(playbackStateInfo.state, playbackStateInfo.position,
                playbackStateInfo.playbackSpeed,
                SystemClock.elapsedRealtime());
        builder.setActiveQueueItemId(playbackStateInfo.tag != null ? playbackStateInfo.tag.getQueueId() : MediaSessionCompat.QueueItem.UNKNOWN_ID);
        builder.setBufferedPosition(playbackStateInfo.bufferedPosition);
        mediaSession.setRepeatMode(playbackStateInfo.repeatMode);
        mediaSession.setShuffleMode(playbackStateInfo.shuffleMode);
        return builder.build();
    }

    private void invalidateRecentList(MediaDescriptionCompat media) {
        AudioPlayClient.addToRecentList(context, media);
    }

    public void stop(boolean reset) {
        player.stop(reset);
    }

    public void release() {
        queueItemList.clear();
        concatenatingMediaSource.clear();
        mediaSession.setQueue(queueItemList);
        stop(true);
    }

    private class PlayerEventListener implements FuPlayer.EventListener {
        private int currentWindowIndex;
        private int currentWindowCount;

        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            FuLog.d(TAG, "onTimelineChanged : timeline=" + timeline + ",reason=" + reason);
            int windowCount = player.getCurrentTimeline().getWindowCount();
            int windowIndex = player.getCurrentWindowIndex();
            if (currentWindowCount != windowCount || currentWindowIndex != windowIndex) {
                invalidateMediaSessionPlaybackState();
            }
            currentWindowCount = windowCount;
            currentWindowIndex = windowIndex;
            invalidateMediaSessionMetadata();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            FuLog.d(TAG, "onPlayerStateChanged : playWhenReady=" + playWhenReady + ",playbackState=" + playbackState);
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            FuLog.d(TAG, "onIsPlayingChanged : isPlaying=" + isPlaying);
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            FuLog.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            FuLog.d(TAG, "onShuffleModeEnabledChanged : shuffleModeEnabled=" + shuffleModeEnabled);
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            FuLog.d(TAG, "onPlaybackParametersChanged : playbackParameters=" + playbackParameters);
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            FuLog.d(TAG, "onPositionDiscontinuity : reason=" + reason);
            if (currentWindowIndex != player.getCurrentWindowIndex()) {
                currentWindowIndex = player.getCurrentWindowIndex();
                invalidateMediaSessionPlaybackState();
                invalidateMediaSessionMetadata();
            } else {
                invalidateMediaSessionPlaybackState();
            }
        }
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPrepare() {
            player.prepare(concatenatingMediaSource);
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            int initialWindowIndex = QueueListUtil.getPositionByMediaId(queueItemList, mediaId);
            player.prepare(concatenatingMediaSource);
            if (initialWindowIndex > 0) {
                player.seekTo(initialWindowIndex, 0);
            }
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            FuLog.d(TAG, "onCommand : command=" + command + ",extras=" + extras);
            if (MusicContract.COMMAND_SET_QUEUE_ITEMS.equals(command)) {
                if (extras != null) {
                    queueItemList.clear();
                    concatenatingMediaSource.clear();
                    extras.setClassLoader(getClass().getClassLoader());
                    ArrayList<MediaDescriptionCompat> list = extras.getParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS);
                    QueueListUtil.addQueueItems(concatenatingMediaSource, queueItemList, list, dataSourceFactory);
                    mediaSession.setQueue(queueItemList);
                    if (queueItemList.size() == 0) {
                        stop(true);
                    } else {
                        onPrepare();
                    }
                }
            } else if (MusicContract.COMMAND_ADD_QUEUE_ITEMS.equals(command)) {
                if (extras != null) {
                    extras.setClassLoader(getClass().getClassLoader());
                    ArrayList<MediaDescriptionCompat> list = extras.getParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS);
                    int index = extras.getInt(MusicContract.KEY_QUEUE_ITEMS, MusicContract.DEFAULT_QUEUE_ADD_INDEX);

                    if (QueueListUtil.addQueueItems(concatenatingMediaSource, queueItemList, index, list, dataSourceFactory) > 0) {
                        mediaSession.setQueue(queueItemList);
                        if (player.getPlaybackState() == FuPlayer.STATE_IDLE && player.getPlaybackError() == null) {
                            onPrepare();
                        }
                    }
                }
            } else if (MusicContract.COMMAND_CLEAR_QUEUE_ITEMS.equals(command)) {
                if (queueItemList.size() != 0) {
                    queueItemList.clear();
                    mediaSession.setQueue(queueItemList);
                }
                if (concatenatingMediaSource.getSize() != 0) {
                    concatenatingMediaSource.clear();
                }
                stop(true);
            } else if (MusicContract.COMMAND_ADD_TO_CURRENT_PLAY.equals(command)) {
                if (extras == null) {
                    return;
                }
                extras.setClassLoader(getClass().getClassLoader());
                MediaDescriptionCompat media = extras.getParcelable(MusicContract.KEY_QUEUE_ITEM);
                if (media == null) {
                    return;
                }
                int mediaPosition = QueueListUtil.getPositionByMediaId(queueItemList, media.getMediaId());
                if (mediaPosition > -1) {
                    onPrepareFromMediaId(media.getMediaId(), null);
                } else {
                    if (QueueListUtil.addQueueItem(concatenatingMediaSource, queueItemList, 0, media, dataSourceFactory) > 0) {
                        mediaSession.setQueue(queueItemList);
                        onPrepareFromMediaId(media.getMediaId(), null);
                    }
                }
            } else if (MusicContract.COMMAND_ADD_TO_NEXT_PLAY.equals(command)) {
//                if (extras == null) {
//                    return;
//                }
//                extras.setClassLoader(getClass().getClassLoader());
//                MediaDescriptionCompat media = extras.getParcelable(MusicContract.KEY_QUEUE_ITEM);
//                if (media == null) {
//                    return;
//                }
//                PlaybackStateCompat playbackState = mediaSession.getController().getPlaybackState();
//                int activePosition = QueueListUtil.getPositionById(queueItemList, playbackState.getActiveQueueItemId());
            }
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            if (QueueListUtil.addQueueItem(concatenatingMediaSource, queueItemList, description, dataSourceFactory) > 0) {
                mediaSession.setQueue(queueItemList);
                if (player.getPlaybackState() == FuPlayer.STATE_IDLE && player.getPlaybackError() == null) {
                    onPrepare();
                }
            }
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description, int index) {
            if (QueueListUtil.addQueueItem(concatenatingMediaSource, queueItemList, index, description, dataSourceFactory) > 0) {
                mediaSession.setQueue(queueItemList);
                if (player.getPlaybackState() == FuPlayer.STATE_IDLE && player.getPlaybackError() == null) {
                    onPrepare();
                }
            }
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            if (QueueListUtil.removeQueueItem(concatenatingMediaSource, queueItemList, description) > 0) {
                mediaSession.setQueue(queueItemList);
                if (queueItemList.size() == 0) {
                    stop(true);
                }
            }
        }

        @Override
        public void onPlay() {
            if (player.getPlaybackState() == FuPlayer.STATE_IDLE || player.getPlaybackError() != null) {
                player.retry();
            } else if (player.getPlaybackState() == FuPlayer.STATE_ENDED) {
                onSeekTo(0);
            }
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            int position = QueueListUtil.getPositionByMediaId(queueItemList, mediaId);
            if (position >= 0 && position < player.getCurrentTimeline().getWindowCount()) {
                player.seekTo(position, 0);
                player.setPlayWhenReady(true);
            }
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToNext() {
            if (player.hasNext()) {
                player.next();
            }
        }

        @Override
        public void onSkipToQueueItem(long id) {
            int position = QueueListUtil.getPositionById(queueItemList, id);
            if (position >= 0 && position < player.getCurrentTimeline().getWindowCount()) {
                player.seekTo(position, 0);
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (player.hasPrevious()) {
                player.previous();
            }
        }

        @Override
        public void onRewind() {
            if (player.isCurrentWindowSeekable() && rewindMs > 0) {
                player.seekTo(player.getCurrentPosition() - rewindMs);
            }
        }

        @Override
        public void onFastForward() {
            if (player.isCurrentWindowSeekable() && fastForwardMs > 0) {
                player.seekTo(player.getCurrentPosition() + fastForwardMs);
            }
        }

        @Override
        public void onSeekTo(long pos) {
            if (player.isCurrentWindowSeekable()) {
                player.seekTo(pos);
            }
        }

        @Override
        public void onStop() {
            player.stop();
        }

        @Override
        public void onSetRepeatMode(int mediaSessionRepeatMode) {
            @RepeatModeUtil.RepeatToggleModes int repeatMode;
            switch (mediaSessionRepeatMode) {
                case PlaybackStateCompat.REPEAT_MODE_ALL:
                case PlaybackStateCompat.REPEAT_MODE_GROUP:
                    repeatMode = FuPlayer.REPEAT_MODE_ALL;
                    break;
                case PlaybackStateCompat.REPEAT_MODE_ONE:
                    repeatMode = FuPlayer.REPEAT_MODE_ONE;
                    break;
                case PlaybackStateCompat.REPEAT_MODE_NONE:
                case PlaybackStateCompat.REPEAT_MODE_INVALID:
                default:
                    repeatMode = FuPlayer.REPEAT_MODE_OFF;
                    break;
            }
            player.setRepeatMode(repeatMode);
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            boolean shuffleModeEnabled;
            switch (shuffleMode) {
                case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                case PlaybackStateCompat.SHUFFLE_MODE_GROUP:
                    shuffleModeEnabled = true;
                    break;
                case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                case PlaybackStateCompat.SHUFFLE_MODE_INVALID:
                default:
                    shuffleModeEnabled = false;
                    break;
            }
            player.setShuffleModeEnabled(shuffleModeEnabled);
        }
    }

    private static final class MetadataInfo {
        boolean isPlayingAd;
        long duration;
        MediaSessionCompat.QueueItem tag;

        MetadataInfo set(boolean isPlayingAd, long duration, MediaSessionCompat.QueueItem tag) {
            this.isPlayingAd = isPlayingAd;
            this.duration = duration;
            this.tag = tag;
            return this;
        }
    }

    private static final class PlaybackStateInfo {
        long actions;
        long bufferedPosition;
        int state;
        long position;
        float playbackSpeed;
        int repeatMode;
        int shuffleMode;
        MediaSessionCompat.QueueItem tag;

        public PlaybackStateInfo set(long actions, long bufferedPosition, int state, long position, float playbackSpeed, int repeatMode, int shuffleMode, MediaSessionCompat.QueueItem tag) {
            this.actions = actions;
            this.bufferedPosition = bufferedPosition;
            this.state = state;
            this.position = position;
            this.playbackSpeed = playbackSpeed;
            this.repeatMode = repeatMode;
            this.shuffleMode = shuffleMode;
            this.tag = tag;
            return this;
        }
    }


}
