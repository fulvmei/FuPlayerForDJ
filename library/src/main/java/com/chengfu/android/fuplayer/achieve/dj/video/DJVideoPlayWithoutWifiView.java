package com.chengfu.android.fuplayer.achieve.dj.video;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.video.util.NetworkUtil;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.google.android.exoplayer2.ExoPlayer;

public class DJVideoPlayWithoutWifiView extends BaseStateView {

    private final Context context;
    protected final ComponentListener componentListener = new ComponentListener();

    protected OnAllowPlayInNoWifiChangeListener onAllowPlayInNoWifiChangeListener;

    private boolean allowPlayInNoWifi;

    public interface OnAllowPlayInNoWifiChangeListener {
        void onAllowPlayInNoWifiChange(boolean allowPlayInNoWifi);
    }

    public DJVideoPlayWithoutWifiView(@NonNull Context context) {
        this(context, null);
    }

    public DJVideoPlayWithoutWifiView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public DJVideoPlayWithoutWifiView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.fu_view_video_state_without_wifi, this, true);

        findViewById(R.id.btnPlayWithoutWifi).setOnClickListener(v -> {
            allowPlayInNoWifi = true;
            player.retry();
            hide();
            if (onAllowPlayInNoWifiChangeListener != null) {
                onAllowPlayInNoWifiChangeListener.onAllowPlayInNoWifiChange(allowPlayInNoWifi);
            }
        });

        hide();
        maybeShow();
    }

    public boolean isAllowPlayInNoWifi() {
        return allowPlayInNoWifi;
    }

    public void setAllowPlayInNoWifi(boolean allowPlayInNoWifi) {
        if (this.allowPlayInNoWifi == allowPlayInNoWifi) {
            return;
        }
        this.allowPlayInNoWifi = allowPlayInNoWifi;
        maybeShow();
        if (onAllowPlayInNoWifiChangeListener != null) {
            onAllowPlayInNoWifiChangeListener.onAllowPlayInNoWifiChange(allowPlayInNoWifi);
        }
    }

    protected void maybeShow() {
        if (isInShowState()) {
            player.stop();
            show();
        }
    }

    protected boolean isInShowState() {
        if (player != null
                && !allowPlayInNoWifi
                && NetworkUtil.getNetWorkType(context) != NetworkUtil.NETWORK_WIFI
                && player.isLoading()) {
            return true;
        }
        return false;
    }

    public OnAllowPlayInNoWifiChangeListener getOnAllowPlayInNoWifiChangeListener() {
        return onAllowPlayInNoWifiChangeListener;
    }

    public void setOnAllowPlayInNoWifiChangeListener(OnAllowPlayInNoWifiChangeListener onAllowPlayInNoWifiChangeListener) {
        this.onAllowPlayInNoWifiChangeListener = onAllowPlayInNoWifiChangeListener;
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {

    }

    @Override
    protected void onAttachedToPlayer(@NonNull FuPlayer player) {
        player.addListener(componentListener);
        maybeShow();
    }

    @Override
    protected void onDetachedFromPlayer(@NonNull FuPlayer player) {
        player.removeListener(componentListener);
        hide();
    }


    private final class ComponentListener implements ExoPlayer.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            maybeShow();
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            maybeShow();
        }
    }
}
