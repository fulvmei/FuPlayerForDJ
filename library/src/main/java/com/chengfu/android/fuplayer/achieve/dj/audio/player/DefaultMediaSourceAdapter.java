package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DefaultMediaSourceAdapter implements MediaSourceAdapter {

    private Context context;

    private List<MediaSessionCompat.QueueItem> queueItemList;

    public DefaultMediaSourceAdapter(Context context) {
        this.context = context;
        queueItemList = new ArrayList<>();
    }

    @Override
    public MediaSource onCreateMediaSource(int position) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, context.getApplicationInfo().packageName), null);
        MediaSource mediaSource = ExoMediaSourceUtil.buildMediaSource(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0703/5778246106dab.mp3"), null, dataSourceFactory);
        return mediaSource;
    }

    @Override
    public void addQueueItem(MediaDescriptionCompat description) {
        MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, queueItemList.size());
        queueItemList.add(queueItem);
    }
}
