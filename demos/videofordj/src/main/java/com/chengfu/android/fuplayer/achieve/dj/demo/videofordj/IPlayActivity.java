package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;

public interface IPlayActivity {
    default void onVideoScreenChanged(boolean fullScreen, boolean portrait) {
    }

    default boolean onVideoRetryClick(Video video) {
        return false;
    }
}
