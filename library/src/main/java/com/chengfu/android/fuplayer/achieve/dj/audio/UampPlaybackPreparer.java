package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.chengfu.android.fuplayer.ext.mediasession.MediaSessionConnector;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;

public class UampPlaybackPreparer implements MediaSessionConnector.PlaybackPreparer {

    private static final String TAG = "UampPlaybackPreparer";

    private MusicSource musicSource;
    private FuPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;

    public UampPlaybackPreparer(FuPlayer exoPlayer, DataSource.Factory dataSourceFactory, MusicSource musicSource) {
        this.musicSource = musicSource;
        this.exoPlayer = exoPlayer;
        this.dataSourceFactory = dataSourceFactory;

        Log.d(TAG, "UampPlaybackPreparer");
    }

    @Override
    public long getSupportedPrepareActions() {
        Log.d(TAG, "getSupportedPrepareActions");
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH;
    }

    @Override
    public void onPrepare() {
        Log.d("UampPlaybackPreparer", "onPrepare");
    }

    @Override
    public void onPrepareFromMediaId(String mediaId, Bundle extras) {
        Log.d(TAG, "onPrepareFromMediaId : mediaId=" + mediaId + ",musicSource=" + musicSource);
        MediaDescriptionCompat itemToPlay = musicSource.findByMediaId(mediaId);
        String parentId = musicSource.findParentIdByMediaId(mediaId);

        if (itemToPlay == null) {
            FuLog.w(TAG, "Content not found: MediaID=$mediaId");
            return;
        }

        MediaDescriptionCompat currentMedia = (MediaDescriptionCompat) exoPlayer.getCurrentTag();

        if (currentMedia != null
                && currentMedia.getMediaId() != null
                && currentMedia.getMediaId().equals(itemToPlay.getMediaId())) {
            exoPlayer.retry();
            return;
        }

        ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource();

        for (MediaDescriptionCompat media : musicSource.getMediaList(parentId)) {
            mediaSource.addMediaSource(ExoMediaSourceUtil.buildMediaSource(media.getMediaUri(), null, dataSourceFactory,media));
        }
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
        int initialWindowIndex = musicSource.indexOf(parentId, itemToPlay);

        exoPlayer.prepare(mediaSource);
        exoPlayer.seekTo(initialWindowIndex, 0);
    }

    @Override
    public void onPrepareFromSearch(String query, Bundle extras) {
        Log.d(TAG, "onPrepareFromSearch");
    }

    @Override
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        Log.d(TAG, "onPrepareFromUri");
    }

    @Override
    public boolean onCommand(FuPlayer player, ControlDispatcher controlDispatcher, String command, Bundle extras, ResultReceiver cb) {
        return false;
    }
}
