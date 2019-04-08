package com.chengfu.android.fuplayer.dj.audio.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chengfu.android.fuplayer.audio.FuLog;
import com.chengfu.android.fuplayer.audio.extensions.PlaybackStateCompatExt;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class AudioPlayView extends FrameLayout {

    private static final int TIME_UNIT = 1000;

    private static final String TAG = "MusicPlayView";

    final ComponentListener componentListener;
    Context context;
    MediaSessionCompat.Token sessionToken;
    MediaControllerCompat controller;

    final StringBuilder formatBuilder;
    final Formatter formatter;

    ValueAnimator progressAnimator;

    ImageView icon;
    TextView title;
    TextView subtitle;
    ImageButton previous;
    ImageButton play;
    ImageButton pause;
    ImageButton play2;
    ImageButton pause2;
    ImageButton next;
    SeekBar seek;
    TextView position;
    TextView duration;
    ImageButton shuffle;
    ImageButton repeat;

    long duration_ms;

    public AudioPlayView(@NonNull Context context) {
        this(context, null);
    }

    public AudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        componentListener = new ComponentListener();

        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        icon = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_icon);

        title = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_title);

        subtitle = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_subtitle);

        previous = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_previous);
        if (previous != null) {
            previous.setOnClickListener(componentListener);
        }

        play = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_play);
        if (play != null) {
            play.setOnClickListener(componentListener);
        }

        pause = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_pause);
        if (pause != null) {
            pause.setOnClickListener(componentListener);
        }

        play2 = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_play2);
        if (play2 != null) {
            play2.setOnClickListener(componentListener);
        }

        pause2 = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_pause2);
        if (pause2 != null) {
            pause2.setOnClickListener(componentListener);
        }

        next = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_next);
        if (next != null) {
            next.setOnClickListener(componentListener);
        }

        seek = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_seek);
        if (seek != null) {
            seek.setOnSeekBarChangeListener(componentListener);
        }

        position = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_position);

        duration = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_duration);

        shuffle = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_shuffle_switch);
        if (shuffle != null) {
            shuffle.setOnClickListener(componentListener);
        }

        repeat = findViewById(com.chengfu.android.fuplayer.audio.R.id.music_controller_repeat_switch);
        if (repeat != null) {
            repeat.setOnClickListener(componentListener);
        }

        updateAll();
    }


    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(com.chengfu.android.fuplayer.audio.R.layout.defaut_audio_view, parent, false);
    }

    public void setSessionToken(MediaSessionCompat.Token sessionToken) {
        if (this.sessionToken == sessionToken) {
            return;
        }
        this.sessionToken = sessionToken;
        if (sessionToken == null) {
            controller = null;
        } else {
            try {
                controller = new MediaControllerCompat(context, sessionToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (controller != null) {
            controller.registerCallback(componentListener);
        }
        updateAll();
    }

    protected void updateAll() {
        if (!isInShowState()) {
            return;
        }
        updateRepeat(controller != null ? controller.getRepeatMode() : PlaybackStateCompat.REPEAT_MODE_INVALID);
        updateShuffle(controller != null ? controller.getShuffleMode() : PlaybackStateCompat.SHUFFLE_MODE_INVALID);

        updatePlaybackState(controller != null ? controller.getPlaybackState() : null);
        updateMetadata(controller != null ? controller.getMetadata() : null);
    }

    protected void updateMetadata(MediaMetadataCompat metadata) {
        MediaDescriptionCompat description = metadata != null ? metadata.getDescription() : null;

        if (title != null) {
            title.setText(description != null && description.getTitle() != null ? description.getTitle() : "");
        }
        if (subtitle != null) {
            subtitle.setText(description != null && description.getSubtitle() != null ? description.getSubtitle() : "");
        }
        if (icon != null) {
            updateIcon(icon, description);
        }

        duration_ms = metadata != null ? metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) : 0;

        if (seek != null) {
            seek.setMax((int) (duration_ms / TIME_UNIT));
        }

        if (duration != null) {
            duration.setText(stringForTime(duration_ms));
        }
    }

    protected void updateShuffle(int shuffleMode) {
        if (shuffle == null) {
            return;
        }
        if (controller == null || controller.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_INVALID) {
            setViewEnabled(shuffle, false);
            return;
        }
        setViewEnabled(shuffle, true);

        if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            shuffle.setAlpha(0.5F);
        } else if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
            shuffle.setAlpha(1.0F);
        }
    }

    protected void updateRepeat(int repeatMode) {
        if (repeat == null) {
            return;
        }
        if (controller == null || controller.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_INVALID) {
            setViewEnabled(repeat, false);
            return;
        }
        setViewEnabled(repeat, true);
        if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) {
            repeat.setImageResource(com.chengfu.android.fuplayer.audio.R.drawable.ic_repeat_black_24dp);
            repeat.setAlpha(0.5f);
        } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
            repeat.setImageResource(com.chengfu.android.fuplayer.audio.R.drawable.ic_repeat_one_black_24dp);
            repeat.setAlpha(1.0f);
        } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
            repeat.setImageResource(com.chengfu.android.fuplayer.audio.R.drawable.ic_repeat_black_24dp);
            repeat.setAlpha(1.0f);
        }
    }

    protected void updateIcon(@NonNull ImageView icon, @Nullable MediaDescriptionCompat description) {
        if (description == null) {
            icon.setImageBitmap(null);
            return;
        }
        if (description.getIconBitmap() != null) {
            icon.setImageBitmap(description.getIconBitmap());
            return;
        }
        icon.setImageURI(description.getIconUri());

    }

    protected void updateProgress(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        if (progressAnimator != null) {
            progressAnimator.cancel();
            progressAnimator = null;
        }

        long bufferedPosition = state.getBufferedPosition();
        long currentPosition = state.getPosition();
        if (seek != null) {
            seek.setProgress((int) (currentPosition / TIME_UNIT));
            seek.setSecondaryProgress((int) (bufferedPosition / TIME_UNIT));
        }
        if (position != null) {
            position.setText(stringForTime(currentPosition));
        }

        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            long timeToEnd = (duration_ms - currentPosition);

            if (timeToEnd > 0) {
                if (progressAnimator != null) {
                    progressAnimator.cancel();
                    progressAnimator = null;
                }
                progressAnimator = new ValueAnimator();

                progressAnimator = ValueAnimator.ofInt((int) currentPosition, (int) duration_ms);
                progressAnimator.setDuration(timeToEnd);

                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.addUpdateListener(componentListener);
                progressAnimator.start();
            }
        }
    }

    protected void updatePlaybackState(PlaybackStateCompat playbackState) {
        setViewEnabled(previous, playbackState != null && PlaybackStateCompatExt.isSkipToPreviousEnabled(playbackState));

        setViewEnabled(next, playbackState != null && PlaybackStateCompatExt.isSkipToNextEnabled(playbackState));

        setViewEnabled(play, playbackState != null && PlaybackStateCompatExt.isPlayEnabled(playbackState));

        setViewEnabled(pause, playbackState != null && PlaybackStateCompatExt.isPauseEnabled(playbackState));

        setViewEnabled(play2, playbackState != null && PlaybackStateCompatExt.isPlayEnabled(playbackState));

        setViewEnabled(pause2, playbackState != null && PlaybackStateCompatExt.isPauseEnabled(playbackState));

        if (playbackState != null && !PlaybackStateCompatExt.isPlaying(playbackState)) {
            setVisibility(play, true);
            setVisibility(pause, false);

            setVisibility(play2, true);
            setVisibility(pause2, false);
        } else {
            setVisibility(play, false);
            setVisibility(pause, true);

            setVisibility(play2, false);
            setVisibility(pause2, true);
        }

        updateProgress(playbackState);
    }

    protected String stringForTime(long timeMs) {
        if (timeMs <= 0) {
            timeMs = 0;
        }

        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    protected void setViewEnabled(View view, boolean enabled) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);

    }

    protected void setVisibility(View view, boolean visible) {
        if (view == null) {
            return;
        }
        if (visible) {
            view.setVisibility(VISIBLE);
        } else {
            view.setVisibility(GONE);
        }
    }

    protected boolean isInShowState() {
        return true;
    }

    public void show() {
        setVisibility(VISIBLE);
    }


    public void hide() {
        setVisibility(GONE);
    }

    public void play() {
        if (controller != null) {
            controller.getTransportControls().play();
            FuLog.d(TAG, "play ");
        }
    }

    public void pause() {
        if (controller != null) {
            controller.getTransportControls().pause();
        }
    }

    public void previous() {
        if (controller != null) {
            controller.getTransportControls().skipToPrevious();
        }
    }

    public void next() {
        if (controller != null) {
            controller.getTransportControls().skipToNext();
        }
    }

    private final class ComponentListener extends MediaControllerCompat.Callback implements OnClickListener, SeekBar.OnSeekBarChangeListener, ValueAnimator.AnimatorUpdateListener {
        private boolean isTracking = false;

        @Override
        public void onClick(View v) {
            if (v == play || v == play2) {
                play();
            } else if (v == pause || v == pause2) {
                pause();
            } else if (v == previous) {
                previous();
            } else if (v == next) {
                next();
            } else if (v == shuffle) {
                if (controller == null) {
                    return;
                }
                if (controller.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                    controller.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                } else if (controller.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                    controller.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                }
            } else if (v == repeat) {
                if (controller == null) {
                    return;
                }
                if (controller.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_NONE) {
                    controller.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                } else if (controller.getShuffleMode() == PlaybackStateCompat.REPEAT_MODE_ONE) {
                    controller.getTransportControls().setShuffleMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                } else if (controller.getShuffleMode() == PlaybackStateCompat.REPEAT_MODE_ALL) {
                    controller.getTransportControls().setShuffleMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                }
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (controller != null) {
                controller.getTransportControls().seekTo(seekBar.getProgress() * TIME_UNIT);
            }
            isTracking = false;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (isTracking) {
                animation.cancel();
                return;
            }

            int animatedIntValue = (int) animation.getAnimatedValue();
            if (seek != null) {
                seek.setProgress(animatedIntValue / TIME_UNIT);
            }
            if (position != null) {
                position.setText(stringForTime(animatedIntValue));
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            FuLog.d(TAG, "onMetadataChanged metadata=" + metadata);
            updateMetadata(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            FuLog.d(TAG, "onPlaybackStateChanged state=" + state);
            updatePlaybackState(state);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            FuLog.d(TAG, "onSessionDestroyed");
        }

        @Override
        public void onSessionReady() {
            super.onSessionReady();
            FuLog.d(TAG, "onSessionReady");
        }

        @Override
        public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
            super.onAudioInfoChanged(info);
            FuLog.d(TAG, "onAudioInfoChanged : info=" + info);
        }

        @Override
        public void onCaptioningEnabledChanged(boolean enabled) {
            super.onCaptioningEnabledChanged(enabled);
            FuLog.d(TAG, "onCaptioningEnabledChanged : enabled=" + enabled);
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
            FuLog.d(TAG, "onExtrasChanged : extras=" + extras);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            FuLog.d(TAG, "onQueueChanged : queue=" + queue);
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);
            FuLog.d(TAG, "onQueueTitleChanged : title=" + title);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            super.onRepeatModeChanged(repeatMode);
            FuLog.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);

            updateRepeat(repeatMode);
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            FuLog.d(TAG, "onSessionEvent : event=" + event + ",extras=" + extras);
        }

        @Override
        public void onShuffleModeChanged(int shuffleMode) {
            super.onShuffleModeChanged(shuffleMode);
            FuLog.d(TAG, "onShuffleModeChanged : shuffleMode=" + shuffleMode);
            updateShuffle(shuffleMode);
        }

    }
}
