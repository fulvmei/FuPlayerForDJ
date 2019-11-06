package com.chengfu.android.fuplayer.achieve.dj.video;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.video.VideoListener;

public class DJVideoImageView extends BaseStateView {

    protected final ComponentListener componentListener;
    private ImageView image;
    private boolean hasFirstFrame;

    private boolean showInError;
    private boolean showInEnded;

    public DJVideoImageView(@NonNull Context context) {
        this(context, null);
    }

    public DJVideoImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DJVideoImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        image = findViewById(R.id.image);

        componentListener = new ComponentListener();
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {

    }

    @Override
    protected void onAttachedToPlayer(@NonNull FuPlayer player) {
        if (player.getPlaybackState() == FuPlayer.STATE_READY && player.getPlayWhenReady()) {
            hasFirstFrame = true;
        } else {
            hasFirstFrame = false;
        }
        updateVisibility();

        player.addListener(componentListener);
        if (player.getVideoComponent() != null) {
            player.getVideoComponent().addVideoListener(componentListener);
        }
    }

    @Override
    protected void onDetachedFromPlayer(@NonNull FuPlayer player) {
        hasFirstFrame = false;
        updateVisibility();

        player.removeListener(componentListener);
        if (player.getVideoComponent() != null) {
            player.getVideoComponent().removeVideoListener(componentListener);
        }
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fu_view_video_image, parent, false);
    }

    public ImageView getImage() {
        return image;
    }

    public boolean isShowInError() {
        return showInError;
    }

    public void setShowInError(boolean showInError) {
        this.showInError = showInError;
        updateVisibility();
    }

    public boolean isShowInEnded() {
        return showInEnded;
    }

    public void setShowInEnded(boolean showInEnded) {
        this.showInEnded = showInEnded;
        updateVisibility();
    }

    protected void updateVisibility() {
        if (isInShowState()) {
            show();
        } else {
            hide();
        }
    }

    protected boolean isInShowState() {
        if (player == null) {
            return true;
        }
        switch (player.getPlaybackState()) {
            case FuPlayer.STATE_IDLE:
                if (player.getPlaybackError() != null) {
                    if (showInError || !hasFirstFrame) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            case FuPlayer.STATE_READY:
                return false;
            case FuPlayer.STATE_BUFFERING:
                if (!hasFirstFrame) {
                    return true;
                }
                return false;
            case FuPlayer.STATE_ENDED:
                if (showInEnded) {
                    return true;
                } else if (!hasFirstFrame) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private final class ComponentListener implements FuPlayer.EventListener, VideoListener {

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            updateVisibility();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updateVisibility();
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            hasFirstFrame = false;
            updateVisibility();
        }

        @Override
        public void onSurfaceSizeChanged(int width, int height) {
            hasFirstFrame = false;
            updateVisibility();
        }

        @Override
        public void onRenderedFirstFrame() {
            hasFirstFrame = true;
            updateVisibility();
        }
    }
}
