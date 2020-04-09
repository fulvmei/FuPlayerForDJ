package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.Context;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.google.android.exoplayer2.util.Util;

public final class AudioPlayerMediator {

    private final ComponentListener componentListener;

    private final Looper looper;
    private final FuPlayer player;
    private final MediaSession mediaSession;

    public AudioPlayerMediator(@NonNull Context context, @NonNull MediaSession mediaSession) {
        componentListener = new ComponentListener();

        looper = Util.getLooper();
        player = new FuExoPlayerFactory(context).create();
        this.mediaSession = mediaSession;

        mediaSession.setCallback(componentListener, new Handler(looper));
    }

    private static class ComponentListener extends MediaSession.Callback
            implements FuPlayer.EventListener {

    }
}
