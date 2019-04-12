package com.chengfu.android.fuplayer.achieve.dj.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengfu.android.fuplayer.achieve.dj.R;
import com.chengfu.android.fuplayer.ui.SampleErrorView;

public class DJVideoPlayErrorView extends SampleErrorView {

    public DJVideoPlayErrorView(@NonNull Context context) {
        super(context);
    }

    public DJVideoPlayErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DJVideoPlayErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fpu_view_video_state_error, parent, false);
    }
}
