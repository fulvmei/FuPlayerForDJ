package com.chengfu.android.fuplayer.achieve.dj.demo.video.player;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;

import com.chengfu.android.fuplayer.achieve.dj.video.DJVideoControlView;
import com.chengfu.android.fuplayer.achieve.dj.video.screen.ScreenRotationHelper;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.chengfu.android.fuplayer.ui.FuPlayerView;
import com.chengfu.android.fuplayer.ui.StateView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;

import java.util.concurrent.CopyOnWriteArraySet;

public class FuPlayerManager implements StateView {

    private static final String TAG = "FuPlayer";

    private static final String TAG_HIDE_CONTROLLER_WITH_STATE_VIEW_SHOW = "tag_hide_controller_with_state_view_show";

    private Context mContext;
    private final ComponentListener mComponentListener;
    private MediaSessionCompat mMediaSession;
    private Player mPlayer;
    private FuPlayerView mPlayerView;
    private DJVideoControlView mVideoControlView;
    private ScreenRotationHelper mScreenRotation;
    private CopyOnWriteArraySet<BaseStateView> mStateViews = new CopyOnWriteArraySet<>();

    public FuPlayerManager(@NonNull Context context, @NonNull Player player) {

        this.mContext = context;
        this.mPlayer = player;
        mComponentListener = new ComponentListener();

        mMediaSession = new MediaSessionCompat(mContext, TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

//        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mMediaSession, new DefaultPlaybackController() {
//            @Override
//            public void onPlay(Player player) {
//                super.onPlay(player);
//                showController();
//            }
//
//            @Override
//            public void onPause(Player player) {
//                super.onPause(player);
//                showController();
//
//            }
//        });
//        mediaSessionConnector.setPlayer(mPlayer, null);

        mPlayer.addListener(mComponentListener);
    }

    @Override
    public Player getPlayer() {
        return mPlayer;
    }

    @Override
    public void setPlayer(Player player) {

    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        if (mVideoControlView != null) {
            mVideoControlView.setFullScreen(fullScreen);
        }
        for (BaseStateView stateView : mStateViews) {
            stateView.setFullScreen(fullScreen);
        }
    }

    public FuPlayerView getPlayerView() {
        return mPlayerView;
    }

    public void setPlayerView(FuPlayerView playerView) {
        if (this.mPlayerView == playerView) {
            return;
        }
        if (this.mPlayerView != null) {
            this.mPlayerView.setPlayer(null);
        }
        this.mPlayerView = playerView;
        if (playerView != null) {
            playerView.setPlayer(mPlayer);
        }
    }

    public void setVideoControlView(DJVideoControlView controlView) {
        if (this.mVideoControlView == controlView) {
            return;
        }
        if (this.mVideoControlView != null) {
            this.mVideoControlView.setPlayer(null);
            this.mVideoControlView.setOnScreenClickListener(null);
            this.mVideoControlView.setOnBackClickListener(null);
        }
        this.mVideoControlView = controlView;
        if (controlView != null) {
            mVideoControlView.setPlayer(mPlayer);
            this.mVideoControlView.setOnScreenClickListener(mComponentListener);
            this.mVideoControlView.setOnBackClickListener(mComponentListener);
        }
    }

    public DJVideoControlView getVideoControlView() {
        return mVideoControlView;
    }

    public void addStateView(BaseStateView stateView) {
        addStateView(stateView, false);
    }

    public void addStateView(BaseStateView stateView, boolean hideController) {
        if (stateView == null) {
            return;
        }
        if (hideController) {
            stateView.setTag(TAG_HIDE_CONTROLLER_WITH_STATE_VIEW_SHOW);
        }
        mStateViews.add(stateView);
        stateView.addVisibilityChangeListener(mComponentListener);
        stateView.setPlayer(mPlayer);
    }

    public void removeStateView(BaseStateView stateView) {
        if (stateView == null) {
            return;
        }
        mStateViews.remove(stateView);
        stateView.setPlayer(null);
        stateView.removeVisibilityChangeListener(mComponentListener);
    }

    public void clearStateViews() {
        for (BaseStateView stateView : mStateViews) {
            stateView.setPlayer(null);
            stateView.removeVisibilityChangeListener(mComponentListener);
        }
        mStateViews.clear();
    }

    public void setScreenRotation(ScreenRotationHelper screenRotation) {
        if (this.mScreenRotation == screenRotation) {
            return;
        }
        if (this.mScreenRotation != null) {
            this.mScreenRotation.setPlayer(null);
        }
        this.mScreenRotation = screenRotation;
        if (screenRotation != null) {
            screenRotation.setPlayer(mPlayer);
        }
    }

    public ScreenRotationHelper getScreenRotation() {
        return mScreenRotation;
    }

    public void showController() {
        if (mVideoControlView == null) {
            return;
        }
        mVideoControlView.show();
    }

    public void maybeHideController() {
        if (mVideoControlView == null) {
            return;
        }
        if (mVideoControlView.isShowing()) {
            mVideoControlView.hide();
        }
    }

    public void prepare(MediaItem mediaSource) {
        mPlayer.setMediaItem(mediaSource,true);
        mPlayer.prepare();
        mPlayer.setPlayWhenReady(true);

        onResume();
    }

    public void stopPlay() {
        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
        if (mScreenRotation != null) {
            mScreenRotation.pause();
        }
        mPlayer.stop(true);
    }

    public void onResume() {
        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
        if (mScreenRotation != null) {
            mScreenRotation.resume();
        }
        if (mPlayer != null && mPlayer.getPlaybackState() == Player.STATE_IDLE
                && mPlayer.getPlayerError() == null) {
            mPlayer.prepare();
        }
    }


    public void onPause() {
        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
        if (mScreenRotation != null) {
            mScreenRotation.pause();
        }
        if (mPlayer.getPlaybackState() == Player.STATE_BUFFERING
                || mPlayer.getPlaybackState() == Player.STATE_READY) {
            mPlayer.stop();
        }
    }


    public boolean onBackPressed() {
        if (mScreenRotation != null) {
            return mScreenRotation.maybeToggleToPortrait();
        }
        return false;
    }

    public void onDestroy() {
        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
        if (mScreenRotation != null) {
            mScreenRotation.pause();
        }
        mPlayer.release();
    }

    private final class ComponentListener implements VisibilityChangeListener, Player.Listener, DJVideoControlView.OnScreenClickListener, DJVideoControlView.OnBackClickListener {

        @Override
        public void onVisibilityChange(StateView stateView, boolean visibility) {
            if (stateView instanceof BaseStateView && visibility) {
                BaseStateView baseStateView = (BaseStateView) stateView;
                if (baseStateView.getTag() != null
                        && baseStateView.getTag().equals(TAG_HIDE_CONTROLLER_WITH_STATE_VIEW_SHOW)) {
                    maybeHideController();
                }
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.e("qwer", "onPlayerStateChanged : playWhenReady=" + playWhenReady + ",playbackState=" + playbackState);
            if (playbackState == Player.STATE_READY) {
                mMediaSession.setActive(true);
            } else {
                mMediaSession.setActive(false);
            }
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            Log.e("qwer", "onPlayerError : playWhenReady=" + error + ",playbackState=" + error);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Log.e("qwer", "onPositionDiscontinuity ");
        }

        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            Log.e("qwer", "onTimelineChanged : timeline=" + timeline + ",reason=" + reason);
        }

        @Override
        public void onScreenClick(boolean fullScreen) {
            if (mScreenRotation != null) {
                mScreenRotation.manualToggleOrientation();
            }
        }

        @Override
        public void onBackClick(View v) {
            if (mScreenRotation != null) {
                mScreenRotation.maybeToggleToPortrait();
            }
        }
    }

}
