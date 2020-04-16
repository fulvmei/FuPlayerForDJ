package com.chengfu.music.player;

import android.app.Application;

import com.chengfu.android.fuplayer.util.FuLog;

public class MusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FuLog.DEBUG = true;
    }
}
