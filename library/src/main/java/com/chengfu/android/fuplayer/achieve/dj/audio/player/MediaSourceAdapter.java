package com.chengfu.android.fuplayer.achieve.dj.audio.player;

import android.support.v4.media.MediaDescriptionCompat;

import com.google.android.exoplayer2.source.MediaSource;

public interface MediaSourceAdapter {

    MediaSource onCreateMediaSource(int position);

    void addQueueItem(MediaDescriptionCompat description);
}
