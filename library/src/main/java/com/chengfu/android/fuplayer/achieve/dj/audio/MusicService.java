package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.android.fuplayer.util.FuLog;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat implements LifecycleOwner {
    public static final String TAG = "MusicService";

    private LifecycleRegistry lifecycle;
    private MediaSessionCompat mediaSession;
    private MediaSessionPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        FuLog.DEBUG = true;
        FuLog.d(TAG, "onCreate");
        lifecycle = new LifecycleRegistry(this);
        lifecycle.setCurrentState(Lifecycle.State.RESUMED);

        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setActive(true);
        setSessionToken(mediaSession.getSessionToken());

        player = new MediaSessionPlayer(this, mediaSession);

        AudioPlayManager.getCurrentPlayList(this).observe(this, new Observer<List<CurrentPlay>>() {
            @Override
            public void onChanged(List<CurrentPlay> currentPlays) {
                player.setPlayQueue(ConverterUtil.currentPlayListToMediaDescriptionList(currentPlays));
            }
        });
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        FuLog.d(TAG, "onGetRoot : clientPackageName=" + clientPackageName + ",clientUid=" + clientUid + ",rootHints=" + rootHints);
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        FuLog.d(TAG, "onLoadChildren : parentId=" + parentId);
//        LiveData<List<CurrentPlay>> currentPlayList = AudioPlayManager.getCurrentPlayList(this);
//        currentPlayList.observe(this, new Observer<List<CurrentPlay>>() {
//            @Override
//            public void onChanged(List<CurrentPlay> currentPlays) {
//                FuLog.d(TAG, "onLoadChildren : onChanged=");
//                result.sendResult(new ArrayList<>());
//            }
//        });
        result.sendResult(new ArrayList<>());
//        result.detach();
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
