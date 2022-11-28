package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoBufferingView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoControlView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoEndedView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoIdleView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoImageView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoPlayErrorView;
import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoPlayWithoutWifiView;
import com.chengfu.android.fuplayer.achieve.dj.video.screen.ScreenRotationHelper;
import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.chengfu.android.fuplayer.ui.FuPlayerView;
import com.chengfu.android.fuplayer.ui.StateView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

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
    private VIPStateView vipStateView;
    private LiveStateView liveStateView;

    private FuPlayer player;
    private boolean fullScreen;
    private ScreenRotationHelper screenRotation;

    private MediaSessionCompat mediaSession;

    private Video video;

    private EventListener eventListener;

    public interface EventListener {
        default void onScreenChanged(boolean fullScreen, boolean portrait) {
        }

        default boolean onRetryClick(View view) {
            return false;
        }

        default void onLoginClick(View view) {
        }

        default void onLiveStateBtnClick(View view, int state) {
        }
    }

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

        initScreenRotation(context);
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
        vipStateView = findViewById(R.id.vipStateView);
        liveStateView = findViewById(R.id.liveStateView);

        vipStateView.setVisibility(GONE);
        liveStateView.setVisibility(GONE);

        noWifiView.addVisibilityChangeListener(componentListener);
        videoPlayErrorView.addVisibilityChangeListener(componentListener);
        videoEndedView.addVisibilityChangeListener(componentListener);

        videoPlayErrorView.setOnReplayListener(player -> false);

        videoControlView.setShowBottomProgress(true);
        videoControlView.setShowTopOnlyFullScreen(true);
        videoControlView.setShowAlwaysInPaused(true);

        videoIdleView.setOnPlayerClickListener(v -> {
            if (eventListener != null && eventListener.onRetryClick(v)) {
                return;
            }
            if (player != null) {
                player.retry();
            }
        });

        videoPlayErrorView.setOnReplayListener(player -> eventListener != null && eventListener.onRetryClick(videoPlayErrorView));

        videoControlView.addVisibilityChangeListener((stateView, visibility) -> {
            if (visibility) {
                videoWatermarkView.setDisable(true);
            } else {
                videoWatermarkView.setDisable(false);
            }
        });

        videoControlView.setOnScreenClickListener(componentListener);
        videoControlView.setOnBackClickListener(componentListener);

        vipStateView.setOnLoginClickListener(view -> {
            if (eventListener != null) {
                eventListener.onLoginClick(view);
            }
        });

        liveStateView.setOnLiveStateBtnClickListener((view, state) -> {
            if (eventListener != null) {
                eventListener.onLiveStateBtnClick(view, state);
            }
        });

        setVideo(video);
    }

    private void initScreenRotation(Context context) {
        screenRotation = new ScreenRotationHelper((Activity) context);

        screenRotation.setDisableInPlayerStateEnd(false);
        screenRotation.setDisableInPlayerStateError(false);
        screenRotation.setToggleToPortraitInDisable(true);
        screenRotation.setEnablePortraitFullScreen(true);
        screenRotation.setAutoRotationMode(ScreenRotationHelper.AUTO_ROTATION_MODE_SYSTEM);

        screenRotation.setOnScreenChangedListener(portraitFullScreen -> changedScreen(portraitFullScreen,true));
    }

    private void changedScreen(boolean fullScreen, boolean portrait) {
        setFullScreen(fullScreen);
        if (fullScreen) {
            videoControlView.setEnableGestureType(DJVideoControlView.Gesture.SHOW_TYPE_BRIGHTNESS | DJVideoControlView.Gesture.SHOW_TYPE_PROGRESS | DJVideoControlView.Gesture.SHOW_TYPE_VOLUME);
        } else {
            videoControlView.setEnableGestureType(DJVideoControlView.Gesture.SHOW_TYPE_NONE);
        }
        if (eventListener != null) {
            eventListener.onScreenChanged(fullScreen,portrait);
        }
    }

    public EventListener getEventLinstener() {
        return eventListener;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        stopPlay();
        this.video = video;
        if (video != null) {
            liveStateView.setState(video.getStatus());
            if (video.getStatus() == Video.STATUS_STARTING) {
                hideLiveState();
                if (video.isNeed_login()) {
                    showVip();
                } else {
                    hideVip();
                    if (!TextUtils.isEmpty(video.getStream_url())) {
                        prepare(video.getStream_url());
                    } else {
                        showError();
                    }
                }
            } else {
                showLiveState();
                hideVip();
            }
        } else {
            hideVip();
            hideLiveState();
            showError();
        }
        String thumbnail = video != null ? video.getThumbnail() : null;
        if (TextUtils.isEmpty(thumbnail)) {
            videoImageView.getImage().setImageResource(0);
        } else {
            Glide.with(this).load(Uri.parse(thumbnail)).into(videoImageView.getImage());
        }

        videoWatermarkView.setVideoIcon(video != null ? video.getIcon() : null);
    }

    public void prepare(String url) {
        if (player == null) {
            return;
        }
        Uri uri = Uri.parse(video.getStream_url());
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter.Builder(getContext()).build();
        DataSource.Factory factory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), getContext().getPackageName()));
        MediaSource mediaSource = ExoMediaSourceUtil.buildMediaSource(uri, null, factory, defaultBandwidthMeter);

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        if (playerView!=null){
            playerView.onResume();
        }
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
            if (mediaSession != null) {
                mediaSession.release();
            }
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);

            mediaSession = new MediaSessionCompat(getContext(), TAG);

            MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);

            mediaSessionConnector.setMediaButtonEventHandler((player1, mediaButtonEvent) -> {
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
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void showController() {
        videoControlView.show();
    }

    public void maybeHideController() {
        if (videoControlView.isShowing()) {
            videoControlView.hide();
        }
    }


    public void showVip() {
        vipStateView.setVisibility(VISIBLE);
        liveStateView.setVisibility(GONE);
        noWifiView.hide();
        videoIdleView.hide();
        videoBufferingView.hide();
        videoPlayErrorView.hide();
        videoEndedView.hide();
    }

    public void hideVip() {
        vipStateView.setVisibility(GONE);
    }

    public void showLiveState() {
        vipStateView.setVisibility(GONE);
        liveStateView.setVisibility(VISIBLE);
        noWifiView.hide();
        videoIdleView.hide();
        videoBufferingView.hide();
        videoPlayErrorView.hide();
        videoEndedView.hide();
    }

    public void hideLiveState() {
        liveStateView.setVisibility(GONE);
    }

    public void showIdle() {
        vipStateView.setVisibility(GONE);

        videoIdleView.show();
        videoPlayErrorView.hide();
        videoEndedView.hide();
    }

    public void showBuffering() {
        vipStateView.setVisibility(GONE);
        videoBufferingView.show();
        videoPlayErrorView.hide();
        videoEndedView.hide();
    }

    public void showError() {
        vipStateView.setVisibility(GONE);
        videoBufferingView.hide();
        videoPlayErrorView.show();
        videoEndedView.hide();
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changedScreen(true,false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            changedScreen(false,false);
        }
    }
    protected boolean onResumePlay;
    public void onResume() {
        if (playerView != null) {
            playerView.onResume();
        }
        if (screenRotation != null) {
            screenRotation.resume();
        }
//        if (player != null && player.getPlaybackState() == Player.STATE_IDLE
//                && player.getPlaybackError() == null) {
//            player.retry();
//        }
        player.setPlayWhenReady(onResumePlay);
    }

    public void onPause() {
        if (playerView != null) {
            playerView.onPause();
        }
        if (screenRotation != null) {
            screenRotation.pause();
        }
//        if (player.getPlaybackState() == Player.STATE_BUFFERING
//                || player.getPlaybackState() == Player.STATE_READY) {
//            player.stop();
//        }
        onResumePlay = player.getPlayWhenReady();
        player.setPlayWhenReady(false);
    }

    public void stopPlay() {
        if (playerView != null) {
            playerView.onPause();
        }
        if (player != null) {
            player.stop(true);
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

    private final class ComponentListener implements StateView.VisibilityChangeListener, Player.Listener, DJVideoControlView.OnScreenClickListener, DJVideoControlView.OnBackClickListener {

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
