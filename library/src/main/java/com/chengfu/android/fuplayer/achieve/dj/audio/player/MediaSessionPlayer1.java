package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.MediaSessionUtil;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ShuffleOrder;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class MediaSessionPlayer1 {
    public static final String TAG = "MediaSessionPlayer";

    public interface MediaLoadProvider {
        void onLoadMedia(MediaDescriptionCompat description, MediaLoadCallback callback);
    }

    public static final long ALL_PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PLAY
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

    private static final MediaMetadataCompat METADATA_EMPTY =
            new MediaMetadataCompat.Builder().build();

    private static final PlaybackStateCompat INITIAL_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
            .build();

    public static final long DEFAULT_PLAYBACK_ACTIONS = ALL_PLAYBACK_ACTIONS;
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

    private MediaLoadProvider mMediaLoadProvider;

    @Nullable
    private final MetadataInfo mMetadataInfo;
    @Nullable
    private final PlaybackStateInfo mPlaybackStateInfo;

    @NonNull
    private final FuPlayer mPlayer;

    private long enabledPlaybackActions;
    private int rewindMs;
    private int fastForwardMs;

    private TimingOff currentTimingOff = TimingOff.defaultTimingOff();
    private CountDownTimer countDownTimer;

    public MediaSessionPlayer1(@NonNull Context context, @NonNull MediaSessionCompat mediaSession, @NonNull FuPlayer player) {
        mContext = context;
        mMediaSession = mediaSession;

        enabledPlaybackActions = DEFAULT_PLAYBACK_ACTIONS;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;

        mPlayerEventListener = new PlayerEventListener();
        mMediaSessionCallback = new MediaSessionCallback();

        mMetadataInfo = new MetadataInfo();
        mPlaybackStateInfo = new PlaybackStateInfo();


        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);
        mMediaSession.setCallback(mMediaSessionCallback, null);
        mMediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
        mMediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
        mMediaSession.setMetadata(METADATA_EMPTY);
        mMediaSession.setPlaybackState(INITIAL_PLAYBACK_STATE);
        invalidateMediaSessionExtras();
        updateTimingOff();

        mPlayer = player;
        mPlayer.addListener(mPlayerEventListener);
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

    public List<MediaSessionCompat.QueueItem> getQueueItemList() {
        return mMediaSessionCallback.mQueueItemList;
    }

    public void setMediaLoadProvider(MediaLoadProvider mediaLoadProvider) {
        this.mMediaLoadProvider = mediaLoadProvider;
    }

    private void invalidateMediaSessionMetadata() {
        boolean isPlayingAd = mPlayer.isPlayingAd();
        long duration = mPlayer.isCurrentWindowDynamic() || mPlayer.getDuration() == C.TIME_UNSET || mPlayer.getDuration() <= 0 ? -1 : mPlayer.getDuration();
        MediaSessionCompat.QueueItem tag = mMediaSessionCallback.mActiveQueueItem;
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


    private int mapPlaybackState(@Player.State int state) {
        switch (state) {
            case FuPlayer.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case FuPlayer.STATE_READY:
                return mPlayer.getPlayWhenReady() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            case FuPlayer.STATE_ENDED:
                return PlaybackStateCompat.STATE_STOPPED;
            default:
                if (mPlayer.getPlaybackError() != null) {
                    return PlaybackStateCompat.STATE_ERROR;
                } else if (mMediaSessionCallback.mPendingPrepare) {
                    return PlaybackStateCompat.STATE_BUFFERING;
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

        if (mMediaSessionCallback.hasNext()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        }
        if (mMediaSessionCallback.hasPrevious()) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        }
        if (enableSetRating) {
            playbackActions |= PlaybackStateCompat.ACTION_SET_RATING;
        }
        if (mMediaSessionCallback.mQueueItemList.size() > 0) {
            playbackActions |= PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
        }
        return playbackActions;
    }

    private void invalidateMediaSessionPlaybackState() {
        long actions = buildPlaybackActions();
        long bufferedPosition = enableSeeking() ? mPlayer.getBufferedPosition() : 0;
        long currentPosition = mPlayer.getCurrentPosition();
        float playbackSpeed = mPlayer.getPlaybackParameters().speed;
        int state = mMediaSessionCallback.mState;
        int repeatMode = mMediaSessionCallback.mCurrentRepeatMode;
        int shuffleMode = mMediaSessionCallback.mCurrentShuffleMode;
//        MediaSessionCompat.QueueItem tag = (mPlayer.getCurrentTag() instanceof MediaSessionCompat.QueueItem) ?
//                (MediaSessionCompat.QueueItem) mPlayer.getCurrentTag() : null;

        MediaSessionCompat.QueueItem tag = mMediaSessionCallback.mActiveQueueItem;

        if (mPlaybackStateInfo.actions != actions ||
                mPlaybackStateInfo.bufferedPosition != bufferedPosition ||
                mPlaybackStateInfo.state != state ||
                mPlaybackStateInfo.playbackSpeed != mPlayer.getPlaybackParameters().speed ||
                mPlaybackStateInfo.position != mPlayer.getCurrentPosition() ||
                mPlaybackStateInfo.repeatMode != repeatMode ||
                mPlaybackStateInfo.shuffleMode != shuffleMode ||
                mPlaybackStateInfo.tag != tag) {

            if (mPlaybackStateInfo.tag != tag && tag != null) {
                invalidateRecentList(tag.getDescription());
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

    private void invalidateRecentList(MediaDescriptionCompat media) {
        AudioPlayClient.addToRecentList(mContext, media);
    }

    private void invalidateMediaSessionExtras() {
        Bundle bundle = new Bundle();
//        bundle.setClassLoader(TimingOff.class.getClassLoader());
//        bundle.putParcelable(MusicContract.KEY_TIMING_OFF, currentTimingOff);
        bundle.putString(MusicContract.KEY_TIMING_OFF, TimingOff.toJson(currentTimingOff));
        mMediaSession.setExtras(bundle);
    }

    private void updateTimingOff() {
        closeCountDownTimer();
        if (currentTimingOff.getMode() == TimingOff.TIMING_OFF_MODE_TIME) {
            if (currentTimingOff.getSecond() != 0) {
                countDownTimer = new CountDownTimer(currentTimingOff.getSecond() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        currentTimingOff.setFinishedSecond((int) (millisUntilFinished / 1000));
                        invalidateMediaSessionExtras();
                    }

                    @Override
                    public void onFinish() {
                        currentTimingOff = TimingOff.defaultTimingOff();
                        invalidateMediaSessionExtras();
                        release();
                    }
                };
                countDownTimer.start();
            }
        }

    }

    private void closeCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void release() {
        mMediaSessionCallback.addQueueItems(null, 0, true);
        mMediaSession.sendSessionEvent(MusicContract.EVENT_CLOSED, null);
        currentTimingOff = TimingOff.defaultTimingOff();
        invalidateMediaSessionExtras();
        closeCountDownTimer();
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
            if (playbackState == FuPlayer.STATE_ENDED) {
                if (currentTimingOff.getMode() == TimingOff.TIMING_OFF_MODE_ONE) {
                    release();
                    return;
                }
                if (mMediaSessionCallback.mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                    mMediaSessionCallback.onPlay();
                } else if (mMediaSessionCallback.mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_NONE
                        && !mMediaSessionCallback.hasNext()) {
                    mMediaSessionCallback.onPause();
                } else {
                    mMediaSessionCallback.onSkipToNext();
                }
            }
            mMediaSessionCallback.setState(mapPlaybackState(mPlayer.getPlaybackState()), mPlayer.getPlayWhenReady());
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            FuLog.d(TAG, "onIsPlayingChanged : isPlaying=" + isPlaying);
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            FuLog.d(TAG, "onPlaybackParametersChanged : playbackParameters=" + playbackParameters);
            invalidateMediaSessionPlaybackState();
        }
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        final List<MediaSessionCompat.QueueItem> mQueueItemList = new ArrayList<>();
        MediaSessionCompat.QueueItem mActiveQueueItem;

        @PlaybackStateCompat.RepeatMode
        int mCurrentRepeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
        @PlaybackStateCompat.ShuffleMode
        int mCurrentShuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE;
        ShuffleOrder mShuffleOrder;
        @PlaybackStateCompat.State
        int mState = PlaybackStateCompat.STATE_NONE;
        boolean mPendingPrepare;
        boolean mPlayWhenReady;

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            FuLog.d(TAG, "onCommand : command=" + command + ",extras=" + extras);
            if (MusicContract.COMMAND_SET_QUEUE_ITEMS.equals(command)) {
                ArrayList<MediaDescriptionCompat> addedList = null;
                if (extras != null) {
                    extras.setClassLoader(getClass().getClassLoader());
                    addedList = extras.getParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS);
                }
                if (mQueueItemList.size() == 0 && (addedList == null || addedList.size() == 0)) {
                    return;
                }
                addQueueItems(addedList, 0, true);
            } else if (MusicContract.COMMAND_ADD_QUEUE_ITEMS.equals(command)) {
                if (extras != null) {
                    extras.setClassLoader(getClass().getClassLoader());
                    ArrayList<MediaDescriptionCompat> addedList = extras.getParcelableArrayList(MusicContract.KEY_QUEUE_ITEMS);
                    int index = extras.getInt(MusicContract.KEY_QUEUE_ITEMS, MusicContract.DEFAULT_QUEUE_ADD_INDEX);
                    addQueueItems(addedList, index, false);
                }
            } else if (MusicContract.COMMAND_CLEAR_QUEUE_ITEMS.equals(command)) {
                addQueueItems(null, 0, true);
                closeCountDownTimer();
            } else if (MusicContract.COMMAND_ADD_TO_TO_FRONT_OF_CURRENT_PLAY.equals(command)) {
                if (extras == null) {
                    return;
                }
                extras.setClassLoader(getClass().getClassLoader());
                MediaDescriptionCompat media = extras.getParcelable(MusicContract.KEY_QUEUE_ITEM);
                if (media == null) {
                    return;
                }
                if (MediaSessionUtil.search(mQueueItemList, media) == -1) {
                    if (mActiveQueueItem == null) {
                        onAddQueueItem(media);
                    } else {
                        int addedIndex = mQueueItemList.indexOf(mActiveQueueItem);
                        onAddQueueItem(media, Math.max(addedIndex, 0));
                    }
                }
            } else if (MusicContract.COMMAND_ADD_AFTER_CURRENT_PLAY.equals(command)) {
                if (extras == null) {
                    return;
                }
                extras.setClassLoader(getClass().getClassLoader());
                MediaDescriptionCompat media = extras.getParcelable(MusicContract.KEY_QUEUE_ITEM);
                if (media == null) {
                    return;
                }
                if (MediaSessionUtil.search(mQueueItemList, media) == -1) {
                    if (mActiveQueueItem == null) {
                        onAddQueueItem(media, mQueueItemList.size());
                    } else {
                        int addedIndex = mQueueItemList.indexOf(mActiveQueueItem) + 1;
                        onAddQueueItem(media, Math.min(addedIndex, mQueueItemList.size()));
                    }

                }
            } else if (MusicContract.COMMAND_SET_TIMING_OFF_MODE.equals(command)) {
                if (extras == null) {
                    return;
                }
//                extras.setClassLoader(TimingOff.class.getClassLoader());
                currentTimingOff = TimingOff.fromJson(extras.getString(MusicContract.KEY_TIMING_OFF));
                invalidateMediaSessionExtras();
                updateTimingOff();
            }
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            onAddQueueItem(description, 0);
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description, int index) {
            if (description == null) {
                return;
            }
            addQueueItems(Collections.singletonList(description), index, false);
        }

        public void addQueueItems(List<MediaDescriptionCompat> list, int index, boolean clear) {
            if (list == null || list.isEmpty()
                    || index < 0 || index > mQueueItemList.size()) {
                if (clear && !mQueueItemList.isEmpty()) {
                    mQueueItemList.clear();
                    updateQueueItemList();
                }
                return;
            }

            long maxId = MediaSessionUtil.maxId(mQueueItemList);
            List<MediaSessionCompat.QueueItem> items = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                int searchIndex = MediaSessionUtil.search(mQueueItemList, list.get(i));
                if (searchIndex < 0) {
                    MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(list.get(i), maxId + i + 1);
                    items.add(item);
                } else {
                    items.add(mQueueItemList.get(searchIndex));
                }
            }
            if (clear) {
                mQueueItemList.clear();
            }
            mQueueItemList.removeAll(items);
            mQueueItemList.addAll(index, items);
            updateQueueItemList();
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            if (description == null) {
                return;
            }
            removeQueueItems(Collections.singletonList(description));
        }

        private void removeQueueItems(List<MediaDescriptionCompat> list) {
            if (mQueueItemList.size() == 0 || list == null || list.size() == 0) {
                return;
            }
            int removeCount = 0;
            for (MediaDescriptionCompat description : list) {
                Iterator<MediaSessionCompat.QueueItem> iterator = mQueueItemList.iterator();
                while (iterator.hasNext()) {
                    MediaSessionCompat.QueueItem item = iterator.next();
                    if (MediaSessionUtil.areItemsTheSame(item.getDescription(), description)) {
                        removeCount++;
                        iterator.remove();
                        break;
                    }
                }
            }
            if (removeCount > 0) {
                updateQueueItemList();
            }
        }

        public void updateQueueItemList() {
            initShuffleOrder();
            mMediaSession.setQueue(mQueueItemList);
            if (mQueueItemList.isEmpty()) {
                mActiveQueueItem = null;
                onStop();
            } else if (!mQueueItemList.contains(mActiveQueueItem)) {
                mActiveQueueItem = mQueueItemList.get(0);
                onPrepare();
            }
            invalidateMediaSessionPlaybackState();
        }

        public void setState(@PlaybackStateCompat.State int state, boolean playWhenReady) {
            if (mState == state && mPlayWhenReady == playWhenReady) {
                mState = state;
                mPlayWhenReady = playWhenReady;
                return;
            }
            mState = state;
            mPlayWhenReady = playWhenReady;

            invalidateMediaSessionMetadata();
            invalidateMediaSessionPlaybackState();
        }

        private void prepare(MediaDescriptionCompat description) {
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    mContext, Util.getUserAgent(mContext, mContext.getApplicationInfo().packageName), null);
            MediaSource mediaSource = ExoMediaSourceUtil
                    .buildMediaSource(mActiveQueueItem.getDescription().getMediaUri(), null, dataSourceFactory, mActiveQueueItem);
            mPlayer.prepare(mediaSource);
        }

        @Override
        public void onPrepare() {
            if (mActiveQueueItem == null) {
                return;
            }
            onStop();
            if (mMediaLoadProvider != null) {
                mPendingPrepare = true;
                setState(PlaybackStateCompat.STATE_BUFFERING, mPlayer.getPlayWhenReady());
                mMediaLoadProvider.onLoadMedia(mActiveQueueItem.getDescription(), new MediaLoadCallback(mActiveQueueItem) {
                    @Override
                    public void onCompleted(MediaDescriptionCompat description) {
                        if (Objects.equals(mActiveQueueItem, item)) {
                            mPendingPrepare = false;
                            MediaSessionCompat.QueueItem newItem = new MediaSessionCompat.QueueItem(description, mActiveQueueItem.getQueueId());
                            mQueueItemList.set(mQueueItemList.indexOf(mActiveQueueItem), newItem);
                            mActiveQueueItem = newItem;
                            invalidateMediaSessionMetadata();
                            mMediaSession.setQueue(mQueueItemList);
                            prepare(description);
                        }
                    }

                    @Override
                    public void onFailure() {
                        if (Objects.equals(mActiveQueueItem, item)) {
                            mPendingPrepare = false;
                            setState(PlaybackStateCompat.STATE_ERROR, mPlayer.getPlayWhenReady());
                        }
                    }
                });
                return;
            }
            prepare(mActiveQueueItem.getDescription());
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            int index = MediaSessionUtil.search(mQueueItemList, new MediaDescriptionCompat.Builder().setMediaId(mediaId).build());
            if (index >= 0) {
                mActiveQueueItem = mQueueItemList.get(index);
                onPrepare();
            }
        }

        @Override
        public void onPlay() {
            switch (mState) {
                case PlaybackStateCompat.STATE_NONE:
                    onPrepare();
                    break;
                case PlaybackStateCompat.STATE_ERROR:
                    mPlayer.retry();
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mPlayer.seekTo(0);
                    break;
            }
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            int index = MediaSessionUtil.search(mQueueItemList, new MediaDescriptionCompat.Builder().setMediaId(mediaId).build());
            if (index < 0) {
                return;
            }
            if (mActiveQueueItem == mQueueItemList.get(index)) {
                onPlay();
            } else {
                mActiveQueueItem = mQueueItemList.get(index);
                onPrepare();
                mPlayer.setPlayWhenReady(true);
            }
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        boolean hasNext() {
            if (mQueueItemList.size() == 0) {
                return false;
            }
            if (mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_ONE ||
                    mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_ALL ||
                    mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_GROUP) {
                return true;
            }
            int currentIndex = mQueueItemList.indexOf(mActiveQueueItem);
            return mShuffleOrder.getNextIndex(currentIndex) != C.INDEX_UNSET;
        }

        @Override
        public void onSkipToNext() {
            if (hasNext()) {
                int currentIndex = mQueueItemList.indexOf(mActiveQueueItem);
                int newIndex = mShuffleOrder.getNextIndex(currentIndex);
                newIndex = newIndex != C.INDEX_UNSET ? newIndex : mShuffleOrder.getFirstIndex();
                mActiveQueueItem = mQueueItemList.get(newIndex);

                setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, mPlayer.getPlayWhenReady());
                onPrepare();
            }
        }

        @Override
        public void onSkipToQueueItem(long id) {
            int index = MediaSessionUtil.getIndexById(mQueueItemList, id);
            if (index >= 0) {
                mActiveQueueItem = mQueueItemList.get(index);

                setState(PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM, mPlayer.getPlayWhenReady());
                onPrepare();
            }
        }

        boolean hasPrevious() {
            if (mQueueItemList.size() == 0) {
                return false;
            }
            if (mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_ONE ||
                    mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_ALL ||
                    mCurrentRepeatMode == PlaybackStateCompat.REPEAT_MODE_GROUP) {
                return true;
            }

            int currentIndex = mQueueItemList.indexOf(mActiveQueueItem);
            return mShuffleOrder.getPreviousIndex(currentIndex) != C.INDEX_UNSET;
        }

        @Override
        public void onSkipToPrevious() {
            if (hasPrevious()) {
                int currentIndex = mQueueItemList.indexOf(mActiveQueueItem);
                int newIndex = mShuffleOrder.getPreviousIndex(currentIndex);
                newIndex = newIndex != C.INDEX_UNSET ? newIndex : mShuffleOrder.getLastIndex();
                mActiveQueueItem = mQueueItemList.get(newIndex);

                setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, mPlayer.getPlayWhenReady());

                onPrepare();
            }
        }

        @Override
        public void onRewind() {
            if (mPlayer.isCurrentWindowSeekable() && rewindMs > 0) {
                mPlayer.seekTo(mPlayer.getCurrentPosition() - rewindMs);
            }
        }

        @Override
        public void onFastForward() {
            if (mPlayer.isCurrentWindowSeekable() && fastForwardMs > 0) {
                mPlayer.seekTo(mPlayer.getCurrentPosition() + fastForwardMs);
            }
        }

        @Override
        public void onSeekTo(long pos) {
            if (mPlayer.isCurrentWindowSeekable()) {
                mPlayer.seekTo(pos);
            }
        }

        @Override
        public void onStop() {
            setState(PlaybackStateCompat.STATE_NONE, mPlayer.getPlayWhenReady());
            mPlayer.stop(true);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            if (mCurrentRepeatMode == repeatMode) {
                return;
            }
            mCurrentRepeatMode = repeatMode;
            invalidateMediaSessionPlaybackState();
        }

        public void initShuffleOrder() {
            if (mCurrentShuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ||
                    mCurrentShuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP) {
                mShuffleOrder = new ShuffleOrder.DefaultShuffleOrder(mQueueItemList.size());
            } else {
                mShuffleOrder = new ShuffleOrder.UnshuffledShuffleOrder(mQueueItemList.size());
            }
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            if (mCurrentShuffleMode == shuffleMode) {
                return;
            }
            mCurrentShuffleMode = shuffleMode;
            initShuffleOrder();
            invalidateMediaSessionPlaybackState();
        }

    }

    public class MediaLoadCallback {
        public final MediaSessionCompat.QueueItem item;

        private MediaLoadCallback(MediaSessionCompat.QueueItem item) {
            this.item = item;
        }


        public void onCompleted(final MediaDescriptionCompat description) {

        }

        public void onFailure() {

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
