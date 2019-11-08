package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoBufferingView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoControlView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoEndedView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoIdleView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoImageView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoPlayErrorView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoPlayWithoutWifiView;
import com.chengfu.android.fuplayer.achieve.dj.video.screen.ScreenRotationHelper;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.chengfu.android.fuplayer.ui.FuPlayerView;
import com.chengfu.android.fuplayer.ui.PlayerView;
import com.chengfu.android.fuplayer.ui.StateView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import cn.gzmovement.kernel.fu.widget.AppVideoWatermarkView;

public class AppVideoPlayView extends FrameLayout {

    private static final String TAG = "AppVideoPlayView";

    private final ComponentListener componentListener;

    private FuPlayerView playerView;
    private AppVideoWatermarkView videoWatermarkView;
    private DJVideoImageView videoImageView;
    private DJVideoControlView videoControlView;
    private DJVideoIdleView videoIdleView;
    private DJVideoBufferingView videoBufferingView;
    private DJVideoPlayErrorView videoPlayErrorView;
    private DJVideoEndedView videoEndedView;
    private DJVideoPlayWithoutWifiView noWifiView;

    private FuPlayer player;
    private boolean fullScreen;
    private ScreenRotationHelper screenRotation;

    private MediaSessionCompat mediaSession;

    public AppVideoPlayView(@NonNull Context context) {
        this(context, null);
    }

    public AppVideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppVideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AppVideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.layout_dj_compat_video_player, this);

        componentListener = new ComponentListener();

        initViews();
    }

    private void initViews() {
        playerView = findViewById(R.id.playerView);
        videoWatermarkView = findViewById(R.id.videoWatermarkView);
        videoControlView = findViewById(R.id.videoControlView);
        videoImageView = findViewById(R.id.videoImageView);
        videoIdleView = findViewById(R.id.videoIdleView);
        videoBufferingView = findViewById(R.id.videoBufferingView);
        videoPlayErrorView = findViewById(R.id.videoPlayErrorView);
        videoEndedView = findViewById(R.id.videoEndedView);
        noWifiView = findViewById(R.id.noWifiView);


    }

    public FuPlayer getPlayer() {
        return player;
    }

    public void setPlayer(FuPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);

            mediaSession = new MediaSessionCompat(getContext(), TAG);

            MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);

            mediaSessionConnector.setMediaButtonEventHandler((player1, controlDispatcher, mediaButtonEvent) -> {
                KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                    maybeHideController();
                }
                return false;
            });
            mediaSessionConnector.setPlayer(player);
        }

        playerView.setPlayer(player);
        videoWatermarkView.setPlayer(player);
        videoControlView.setPlayer(player);
        videoImageView.setPlayer(player);
        videoIdleView.setPlayer(player);
        videoBufferingView.setPlayer(player);
        videoPlayErrorView.setPlayer(player);
        videoEndedView.setPlayer(player);
        noWifiView.setPlayer(player);

        if (screenRotation != null) {
            screenRotation.setPlayer(player);
        }
    }

    public void setFullScreen(boolean fullScreen) {
        if (this.fullScreen == fullScreen) {
            return;
        }
        this.fullScreen = fullScreen;

        videoControlView.setFullScreen(fullScreen);
        videoImageView.setFullScreen(fullScreen);
        videoIdleView.setFullScreen(fullScreen);
        videoBufferingView.setFullScreen(fullScreen);
        videoPlayErrorView.setFullScreen(fullScreen);
        videoEndedView.setFullScreen(fullScreen);
        noWifiView.setFullScreen(fullScreen);

        noWifiView.addVisibilityChangeListener(componentListener);
        videoPlayErrorView.addVisibilityChangeListener(componentListener);
        videoEndedView.addVisibilityChangeListener(componentListener);
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setScreenRotation(ScreenRotationHelper screenRotation) {
        if (this.screenRotation == screenRotation) {
            return;
        }
        if (this.screenRotation != null) {
            this.screenRotation.setPlayer(null);
        }
        this.screenRotation = screenRotation;
        if (screenRotation != null) {
            screenRotation.setPlayer(player);
        }
    }

    public void showController() {
        videoControlView.show();
    }

    public void maybeHideController() {
        if (videoControlView.isShowing()) {
            videoControlView.hide();
        }
    }

    public ScreenRotationHelper getScreenRotation() {
        return screenRotation;
    }

    public void showBuffering() {
        videoBufferingView.show();
        videoPlayErrorView.hide();
        videoEndedView.hide();
    }

    public void showError() {
        videoBufferingView.hide();
        videoPlayErrorView.show();
        videoEndedView.hide();
    }

    public void stopPlay() {
        if (playerView != null) {
            playerView.onPause();
        }
        if (screenRotation != null) {
            screenRotation.pause();
        }
        player.stop(true);
    }

    public void onResume() {
        if (playerView != null) {
            playerView.onResume();
        }
        if (screenRotation != null) {
            screenRotation.resume();
        }
        if (player != null && player.getPlaybackState() == Player.STATE_IDLE
                && player.getPlaybackError() == null) {
            player.retry();
        }
    }


    public void onPause() {
        if (playerView != null) {
            playerView.onPause();
        }
        if (screenRotation != null) {
            screenRotation.pause();
        }
        if (player.getPlaybackState() == Player.STATE_BUFFERING
                || player.getPlaybackState() == Player.STATE_READY) {
            player.stop();
        }
    }


    public boolean onBackPressed() {
        if (screenRotation != null) {
            return screenRotation.maybeToggleToPortrait();
        }
        return false;
    }

    public void onDestroy() {
        if (playerView != null) {
            playerView.onPause();
        }
        if (screenRotation != null) {
            screenRotation.pause();
        }
        player.release();
    }

    private final class ComponentListener implements StateView.VisibilityChangeListener, Player.EventListener, DJVideoControlView.OnScreenClickListener, DJVideoControlView.OnBackClickListener {

        @Override
        public void onVisibilityChange(StateView stateView, boolean visibility) {
            if (stateView instanceof BaseStateView && visibility) {
                BaseStateView baseStateView = (BaseStateView) stateView;
                int id = baseStateView.getId();
                if (id == R.id.noWifiView
                        || id == R.id.videoPlayErrorView
                        || id == R.id.videoEndedView) {
                    maybeHideController();
                }
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY) {
                mediaSession.setActive(true);
            } else {
                mediaSession.setActive(false);
            }
        }

        @Override
        public void onScreenClick(boolean fullScreen) {
            if (screenRotation != null) {
                screenRotation.manualToggleOrientation();
            }
        }

        @Override
        public void onBackClick(View v) {
            if (screenRotation != null) {
                screenRotation.maybeToggleToPortrait();
            }
        }
    }
}
