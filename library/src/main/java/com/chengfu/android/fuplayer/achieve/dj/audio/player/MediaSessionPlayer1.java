package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.MediaSessionUtil;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.QueueListUtil;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class MediaSessionPlayer1 {
    public static final String TAG = "MediaSessionPlayer";

    public static final long BASE_PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            | PlaybackStateCompat.ACTION_SET_REPEAT_MODE;

    private static final MediaMetadataCompat METADATA_EMPTY =
            new MediaMetadataCompat.Builder().build();

    private static final PlaybackStateCompat INITIAL_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
            .build();

    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;

    @NonNull
    private Context mContext;
    @NonNull
    private final MediaSessionCompat mMediaSession;

    @NonNull
    private final PlayerEventListener mPlayerEventListener;
    @NonNull
    private final MediaSessionCallback mMediaSessionCallback;

    @Nullable
    private final MetadataInfo mMetadataInfo;
    @Nullable
    private final PlaybackStateInfo mPlaybackStateInfo;

    @NonNull
    private final FuPlayer mPlayer;
    @Nullable
    private MediaSourceAdapter mMediaSourceAdapter;

    private long enabledPlaybackActions;
    private int rewindMs;
    private int fastForwardMs;


    public MediaSessionPlayer1(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {
        mContext = context;
        mMediaSession = mediaSession;

        mPlayerEventListener = new PlayerEventListener();
        mMediaSessionCallback = new MediaSessionCallback();

        mMetadataInfo = new MetadataInfo();
        mPlaybackStateInfo = new PlaybackStateInfo();


        mMediaSession.setCallback(mMediaSessionCallback, new Handler(Util.getLooper()));
        mediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
        mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
        mediaSession.setMetadata(METADATA_EMPTY);
        mediaSession.setPlaybackState(INITIAL_PLAYBACK_STATE);

        mPlayer = new FuExoPlayerFactory(mContext).create();
        mPlayer.addListener(mPlayerEventListener);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();
        if (mPlayer.getAudioComponent() != null) {
            mPlayer.getAudioComponent().setAudioAttributes(attributes, true);
        }

        mMediaSourceAdapter = new MediaSourceAdapter();
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @NonNull
    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    @NonNull
    public FuPlayer getPlayer() {
        return mPlayer;
    }

    @Nullable
    public MediaSourceAdapter getMediaSourceAdapter() {
        return mMediaSourceAdapter;
    }

    public void setMediaSourceAdapter(@Nullable MediaSourceAdapter mMediaSourceAdapter) {
        this.mMediaSourceAdapter = mMediaSourceAdapter;
    }

    private void invalidateMediaSessionMetadata() {
        boolean isPlayingAd = mPlayer.isPlayingAd();
        long duration = mPlayer.isCurrentWindowDynamic() || mPlayer.getDuration() == C.TIME_UNSET || mPlayer.getDuration() <= 0 ? -1 : mPlayer.getDuration();

        MediaSessionCompat.QueueItem tag = (mPlayer.getCurrentTag() instanceof MediaSessionCompat.QueueItem) ?
                (MediaSessionCompat.QueueItem) mPlayer.getCurrentTag() : null;

        if (mMetadataInfo.duration != duration
                || mMetadataInfo.isPlayingAd != isPlayingAd
                || mMetadataInfo.tag != tag) {
            mMetadataInfo.set(isPlayingAd, duration, tag);
            mMediaSession.setMetadata(getMetadata(mMetadataInfo));
        } else {
            mMetadataInfo.set(isPlayingAd, duration, tag);
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
        int repeatMode = mPlayer.getRepeatMode();
        return repeatMode == FuPlayer.REPEAT_MODE_ONE
                ? PlaybackStateCompat.REPEAT_MODE_ONE
                : repeatMode == FuPlayer.REPEAT_MODE_ALL
                ? PlaybackStateCompat.REPEAT_MODE_ALL
                : PlaybackStateCompat.REPEAT_MODE_NONE;
    }

    private int mapShuffleMode() {
        return mPlayer.getShuffleModeEnabled()
                ? PlaybackStateCompat.SHUFFLE_MODE_ALL
                : PlaybackStateCompat.SHUFFLE_MODE_NONE;
    }

    private int mapPlaybackState() {
        switch (mPlayer.getPlaybackState()) {
            case FuPlayer.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case FuPlayer.STATE_READY:
                return mPlayer.getPlayWhenReady() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            case FuPlayer.STATE_ENDED:
                return PlaybackStateCompat.STATE_STOPPED;
            default:
                if (mPlayer.getPlaybackError() != null) {
                    return PlaybackStateCompat.STATE_ERROR;
                }
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    private boolean enableSeeking() {
        Timeline timeline = mPlayer.getCurrentTimeline();
        if (!timeline.isEmpty() && !mPlayer.isPlayingAd()) {
            return !mPlayer.isCurrentWindowDynamic() && mPlayer.getDuration() > 0 && mPlayer.isCurrentWindowSeekable();
        }
        return false;
    }

    private long buildPlaybackActions() {
        boolean enableSeeking = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableSetRating = false;
        Timeline timeline = mPlayer.getCurrentTimeline();
        if (!timeline.isEmpty() && !mPlayer.isPlayingAd()) {
            enableSeeking = enableSeeking();
            enableRewind = enableSeeking && rewindMs > 0;
            enableFastForward = enableSeeking && fastForwardMs > 0;
            enableSetRating = true;
        }

        long playbackActions = BASE_PLAYBACK_ACTIONS;

        boolean playWhenReady = mPlayer.getPlayWhenReady();
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

        if (mPlayer.hasNext()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        }
        if (mPlayer.hasPrevious()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        }
        if (enableSetRating) {
            playbackActions |= PlaybackStateCompat.ACTION_SET_RATING;
        }
//        if (queueItemList.size() > 0) {
//            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
//        }
        return playbackActions;
    }

    private void invalidateMediaSessionPlaybackState() {
        long actions = buildPlaybackActions();
        long bufferedPosition = enableSeeking() ? mPlayer.getBufferedPosition() : 0;
        long currentPosition = mPlayer.getCurrentPosition();
        float playbackSpeed = mPlayer.getPlaybackParameters().speed;
        int state = mapPlaybackState();
        int repeatMode = mapRepeatMode();
        int shuffleMode = mapShuffleMode();
        MediaSessionCompat.QueueItem tag = (mPlayer.getCurrentTag() instanceof MediaSessionCompat.QueueItem) ?
                (MediaSessionCompat.QueueItem) mPlayer.getCurrentTag() : null;

        if (mPlaybackStateInfo.actions != actions ||
                mPlaybackStateInfo.bufferedPosition != bufferedPosition ||
                mPlaybackStateInfo.state != mapPlaybackState() ||
                mPlaybackStateInfo.playbackSpeed != mPlayer.getPlaybackParameters().speed ||
                mPlaybackStateInfo.position != mPlayer.getCurrentPosition() ||
                mPlaybackStateInfo.repeatMode != repeatMode ||
                mPlaybackStateInfo.shuffleMode != shuffleMode ||
                mPlaybackStateInfo.tag != tag) {

            if (mPlaybackStateInfo.tag != tag && tag != null) {
//                invalidateRecentList(tag.getDescription());
            }
            mPlaybackStateInfo.set(actions, bufferedPosition, state, currentPosition, playbackSpeed, repeatMode, shuffleMode, tag);
            mMediaSession.setPlaybackState(getPlaybackState(mPlaybackStateInfo));
        } else {
            mPlaybackStateInfo.set(actions, bufferedPosition, state, currentPosition, playbackSpeed, repeatMode, shuffleMode, tag);
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
        mMediaSession.setRepeatMode(playbackStateInfo.repeatMode);
        mMediaSession.setShuffleMode(playbackStateInfo.shuffleMode);
        return builder.build();
    }

    private class PlayerEventListener implements FuPlayer.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            FuLog.d(TAG, "onTimelineChanged : timeline=" + timeline + ",reason=" + reason);
            invalidateMediaSessionMetadata();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            FuLog.d(TAG, "onPlayerStateChanged : playWhenReady=" + playWhenReady + ",playbackState=" + playbackState);
            invalidateMediaSessionPlaybackState();
        }
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            FuLog.d(TAG, "onCommand : command=" + command + ",extras=" + extras);
            onPrepare();
        }

        @Override
        public void onPrepare() {
//            MediaSource mediaSource = mMediaSourceAdapter.onCreateMediaSource(0);
//            mPlayer.prepare(mediaSource);
//            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            mMediaSourceAdapter.addQueueItem(description);
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description, int index) {
            mMediaSourceAdapter.addQueueItem(description, index);
        }
    }

    private class MediaSourceAdapter {
        List<MediaSessionCompat.QueueItem> queueItemList = new ArrayList<>();

        int size() {
            return queueItemList.size();
        }

        void addQueueItem(MediaDescriptionCompat description) {
            addQueueItem(description, size());
        }

        void addQueueItem(MediaDescriptionCompat description, int index) {
            if (description == null || MediaSessionUtil.search(queueItemList, description) >= 0) {
                return;
            }

            MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, MediaSessionUtil.maxId(queueItemList) + 1);

            queueItemList.add(index, queueItem);

            mMediaSession.setQueue(queueItemList);
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
