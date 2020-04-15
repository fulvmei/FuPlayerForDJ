package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MediaSessionConnector {

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

    @PlaybackActions
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


    @PlaybackActions
    public static final long DEFAULT_PLAYBACK_ACTIONS = ALL_PLAYBACK_ACTIONS;

    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;
    public static final String EXTRAS_SPEED = "EXO_SPEED";
    public static final String EXTRAS_PITCH = "EXO_PITCH";

    private static final long BASE_PLAYBACK_ACTIONS =
            PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                    | PlaybackStateCompat.ACTION_SET_REPEAT_MODE;
    private static final int BASE_MEDIA_SESSION_FLAGS =
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
    private static final int EDITOR_MEDIA_SESSION_FLAGS =
            BASE_MEDIA_SESSION_FLAGS | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS;

    private static final MediaMetadataCompat METADATA_EMPTY =
            new MediaMetadataCompat.Builder().build();

    public interface CommandReceiver {
        boolean onCommand(
                Player player,
                ControlDispatcher controlDispatcher,
                String command,
                @Nullable Bundle extras,
                @Nullable ResultReceiver cb);
    }

    public interface PlaybackPreparer extends CommandReceiver {

        long ACTIONS =
                PlaybackStateCompat.ACTION_PREPARE
                        | PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID
                        | PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH
                        | PlaybackStateCompat.ACTION_PREPARE_FROM_URI
                        | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                        | PlaybackStateCompat.ACTION_PLAY_FROM_URI;

        long getSupportedPrepareActions();

        void onPrepare(boolean playWhenReady);

        void onPrepareFromMediaId(String mediaId, boolean playWhenReady, Bundle extras);

        void onPrepareFromSearch(String query, boolean playWhenReady, Bundle extras);

        void onPrepareFromUri(Uri uri, boolean playWhenReady, Bundle extras);
    }

    public interface QueueNavigator extends CommandReceiver {

        long ACTIONS =
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

        /**
         * Returns the actions which are supported by the navigator. The supported actions must be a
         * bitmask combined out of {@link PlaybackStateCompat#ACTION_SKIP_TO_QUEUE_ITEM}, {@link
         * PlaybackStateCompat#ACTION_SKIP_TO_NEXT}, {@link
         * PlaybackStateCompat#ACTION_SKIP_TO_PREVIOUS}.
         *
         * @param player The player connected to the media session.
         * @return The bitmask of the supported media actions.
         */
        long getSupportedQueueNavigatorActions(Player player);

        /**
         * Called when the timeline of the player has changed.
         *
         * @param player The player connected to the media session.
         */
        void onTimelineChanged(Player player);

        /**
         * Called when the current window index changed.
         *
         * @param player The player connected to the media session.
         */
        void onCurrentWindowIndexChanged(Player player);

        /**
         * Gets the id of the currently active queue item, or {@link
         * MediaSessionCompat.QueueItem#UNKNOWN_ID} if the active item is unknown.
         *
         * <p>To let the connector publish metadata for the active queue item, the queue item with the
         * returned id must be available in the list of items returned by {@link
         * MediaControllerCompat#getQueue()}.
         *
         * @param player The player connected to the media session.
         * @return The id of the active queue item.
         */
        long getActiveQueueItemId(@Nullable Player player);

        /**
         * See {@link MediaSessionCompat.Callback#onSkipToPrevious()}.
         *
         * @param player            The player connected to the media session.
         * @param controlDispatcher A {@link ControlDispatcher} that should be used for dispatching
         *                          changes to the player.
         */
        void onSkipToPrevious(Player player, ControlDispatcher controlDispatcher);

        /**
         * See {@link MediaSessionCompat.Callback#onSkipToQueueItem(long)}.
         *
         * @param player            The player connected to the media session.
         * @param controlDispatcher A {@link ControlDispatcher} that should be used for dispatching
         *                          changes to the player.
         */
        void onSkipToQueueItem(Player player, ControlDispatcher controlDispatcher, long id);

        /**
         * See {@link MediaSessionCompat.Callback#onSkipToNext()}.
         *
         * @param player            The player connected to the media session.
         * @param controlDispatcher A {@link ControlDispatcher} that should be used for dispatching
         *                          changes to the player.
         */
        void onSkipToNext(Player player, ControlDispatcher controlDispatcher);
    }

    public interface QueueEditor extends CommandReceiver {

        /**
         * See {@link MediaSessionCompat.Callback#onAddQueueItem(MediaDescriptionCompat description)}.
         */
        void onAddQueueItem(Player player, MediaDescriptionCompat description);

        /**
         * See {@link MediaSessionCompat.Callback#onAddQueueItem(MediaDescriptionCompat description, int
         * index)}.
         */
        void onAddQueueItem(Player player, MediaDescriptionCompat description, int index);

        /**
         * See {@link MediaSessionCompat.Callback#onRemoveQueueItem(MediaDescriptionCompat
         * description)}.
         */
        void onRemoveQueueItem(Player player, MediaDescriptionCompat description);
    }

    public interface RatingCallback extends CommandReceiver {

        /**
         * See {@link MediaSessionCompat.Callback#onSetRating(RatingCompat)}.
         */
        void onSetRating(Player player, RatingCompat rating);

        /**
         * See {@link MediaSessionCompat.Callback#onSetRating(RatingCompat, Bundle)}.
         */
        void onSetRating(Player player, RatingCompat rating, Bundle extras);
    }

    public interface CaptionCallback extends CommandReceiver {

        /**
         * See {@link MediaSessionCompat.Callback#onSetCaptioningEnabled(boolean)}.
         */
        void onSetCaptioningEnabled(Player player, boolean enabled);

        /**
         * Returns whether the media currently being played has captions.
         * <p>
         * This method is called each time the media session playback state needs to be updated and
         * published upon a player state change.
         */
        boolean hasCaptions(Player player);
    }

    public interface MediaButtonEventHandler {
        /**
         * See {@link MediaSessionCompat.Callback#onMediaButtonEvent(Intent)}.
         *
         * @param player            The {@link Player}.
         * @param controlDispatcher A {@link ControlDispatcher} that should be used for dispatching
         *                          changes to the player.
         * @param mediaButtonEvent  The {@link Intent}.
         * @return True if the event was handled, false otherwise.
         */
        boolean onMediaButtonEvent(
                Player player, ControlDispatcher controlDispatcher, Intent mediaButtonEvent);
    }

    public interface CustomActionProvider {
        /**
         * Called when a custom action provided by this provider is sent to the media session.
         *
         * @param player            The player connected to the media session.
         * @param controlDispatcher A {@link ControlDispatcher} that should be used for dispatching
         *                          changes to the player.
         * @param action            The name of the action which was sent by a media controller.
         * @param extras            Optional extras sent by a media controller.
         */
        void onCustomAction(
                Player player, ControlDispatcher controlDispatcher, String action, @Nullable Bundle extras);

        /**
         * Returns a {@link PlaybackStateCompat.CustomAction} which will be published to the media
         * session by the connector or {@code null} if this action should not be published at the given
         * player state.
         *
         * @param player The player connected to the media session.
         * @return The custom action to be included in the session playback state or {@code null}.
         */
        @Nullable
        PlaybackStateCompat.CustomAction getCustomAction(Player player);
    }

    public interface MediaMetadataProvider {
        /**
         * Gets the {@link MediaMetadataCompat} to be published to the session.
         *
         * <p>An app may need to load metadata resources like artwork bitmaps asynchronously. In such a
         * case the app should return a {@link MediaMetadataCompat} object that does not contain these
         * resources as a placeholder. The app should start an asynchronous operation to download the
         * bitmap and put it into a cache. Finally, the app should call {@link
         * #invalidateMediaSessionMetadata()}. This causes this callback to be called again and the app
         * can now return a {@link MediaMetadataCompat} object with all the resources included.
         *
         * @param player The player connected to the media session.
         * @return The {@link MediaMetadataCompat} to be published to the session.
         */
        MediaMetadataCompat getMetadata(Player player);
    }

    public final MediaSessionCompat mediaSession;

    private final Looper looper;
    private final ComponentListener componentListener;
    private final ArrayList<CommandReceiver> commandReceivers;
    private final ArrayList<CommandReceiver> customCommandReceivers;

    private ControlDispatcher controlDispatcher;
    private CustomActionProvider[] customActionProviders;
    private Map<String, CustomActionProvider> customActionMap;
    @Nullable
    private MediaMetadataProvider mediaMetadataProvider;
    @Nullable
    private Player player;
    @Nullable
    private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    @Nullable
    private Pair<Integer, CharSequence> customError;
    @Nullable
    private Bundle customErrorExtras;
    @Nullable
    private PlaybackPreparer playbackPreparer;
    @Nullable
    private QueueNavigator queueNavigator;
    @Nullable
    private QueueEditor queueEditor;
    @Nullable
    private RatingCallback ratingCallback;
    @Nullable
    private CaptionCallback captionCallback;
    @Nullable
    private MediaButtonEventHandler mediaButtonEventHandler;

    private long enabledPlaybackActions;
    private int rewindMs;
    private int fastForwardMs;

    public MediaSessionConnector(MediaSessionCompat mediaSession) {
        this.mediaSession = mediaSession;
        looper = Util.getLooper();
        componentListener = new ComponentListener();
        commandReceivers = new ArrayList<>();
        customCommandReceivers = new ArrayList<>();
        controlDispatcher = new DefaultControlDispatcher();
        customActionProviders = new CustomActionProvider[0];
        customActionMap = Collections.emptyMap();
        mediaMetadataProvider =
                new DefaultMediaMetadataProvider(
                        mediaSession.getController(), /* metadataExtrasPrefix= */ null);
        enabledPlaybackActions = DEFAULT_PLAYBACK_ACTIONS;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        mediaSession.setFlags(BASE_MEDIA_SESSION_FLAGS);
        mediaSession.setCallback(componentListener, new Handler(looper));
    }

    public void setPlayer(@Nullable Player player) {
        Assertions.checkArgument(player == null || player.getApplicationLooper() == looper);
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        invalidateMediaSessionPlaybackState();
        invalidateMediaSessionMetadata();
    }

    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        if (this.playbackPreparer != playbackPreparer) {
            unregisterCommandReceiver(this.playbackPreparer);
            this.playbackPreparer = playbackPreparer;
            registerCommandReceiver(playbackPreparer);
            invalidateMediaSessionPlaybackState();
        }
    }

    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        if (this.controlDispatcher != controlDispatcher) {
            this.controlDispatcher =
                    controlDispatcher == null ? new DefaultControlDispatcher() : controlDispatcher;
        }
    }

    public void setMediaButtonEventHandler(
            @Nullable MediaButtonEventHandler mediaButtonEventHandler) {
        this.mediaButtonEventHandler = mediaButtonEventHandler;
    }

    public void setEnabledPlaybackActions(@PlaybackActions long enabledPlaybackActions) {
        enabledPlaybackActions &= ALL_PLAYBACK_ACTIONS;
        if (this.enabledPlaybackActions != enabledPlaybackActions) {
            this.enabledPlaybackActions = enabledPlaybackActions;
            invalidateMediaSessionPlaybackState();
        }
    }

    public void setRewindIncrementMs(int rewindMs) {
        if (this.rewindMs != rewindMs) {
            this.rewindMs = rewindMs;
            invalidateMediaSessionPlaybackState();
        }
    }

    public void setFastForwardIncrementMs(int fastForwardMs) {
        if (this.fastForwardMs != fastForwardMs) {
            this.fastForwardMs = fastForwardMs;
            invalidateMediaSessionPlaybackState();
        }
    }

    public void setErrorMessageProvider(
            @Nullable ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider) {
        if (this.errorMessageProvider != errorMessageProvider) {
            this.errorMessageProvider = errorMessageProvider;
            invalidateMediaSessionPlaybackState();
        }
    }

    public void setQueueNavigator(@Nullable QueueNavigator queueNavigator) {
        if (this.queueNavigator != queueNavigator) {
            unregisterCommandReceiver(this.queueNavigator);
            this.queueNavigator = queueNavigator;
            registerCommandReceiver(queueNavigator);
        }
    }

    public void setQueueEditor(@Nullable QueueEditor queueEditor) {
        if (this.queueEditor != queueEditor) {
            unregisterCommandReceiver(this.queueEditor);
            this.queueEditor = queueEditor;
            registerCommandReceiver(queueEditor);
            mediaSession.setFlags(
                    queueEditor == null ? BASE_MEDIA_SESSION_FLAGS : EDITOR_MEDIA_SESSION_FLAGS);
        }
    }

    public void setRatingCallback(@Nullable RatingCallback ratingCallback) {
        if (this.ratingCallback != ratingCallback) {
            unregisterCommandReceiver(this.ratingCallback);
            this.ratingCallback = ratingCallback;
            registerCommandReceiver(this.ratingCallback);
        }
    }

    public void setCaptionCallback(@Nullable CaptionCallback captionCallback) {
        if (this.captionCallback != captionCallback) {
            unregisterCommandReceiver(this.captionCallback);
            this.captionCallback = captionCallback;
            registerCommandReceiver(this.captionCallback);
        }
    }

    public void setCustomErrorMessage(@Nullable CharSequence message) {
        int code = (message == null) ? 0 : PlaybackStateCompat.ERROR_CODE_APP_ERROR;
        setCustomErrorMessage(message, code);
    }

    public void setCustomErrorMessage(@Nullable CharSequence message, int code) {
        setCustomErrorMessage(message, code, /* extras= */ null);
    }

    public void setCustomErrorMessage(
            @Nullable CharSequence message, int code, @Nullable Bundle extras) {
        customError = (message == null) ? null : new Pair<>(code, message);
        customErrorExtras = (message == null) ? null : extras;
        invalidateMediaSessionPlaybackState();
    }

    public void setCustomActionProviders(@Nullable CustomActionProvider... customActionProviders) {
        this.customActionProviders =
                customActionProviders == null ? new CustomActionProvider[0] : customActionProviders;
        invalidateMediaSessionPlaybackState();
    }

    public void setMediaMetadataProvider(@Nullable MediaMetadataProvider mediaMetadataProvider) {
        if (this.mediaMetadataProvider != mediaMetadataProvider) {
            this.mediaMetadataProvider = mediaMetadataProvider;
            invalidateMediaSessionMetadata();
        }
    }

    public final void invalidateMediaSessionMetadata() {
        MediaMetadataCompat metadata =
                mediaMetadataProvider != null && player != null
                        ? mediaMetadataProvider.getMetadata(player)
                        : METADATA_EMPTY;
        mediaSession.setMetadata(metadata);
    }

    public final void invalidateMediaSessionPlaybackState() {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        @Nullable Player player = this.player;
        if (player == null) {
            builder
                    .setActions(buildPrepareActions())
                    .setState(
                            PlaybackStateCompat.STATE_NONE,
                            /* position= */ 0,
                            /* playbackSpeed= */ 0,
                            /* updateTime= */ SystemClock.elapsedRealtime());

            mediaSession.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
            mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
            mediaSession.setPlaybackState(builder.build());
            return;
        }

        Map<String, CustomActionProvider> currentActions = new HashMap<>();
        for (CustomActionProvider customActionProvider : customActionProviders) {
            @Nullable
            PlaybackStateCompat.CustomAction customAction = customActionProvider.getCustomAction(player);
            if (customAction != null) {
                currentActions.put(customAction.getAction(), customActionProvider);
                builder.addCustomAction(customAction);
            }
        }
        customActionMap = Collections.unmodifiableMap(currentActions);

        Bundle extras = new Bundle();
        @Nullable ExoPlaybackException playbackError = player.getPlaybackError();
        boolean reportError = playbackError != null || customError != null;
        int sessionPlaybackState =
                reportError
                        ? PlaybackStateCompat.STATE_ERROR
                        : getMediaSessionPlaybackState(player.getPlaybackState(), player.getPlayWhenReady());
        if (customError != null) {
            builder.setErrorMessage(customError.first, customError.second);
            if (customErrorExtras != null) {
                extras.putAll(customErrorExtras);
            }
        } else if (playbackError != null && errorMessageProvider != null) {
            Pair<Integer, String> message = errorMessageProvider.getErrorMessage(playbackError);
            builder.setErrorMessage(message.first, message.second);
        }
        long activeQueueItemId =
                queueNavigator != null
                        ? queueNavigator.getActiveQueueItemId(player)
                        : MediaSessionCompat.QueueItem.UNKNOWN_ID;
        PlaybackParameters playbackParameters = player.getPlaybackParameters();
        extras.putFloat(EXTRAS_SPEED, playbackParameters.speed);
        extras.putFloat(EXTRAS_PITCH, playbackParameters.pitch);
        float sessionPlaybackSpeed = player.isPlaying() ? playbackParameters.speed : 0f;
        builder
                .setActions(buildPrepareActions() | buildPlaybackActions(player))
                .setActiveQueueItemId(activeQueueItemId)
                .setBufferedPosition(player.getBufferedPosition())
                .setState(
                        sessionPlaybackState,
                        player.getCurrentPosition(),
                        sessionPlaybackSpeed,
                        /* updateTime= */ SystemClock.elapsedRealtime())
                .setExtras(extras);

        @Player.RepeatMode int repeatMode = player.getRepeatMode();
        mediaSession.setRepeatMode(
                repeatMode == Player.REPEAT_MODE_ONE
                        ? PlaybackStateCompat.REPEAT_MODE_ONE
                        : repeatMode == Player.REPEAT_MODE_ALL
                        ? PlaybackStateCompat.REPEAT_MODE_ALL
                        : PlaybackStateCompat.REPEAT_MODE_NONE);
        mediaSession.setShuffleMode(
                player.getShuffleModeEnabled()
                        ? PlaybackStateCompat.SHUFFLE_MODE_ALL
                        : PlaybackStateCompat.SHUFFLE_MODE_NONE);
        mediaSession.setPlaybackState(builder.build());
    }

    public final void invalidateMediaSessionQueue() {
        if (queueNavigator != null && player != null) {
            queueNavigator.onTimelineChanged(player);
        }
    }

    public void registerCustomCommandReceiver(@Nullable CommandReceiver commandReceiver) {
        if (commandReceiver != null && !customCommandReceivers.contains(commandReceiver)) {
            customCommandReceivers.add(commandReceiver);
        }
    }

    public void unregisterCustomCommandReceiver(@Nullable CommandReceiver commandReceiver) {
        if (commandReceiver != null) {
            customCommandReceivers.remove(commandReceiver);
        }
    }

    private void registerCommandReceiver(@Nullable CommandReceiver commandReceiver) {
        if (commandReceiver != null && !commandReceivers.contains(commandReceiver)) {
            commandReceivers.add(commandReceiver);
        }
    }

    private void unregisterCommandReceiver(@Nullable CommandReceiver commandReceiver) {
        if (commandReceiver != null) {
            commandReceivers.remove(commandReceiver);
        }
    }

    private long buildPrepareActions() {
        return playbackPreparer == null
                ? 0
                : (PlaybackPreparer.ACTIONS & playbackPreparer.getSupportedPrepareActions());
    }

    private long buildPlaybackActions(Player player) {
        boolean enableSeeking = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableSetRating = false;
        boolean enableSetCaptioningEnabled = false;
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty() && !player.isPlayingAd()) {
            enableSeeking = player.isCurrentWindowSeekable();
            enableRewind = enableSeeking && rewindMs > 0;
            enableFastForward = enableSeeking && fastForwardMs > 0;
            enableSetRating = ratingCallback != null;
            enableSetCaptioningEnabled = captionCallback != null && captionCallback.hasCaptions(player);
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

        long actions = playbackActions;
        if (queueNavigator != null) {
            actions |=
                    (QueueNavigator.ACTIONS & queueNavigator.getSupportedQueueNavigatorActions(player));
        }
        if (enableSetRating) {
            actions |= PlaybackStateCompat.ACTION_SET_RATING;
        }
        if (enableSetCaptioningEnabled) {
            actions |= PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;
        }
        return actions;
    }


    private boolean canDispatchPlaybackAction(long action) {
        return player != null && (enabledPlaybackActions & action) != 0;
    }

    private boolean canDispatchToPlaybackPreparer(long action) {
        return playbackPreparer != null
                && (playbackPreparer.getSupportedPrepareActions() & action) != 0;
    }

    private boolean canDispatchToQueueNavigator(long action) {
        return player != null
                && queueNavigator != null
                && (queueNavigator.getSupportedQueueNavigatorActions(player) & action) != 0;
    }

    private boolean canDispatchSetRating() {
        return player != null && ratingCallback != null;
    }

    private boolean canDispatchSetCaptioningEnabled() {
        return player != null && captionCallback != null;
    }

    private boolean canDispatchQueueEdit() {
        return player != null && queueEditor != null;
    }

    private boolean canDispatchMediaButtonEvent() {
        return player != null && mediaButtonEventHandler != null;
    }

    private void rewind(Player player) {
        if (player.isCurrentWindowSeekable() && rewindMs > 0) {
            seekToOffset(player, /* offsetMs= */ -rewindMs);
        }
    }

    private void fastForward(Player player) {
        if (player.isCurrentWindowSeekable() && fastForwardMs > 0) {
            seekToOffset(player, /* offsetMs= */ fastForwardMs);
        }
    }

    private void seekToOffset(Player player, long offsetMs) {
        long positionMs = player.getCurrentPosition() + offsetMs;
        long durationMs = player.getDuration();
        if (durationMs != C.TIME_UNSET) {
            positionMs = Math.min(positionMs, durationMs);
        }
        positionMs = Math.max(positionMs, 0);
        seekTo(player, player.getCurrentWindowIndex(), positionMs);
    }

    private void seekTo(Player player, int windowIndex, long positionMs) {
        controlDispatcher.dispatchSeekTo(player, windowIndex, positionMs);
    }

    private static int getMediaSessionPlaybackState(
            @Player.State int exoPlayerPlaybackState, boolean playWhenReady) {
        switch (exoPlayerPlaybackState) {
            case Player.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case Player.STATE_READY:
                return playWhenReady ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_ENDED:
                return PlaybackStateCompat.STATE_STOPPED;
            case Player.STATE_IDLE:
            default:
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    public static final class DefaultMediaMetadataProvider implements MediaMetadataProvider {

        private final MediaControllerCompat mediaController;
        private final String metadataExtrasPrefix;

        public DefaultMediaMetadataProvider(
                MediaControllerCompat mediaController, @Nullable String metadataExtrasPrefix) {
            this.mediaController = mediaController;
            this.metadataExtrasPrefix = metadataExtrasPrefix != null ? metadataExtrasPrefix : "";
        }

        @Override
        public MediaMetadataCompat getMetadata(Player player) {
            if (player.getCurrentTimeline().isEmpty()) {
                return METADATA_EMPTY;
            }
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
            if (player.isPlayingAd()) {
                builder.putLong(MediaMetadataCompat.METADATA_KEY_ADVERTISEMENT, 1);
            }
            builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    player.isCurrentWindowDynamic() || player.getDuration() == C.TIME_UNSET
                            ? -1
                            : player.getDuration());
            long activeQueueItemId = mediaController.getPlaybackState().getActiveQueueItemId();
            if (activeQueueItemId != MediaSessionCompat.QueueItem.UNKNOWN_ID) {
                List<MediaSessionCompat.QueueItem> queue = mediaController.getQueue();
                for (int i = 0; queue != null && i < queue.size(); i++) {
                    MediaSessionCompat.QueueItem queueItem = queue.get(i);
                    if (queueItem.getQueueId() == activeQueueItemId) {
                        MediaDescriptionCompat description = queueItem.getDescription();
                        @Nullable Bundle extras = description.getExtras();
                        if (extras != null) {
                            for (String key : extras.keySet()) {
                                @Nullable Object value = extras.get(key);
                                if (value instanceof String) {
                                    builder.putString(metadataExtrasPrefix + key, (String) value);
                                } else if (value instanceof CharSequence) {
                                    builder.putText(metadataExtrasPrefix + key, (CharSequence) value);
                                } else if (value instanceof Long) {
                                    builder.putLong(metadataExtrasPrefix + key, (Long) value);
                                } else if (value instanceof Integer) {
                                    builder.putLong(metadataExtrasPrefix + key, (Integer) value);
                                } else if (value instanceof Bitmap) {
                                    builder.putBitmap(metadataExtrasPrefix + key, (Bitmap) value);
                                } else if (value instanceof RatingCompat) {
                                    builder.putRating(metadataExtrasPrefix + key, (RatingCompat) value);
                                }
                            }
                        }
                        @Nullable CharSequence title = description.getTitle();
                        if (title != null) {
                            String titleString = String.valueOf(title);
                            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, titleString);
                            builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, titleString);
                        }
                        @Nullable CharSequence subtitle = description.getSubtitle();
                        if (subtitle != null) {
                            builder.putString(
                                    MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, String.valueOf(subtitle));
                        }
                        @Nullable CharSequence displayDescription = description.getDescription();
                        if (displayDescription != null) {
                            builder.putString(
                                    MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                                    String.valueOf(displayDescription));
                        }
                        @Nullable Bitmap iconBitmap = description.getIconBitmap();
                        if (iconBitmap != null) {
                            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, iconBitmap);
                        }
                        @Nullable Uri iconUri = description.getIconUri();
                        if (iconUri != null) {
                            builder.putString(
                                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, String.valueOf(iconUri));
                        }
                        @Nullable String mediaId = description.getMediaId();
                        if (mediaId != null) {
                            builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId);
                        }
                        @Nullable Uri mediaUri = description.getMediaUri();
                        if (mediaUri != null) {
                            builder.putString(
                                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI, String.valueOf(mediaUri));
                        }
                        break;
                    }
                }
            }
            return builder.build();
        }
    }

    private class ComponentListener extends MediaSessionCompat.Callback
            implements Player.EventListener {

        private int currentWindowIndex;
        private int currentWindowCount;

        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            int windowCount = player.getCurrentTimeline().getWindowCount();
            int windowIndex = player.getCurrentWindowIndex();
            if (queueNavigator != null) {
                queueNavigator.onTimelineChanged(player);
                invalidateMediaSessionPlaybackState();
            } else if (currentWindowCount != windowCount || currentWindowIndex != windowIndex) {
                // active queue item and queue navigation actions may need to be updated
                invalidateMediaSessionPlaybackState();
            }
            currentWindowCount = windowCount;
            currentWindowIndex = windowIndex;
            invalidateMediaSessionMetadata();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onRepeatModeChanged(@Player.RepeatMode int repeatMode) {
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            invalidateMediaSessionPlaybackState();
            invalidateMediaSessionQueue();
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (currentWindowIndex != player.getCurrentWindowIndex()) {
                if (queueNavigator != null) {
                    queueNavigator.onCurrentWindowIndexChanged(player);
                }
                currentWindowIndex = player.getCurrentWindowIndex();
                // Update playback state after queueNavigator.onCurrentWindowIndexChanged has been called
                // and before updating metadata.
                invalidateMediaSessionPlaybackState();
                invalidateMediaSessionMetadata();
                return;
            }
            invalidateMediaSessionPlaybackState();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            invalidateMediaSessionPlaybackState();
        }

        // MediaSessionCompat.Callback implementation.

        @Override
        public void onPlay() {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_PLAY)) {
                if (player.getPlaybackState() == Player.STATE_IDLE) {
                    if (playbackPreparer != null) {
                        playbackPreparer.onPrepare(/* playWhenReady= */ true);
                    }
                } else if (player.getPlaybackState() == Player.STATE_ENDED) {
                    seekTo(player, player.getCurrentWindowIndex(), C.TIME_UNSET);
                }
                controlDispatcher.dispatchSetPlayWhenReady(
                        Assertions.checkNotNull(player), /* playWhenReady= */ true);
            }
        }

        @Override
        public void onPause() {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_PAUSE)) {
                controlDispatcher.dispatchSetPlayWhenReady(player, /* playWhenReady= */ false);
            }
        }

        @Override
        public void onSeekTo(long positionMs) {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_SEEK_TO)) {
                seekTo(player, player.getCurrentWindowIndex(), positionMs);
            }
        }

        @Override
        public void onFastForward() {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_FAST_FORWARD)) {
                fastForward(player);
            }
        }

        @Override
        public void onRewind() {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_REWIND)) {
                rewind(player);
            }
        }

        @Override
        public void onStop() {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_STOP)) {
                controlDispatcher.dispatchStop(player, /* reset= */ true);
            }
        }

        @Override
        public void onSetShuffleMode(@PlaybackStateCompat.ShuffleMode int shuffleMode) {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE)) {
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
                controlDispatcher.dispatchSetShuffleModeEnabled(player, shuffleModeEnabled);
            }
        }

        @Override
        public void onSetRepeatMode(@PlaybackStateCompat.RepeatMode int mediaSessionRepeatMode) {
            if (canDispatchPlaybackAction(PlaybackStateCompat.ACTION_SET_REPEAT_MODE)) {
                @RepeatModeUtil.RepeatToggleModes int repeatMode;
                switch (mediaSessionRepeatMode) {
                    case PlaybackStateCompat.REPEAT_MODE_ALL:
                    case PlaybackStateCompat.REPEAT_MODE_GROUP:
                        repeatMode = Player.REPEAT_MODE_ALL;
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_ONE:
                        repeatMode = Player.REPEAT_MODE_ONE;
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_NONE:
                    case PlaybackStateCompat.REPEAT_MODE_INVALID:
                    default:
                        repeatMode = Player.REPEAT_MODE_OFF;
                        break;
                }
                controlDispatcher.dispatchSetRepeatMode(player, repeatMode);
            }
        }

        @Override
        public void onSkipToNext() {
            if (canDispatchToQueueNavigator(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)) {
                queueNavigator.onSkipToNext(player, controlDispatcher);
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (canDispatchToQueueNavigator(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)) {
                queueNavigator.onSkipToPrevious(player, controlDispatcher);
            }
        }

        @Override
        public void onSkipToQueueItem(long id) {
            if (canDispatchToQueueNavigator(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM)) {
                queueNavigator.onSkipToQueueItem(player, controlDispatcher, id);
            }
        }

        @Override
        public void onCustomAction(String action, @Nullable Bundle extras) {
            if (player != null && customActionMap.containsKey(action)) {
                customActionMap.get(action).onCustomAction(player, controlDispatcher, action, extras);
                invalidateMediaSessionPlaybackState();
            }
        }

        @Override
        public void onCommand(String command, @Nullable Bundle extras, @Nullable ResultReceiver cb) {
            if (player != null) {
                for (int i = 0; i < commandReceivers.size(); i++) {
                    if (commandReceivers.get(i).onCommand(player, controlDispatcher, command, extras, cb)) {
                        return;
                    }
                }
                for (int i = 0; i < customCommandReceivers.size(); i++) {
                    if (customCommandReceivers
                            .get(i)
                            .onCommand(player, controlDispatcher, command, extras, cb)) {
                        return;
                    }
                }
            }
        }

        @Override
        public void onPrepare() {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PREPARE)) {
                playbackPreparer.onPrepare(/* playWhenReady= */ false);
            }
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID)) {
                playbackPreparer.onPrepareFromMediaId(mediaId, /* playWhenReady= */ false, extras);
            }
        }

        @Override
        public void onPrepareFromSearch(String query, Bundle extras) {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH)) {
                playbackPreparer.onPrepareFromSearch(query, /* playWhenReady= */ false, extras);
            }
        }

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PREPARE_FROM_URI)) {
                playbackPreparer.onPrepareFromUri(uri, /* playWhenReady= */ false, extras);
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)) {
                playbackPreparer.onPrepareFromMediaId(mediaId, /* playWhenReady= */ true, extras);
            }
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH)) {
                playbackPreparer.onPrepareFromSearch(query, /* playWhenReady= */ true, extras);
            }
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            if (canDispatchToPlaybackPreparer(PlaybackStateCompat.ACTION_PLAY_FROM_URI)) {
                playbackPreparer.onPrepareFromUri(uri, /* playWhenReady= */ true, extras);
            }
        }

        @Override
        public void onSetRating(RatingCompat rating) {
            if (canDispatchSetRating()) {
                ratingCallback.onSetRating(player, rating);
            }
        }

        @Override
        public void onSetRating(RatingCompat rating, Bundle extras) {
            if (canDispatchSetRating()) {
                ratingCallback.onSetRating(player, rating, extras);
            }
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            if (canDispatchQueueEdit()) {
                queueEditor.onAddQueueItem(player, description);
            }
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description, int index) {
            if (canDispatchQueueEdit()) {
                queueEditor.onAddQueueItem(player, description, index);
            }
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            if (canDispatchQueueEdit()) {
                queueEditor.onRemoveQueueItem(player, description);
            }
        }

        @Override
        public void onSetCaptioningEnabled(boolean enabled) {
            if (canDispatchSetCaptioningEnabled()) {
                captionCallback.onSetCaptioningEnabled(player, enabled);
            }
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            boolean isHandled =
                    canDispatchMediaButtonEvent()
                            && mediaButtonEventHandler.onMediaButtonEvent(
                            player, controlDispatcher, mediaButtonEvent);
            return isHandled || super.onMediaButtonEvent(mediaButtonEvent);
        }
    }
}
