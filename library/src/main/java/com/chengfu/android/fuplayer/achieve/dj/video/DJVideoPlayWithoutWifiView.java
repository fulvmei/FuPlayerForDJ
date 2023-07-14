package com.chengfu.android.fuplayer.achieve.dj.video;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.achieve.dj.video.util.NetworkUtil;
import com.chengfu.android.fuplayer.ui.BaseStateView;
import com.google.android.exoplayer2.Player;

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
            player.prepare();
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
        return player != null
                && !allowPlayInNoWifi
                && NetworkUtil.getNetWorkType(context) != NetworkUtil.NETWORK_WIFI
                && player.isLoading();
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
    protected void onAttachedToPlayer(@NonNull Player player) {
        player.addListener(componentListener);
        maybeShow();
    }

    @Override
    protected void onDetachedFromPlayer(@NonNull Player player) {
        player.removeListener(componentListener);
        hide();
    }


    private final class ComponentListener implements Player.Listener {
        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            maybeShow();
        }
    }
}
