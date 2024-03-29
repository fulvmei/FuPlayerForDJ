package com.chengfu.android.fuplayer.achieve.dj.audio.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
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

import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.PlaybackStateCompatExt;
import com.chengfu.android.fuplayer.achieve.dj.audio.player.TimingOff;
import com.chengfu.android.fuplayer.ui.DefaultControlView;
import com.chengfu.android.fuplayer.util.FuLog;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class AudioControlView extends FrameLayout {

    private static final int TIME_UNIT = 1000;

    private static final String TAG = "MusicPlayView";

    protected final ActionHandler actionHandler;
    protected final ControllerEventsHandler controllerEventsHandler;
    protected Context context;
    protected MediaSessionCompat.Token sessionToken;
    protected MediaControllerCompat controller;

    protected final StringBuilder formatBuilder;
    protected final Formatter formatter;

    protected ValueAnimator progressAnimator;

    protected ImageView icon;
    protected TextView title;
    protected TextView subtitle;
    protected ImageButton previous;
    protected ImageButton play;
    protected ImageButton pause;
    protected ImageButton next;
    protected SeekBar seek;
    protected TextView position;
    protected TextView duration;
    protected ImageButton shuffle;
    protected ImageButton repeat;

    protected long duration_ms;

    protected TimingOff timingOff;

    protected OnVisibilityChangeListener onVisibilityChangeListener;

    public interface OnVisibilityChangeListener {
        void onVisibilityChange(View v, boolean visible);
    }

    public AudioControlView(@NonNull Context context) {
        this(context, null);
    }

    public AudioControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        actionHandler = initActionHandler();
        controllerEventsHandler = initControllerEventsHandler();

        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        icon = findViewById(R.id.audio_controller_icon);

        title = findViewById(R.id.audio_controller_title);

        subtitle = findViewById(R.id.audio_controller_subtitle);

        previous = findViewById(R.id.audio_controller_previous);
        if (previous != null) {
            previous.setOnClickListener(actionHandler);
        }

        play = findViewById(R.id.audio_controller_play);
        if (play != null) {
            play.setOnClickListener(actionHandler);
        }

        pause = findViewById(R.id.audio_controller_pause);
        if (pause != null) {
            pause.setOnClickListener(actionHandler);
        }

        next = findViewById(R.id.audio_controller_next);
        if (next != null) {
            next.setOnClickListener(actionHandler);
        }

        seek = findViewById(R.id.audio_controller_seek);
        if (seek != null) {
            seek.setOnSeekBarChangeListener(actionHandler);
        }

        position = findViewById(R.id.audio_controller_position);

        duration = findViewById(R.id.audio_controller_duration);

        shuffle = findViewById(R.id.audio_controller_shuffle_switch);
        if (shuffle != null) {
            shuffle.setOnClickListener(actionHandler);
        }

        repeat = findViewById(R.id.audio_controller_repeat_switch);
        if (repeat != null) {
            repeat.setOnClickListener(actionHandler);
        }

        updateAll();
    }

    protected ActionHandler initActionHandler() {
        return new ActionHandler();
    }

    protected ControllerEventsHandler initControllerEventsHandler() {
        return new ControllerEventsHandler();
    }

    public OnVisibilityChangeListener getOnVisibilityChangeListener() {
        return onVisibilityChangeListener;
    }

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener onVisibilityChangeListener) {
        this.onVisibilityChangeListener = onVisibilityChangeListener;
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fu_defaut_audio_view, parent, false);
    }

    public void setSessionToken(MediaSessionCompat.Token sessionToken) {
        if (this.sessionToken == sessionToken) {
            return;
        }
        this.sessionToken = sessionToken;
        if (sessionToken == null) {
            controller = null;
        } else {
//            try {
            controller = new MediaControllerCompat(context, sessionToken);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }
        if (controller != null) {
            controller.registerCallback(controllerEventsHandler);
        }
        updateAll();
    }

    public TimingOff getTimingOff() {
        return timingOff;
    }

    public MediaControllerCompat getController() {
        return controller;
    }

    protected void updateAll() {
        updateRepeat(controller != null ? controller.getRepeatMode() : PlaybackStateCompat.REPEAT_MODE_INVALID);
        updateShuffle(controller != null ? controller.getShuffleMode() : PlaybackStateCompat.SHUFFLE_MODE_INVALID);

        updateMetadata(controller != null ? controller.getMetadata() : null);
        updatePlaybackState(controller != null ? controller.getPlaybackState() : null);

        Bundle extras = controller != null ? controller.getExtras() : null;
        if (extras != null) {
//            extras.setClassLoader(TimingOff.class.getClassLoader());
//            timingOff = extras.getParcelable(MusicContract.KEY_TIMING_OFF);
            timingOff = TimingOff.fromJson(extras.getString(MusicContract.KEY_TIMING_OFF));
        }
        if (timingOff == null) {
            timingOff = TimingOff.defaultTimingOff();
        }

        updateTimingOff(timingOff);
    }

    protected void updateMetadata(MediaMetadataCompat metadata) {
        MediaDescriptionCompat description = metadata != null ? metadata.getDescription() : null;

        if (title != null) {
            CharSequence oldText = title.getText();
            CharSequence newText = description != null ? description.getTitle() : null;
            if (!TextUtils.equals(oldText, newText)) {
                title.setText(newText);
            }
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

    public void setTimingOff(TimingOff timingOff) {
        if (controller == null) {
            return;
        }
        Bundle params = new Bundle();
//        params.setClassLoader(TimingOff.class.getClassLoader());
//        params.putParcelable(MusicContract.KEY_TIMING_OFF, timingOff);
        params.putString(MusicContract.KEY_TIMING_OFF, TimingOff.toJson(timingOff));
        controller.sendCommand(MusicContract.COMMAND_SET_TIMING_OFF_MODE, params, null);
    }

    protected void updateTimingOff(TimingOff timingOff) {

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
            repeat.setImageResource(R.drawable.fu_ic_repeat_black_24dp);
            repeat.setAlpha(0.5f);
        } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
            repeat.setImageResource(R.drawable.fu_ic_repeat_one_black_24dp);
            repeat.setAlpha(1.0f);
        } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
            repeat.setImageResource(R.drawable.fu_ic_repeat_black_24dp);
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
                progressAnimator.addUpdateListener(actionHandler);
                progressAnimator.start();
            }
        }
    }

    protected void updatePlaybackState(PlaybackStateCompat state) {
        setViewEnabled(previous, state != null && PlaybackStateCompatExt.isSkipToPreviousEnabled(state));

        setViewEnabled(next, state != null && PlaybackStateCompatExt.isSkipToNextEnabled(state));

//        setViewEnabled(play, state != null && PlaybackStateCompatExt.isPlayEnabled(state));
//
//        setViewEnabled(pause, state != null && PlaybackStateCompatExt.isPauseEnabled(state));
//
//        setViewEnabled(play2, state != null && PlaybackStateCompatExt.isPlayEnabled(state));
//
//        setViewEnabled(pause2, state != null && PlaybackStateCompatExt.isPauseEnabled(state));

        if (state != null && PlaybackStateCompatExt.isPlayEnabled(state)) {
            setVisibility(play, false);
            setVisibility(pause, true);
        } else {
            setVisibility(play, true);
            setVisibility(pause, false);

        }

        if (state != null && PlaybackStateCompatExt.isSeekToEnabled(state)) {
            seek.setVisibility(VISIBLE);
        } else {
            seek.setVisibility(INVISIBLE);
        }
//        setViewEnabled(seek, state != null && PlaybackStateCompatExt.isSeekToEnabled(state));

        updateProgress(state);

        updateVisibility(state);
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

    protected void updateVisibility(PlaybackStateCompat state) {
        if (isInShowState(state)) {
            show();
        } else {
            hide();
        }
    }

    protected boolean isInShowState(PlaybackStateCompat state) {
        return true;
    }

    protected void show() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            if (onVisibilityChangeListener != null) {
                onVisibilityChangeListener.onVisibilityChange(this, true);
            }
        }
    }

    protected void hide() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
            if (onVisibilityChangeListener != null) {
                onVisibilityChangeListener.onVisibilityChange(this, false);
            }
        }
    }

    public void play() {
        if (controller != null) {
            controller.getTransportControls().play();
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

    protected class ActionHandler implements OnClickListener, SeekBar.OnSeekBarChangeListener, ValueAnimator.AnimatorUpdateListener {
        protected boolean isTracking = false;

        @Override
        public void onClick(View v) {
            if (v == play) {
                play();
            } else if (v == pause) {
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
                } else if (controller.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ONE) {
                    controller.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                } else if (controller.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL) {
                    controller.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
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
    }

    protected class ControllerEventsHandler extends MediaControllerCompat.Callback {
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
            if (extras != null) {
//                extras.setClassLoader(TimingOff.class.getClassLoader());
//                timingOff = extras.getParcelable(MusicContract.KEY_TIMING_OFF);
                timingOff = TimingOff.fromJson(extras.getString(MusicContract.KEY_TIMING_OFF));
            }
            if (timingOff == null) {
                timingOff = TimingOff.defaultTimingOff();
            }
            updateTimingOff(timingOff);
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
