package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.service.media.MediaBrowserService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.android.fuplayer.util.FuLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicService extends MediaBrowserService implements LifecycleOwner {
    public static final String TAG = "MusicService";

    private MediaSession mediaSession;
    private LifecycleRegistry lifecycle;

    @Override
    public void onCreate() {
        super.onCreate();
        FuLog.DEBUG = true;
        FuLog.d(TAG, "onCreate");
        lifecycle = new LifecycleRegistry(this);
        lifecycle.setCurrentState(Lifecycle.State.RESUMED);

        mediaSession = new MediaSession(this, TAG);

        setSessionToken(mediaSession.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        FuLog.d(TAG, "onGetRoot : clientPackageName=" + clientPackageName + ",clientUid=" + clientUid + ",rootHints=" + rootHints);
            return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {
        FuLog.d(TAG, "onLoadChildren : parentId=" + parentId);
        result.detach();
        AudioPlayManager.getCurrentPlayList(this).observe(this, new Observer<List<CurrentPlay>>() {
            @Override
            public void onChanged(List<CurrentPlay> currentPlays) {
                result.sendResult(new ArrayList<>());
            }
        });
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.setCurrentState(Lifecycle.State.DESTROYED);
    }
}
