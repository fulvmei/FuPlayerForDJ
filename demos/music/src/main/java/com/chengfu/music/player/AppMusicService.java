package com.chengfu.music.player;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.player.MediaSessionPlayer1;

public class AppMusicService extends MusicService {

    @Override
    public PendingIntent createCurrentContentIntent(MediaMetadataCompat metadata) {
        Intent sessionIntent = new Intent();
        sessionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sessionIntent.putExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS, metadata != null ? metadata.getBundle() : null);
        ComponentName componentName = new ComponentName(this, getApplication().getPackageName() + ".FuSessionActivity");
        sessionIntent.setComponent(componentName);
        return PendingIntent.getActivity(this, MusicContract.REQUEST_CODE_SESSION_ACTIVITY, sessionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onLoadMedia(MediaDescriptionCompat description, MediaSessionPlayer1.MediaLoadCallback callback) {

        if (true){
            callback.onCompleted(description);
            return;
        }

        Handler mainHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                MediaDescriptionCompat d = new MediaDescriptionCompat.Builder()
                        .setMediaId(description.getMediaId())
                        .setMediaUri(description.getMediaUri())
                        .setTitle(description.getTitle())
                        .setSubtitle(description.getSubtitle())
                        .setDescription(description.getDescription())
                        .setIconUri(description.getIconUri())
                        .setIconBitmap(description.getIconBitmap())
                        .setExtras(description.getExtras())
                        .build();
                callback.onCompleted(d);
//                callback.onFailure();
            }
        };
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainHandler.sendEmptyMessage(0);
            }
        }, 20);
    }

    @Override
    public void onLoadItem(String itemId, @NonNull Result<MediaBrowserCompat.MediaItem> result) {
        super.onLoadItem(itemId, result);
        Log.e("eee","onLoadItem itemId="+itemId);
    }
}
