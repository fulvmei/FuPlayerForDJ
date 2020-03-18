package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 会员状态显示View（目前是登录状态来判断）
 */
public class VIPStateView extends FrameLayout {

    private View login;

    public VIPStateView(@NonNull Context context) {
        this(context, null);
    }

    public VIPStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VIPStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VIPStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater.from(context).inflate(R.layout.media_layout_vip_state, this);

        login = findViewById(R.id.login);
    }

    public void setOnLoginClickListener(OnClickListener onClickListener) {
        if (login != null) {
            login.setOnClickListener(onClickListener);
        }
    }
}
