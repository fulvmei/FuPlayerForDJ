package com.chengfu.music.player.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;
import com.chengfu.music.player.R;

public class AppAudioControlView extends AudioControlView {


    public AppAudioControlView(@NonNull Context context) {
        this(context, null);
    }

    public AppAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
//        return inflater.inflate(R.layout.app_audio_control_view, parent, false);
//    }
}
