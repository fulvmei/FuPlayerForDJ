package cn.gzmovement.kernel.fu.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.ui.BaseStateView;

public class AppVideoWatermarkView extends BaseStateView {

    public AppVideoWatermarkView(@NonNull Context context) {
        super(context);
    }

    public AppVideoWatermarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppVideoWatermarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {

    }

    @Override
    protected void onAttachedToPlayer(@NonNull FuPlayer player) {

    }

    @Override
    protected void onDetachedFromPlayer(@NonNull FuPlayer player) {

    }
}
