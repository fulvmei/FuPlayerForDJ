package com.chengfu.music.player.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.achieve.dj.audio.widget.AudioControlView;

public class AppAudioControlView extends AudioControlView {


    public AppAudioControlView(@NonNull Context context) {
        super(context);
    }

    public AppAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppAudioControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(com.chengfu.android.fuplayer.achieve.dj.R.layout.fu_defaut_audio_view, parent, false);
    }
}
