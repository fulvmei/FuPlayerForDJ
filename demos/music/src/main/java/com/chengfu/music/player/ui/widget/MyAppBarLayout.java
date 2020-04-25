package com.chengfu.music.player.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;

public class MyAppBarLayout extends AppBarLayout {

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    OnSizeChangedListener onSizeChangedListener;

    public MyAppBarLayout(@NonNull Context context) {
        super(context);
    }

    public MyAppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAppBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    public OnSizeChangedListener getOnSizeChangedListener() {
        return onSizeChangedListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }
}
