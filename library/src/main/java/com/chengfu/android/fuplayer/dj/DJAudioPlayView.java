package com.chengfu.android.fuplayer.dj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chengfu.android.fuplayer.audio.widget.AudioPlayView;

public class DJAudioPlayView extends AudioPlayView {

    public DJAudioPlayView(@NonNull Context context) {
        this(context, null);
    }

    public DJAudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DJAudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
