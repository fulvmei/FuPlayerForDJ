package cn.gzmovement.kernel.fu.widget;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.R;
import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.VideoIcon;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.video.VideoListener;

public class AppVideoWatermarkView extends BaseStateView {

    protected final ComponentListener componentListener;

    protected ImageView watermarkView;
    private boolean hasFirstFrame;
    private boolean disable;

    protected int screenWidth;
    protected int videoHeight;
    protected VideoIcon videoIcon;

    public AppVideoWatermarkView(@NonNull Context context) {
        this(context, null);
    }

    public AppVideoWatermarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppVideoWatermarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        screenWidth = getScreenWidth(getContext());
        videoHeight = (int) (screenWidth * 9F / 16F);

        watermarkView = findViewById(R.id.watermark);

        componentListener = new ComponentListener();

        updateVisibility();

        updateVideoIcon();
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.app_view_video_watermark, parent, false);
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {
        updateVideoIconSize();
    }

    public void setVideoIcon(VideoIcon videoIcon) {
        this.videoIcon = videoIcon;
        updateVideoIcon();
    }

    private void updateVideoIcon() {
        boolean haoIcon = false;
        if (videoIcon != null && !TextUtils.isEmpty(videoIcon.getUrl())) {
            haoIcon = true;
        }
        if (!haoIcon) {
            watermarkView.setImageResource(0);
            return;
        }

        updateVideoIconSize();

        Glide.with(this).load(Uri.parse(videoIcon.getUrl())).into(watermarkView);
//        ImageLoaderHelper.simpleLoadImageByGlide(getContext(), watermarkView, videoIcon.getUrl(), null);
    }

    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context

                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void updateVideoIconSize() {
        if (isFullScreen()) {
            int size = videoIcon.getScale_to_y() > 0 ? (int) (screenWidth / (double) videoIcon.getScale_to_y()) : 0;
            watermarkView.getLayoutParams().height = size;
            watermarkView.getLayoutParams().width = size;
        } else {
            int size = videoIcon.getScale_to_y() > 0 ? (int) (videoHeight / (double) videoIcon.getScale_to_y()) : 0;
            watermarkView.getLayoutParams().height = size;
            watermarkView.getLayoutParams().width = size;
        }
        watermarkView.setImageAlpha((int) (255 * videoIcon.getOpacity()));
    }

    public void setDisable(boolean disable) {
        if (this.disable == disable) {
            return;
        }
        this.disable = disable;
        updateVisibility();
    }

    public boolean isDisable() {
        return disable;
    }

    protected void updateVisibility() {
        if (isInShowState()) {
            show();
        } else {
            hide();
        }
    }

    public boolean isInShowState() {
        if (disable || player == null) {
            return false;
        }
        switch (player.getPlaybackState()) {
            case FuPlayer.STATE_READY:
                return true;
            case FuPlayer.STATE_BUFFERING:
            case FuPlayer.STATE_ENDED:
                if (hasFirstFrame) {
                    return true;
                }
                return false;
            default:
                return false;
        }
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

    private final class ComponentListener implements FuPlayer.EventListener, VideoListener {

        @Override
        public void onPlayerError(PlaybackException error) {
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
