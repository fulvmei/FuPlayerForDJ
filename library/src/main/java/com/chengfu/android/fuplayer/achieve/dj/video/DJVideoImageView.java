package com.chengfu.android.fuplayer.achieve.dj.video;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.video.VideoSize;

public class DJVideoImageView extends BaseStateView {

    protected final ComponentListener componentListener;
    private final ImageView image;
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
    protected void onAttachedToPlayer(@NonNull Player player) {
        if (player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady()) {
            hasFirstFrame = true;
        } else {
            hasFirstFrame = false;
        }
        updateVisibility();

        player.addListener(componentListener);
    }

    @Override
    protected void onDetachedFromPlayer(@NonNull Player player) {
        hasFirstFrame = false;
        updateVisibility();

        player.removeListener(componentListener);
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
            case Player.STATE_IDLE:
                if (player.getPlayerError() != null) {
                    return showInError || !hasFirstFrame;
                }
                return true;
            case Player.STATE_BUFFERING:
                return !hasFirstFrame;
            case Player.STATE_ENDED:
                if (showInEnded) {
                    return true;
                } else return !hasFirstFrame;
            default:
                return false;
        }
    }

    private final class ComponentListener implements Player.Listener {

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            updateVisibility();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            updateVisibility();
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            updateVisibility();
        }

        @Override
        public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
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
