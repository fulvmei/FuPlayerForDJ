package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.Context;
import android.media.MediaDescription;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chengfu.android.fuplayer.FuPlayer;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public final class MediaSessionPlayer {
    public static final String TAG = "MediaSessionPlayer";

    private final PlayerEventListener playerEventListener;
    private final ComponentListener componentListener;

    private final Context context;
    private final Looper looper;
    private final FuPlayer player;
    private final MediaSessionCompat mediaSession;
    private List<MediaDescriptionCompat> playQueue;
    private final DefaultDataSourceFactory dataSourceFactory;


    public MediaSessionPlayer(@NonNull Context context, @NonNull MediaSessionCompat mediaSession) {
        playerEventListener=new PlayerEventListener();
        componentListener = new ComponentListener();

        this.context = context;
        looper = Util.getLooper();
        player = new FuExoPlayerFactory(context).create();
        player.addListener(playerEventListener);
        this.mediaSession = mediaSession;

        dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, context.getApplicationInfo().packageName), null);

        mediaSession.setCallback(componentListener, new Handler(looper));
    }

    public void setPlayQueue(List<MediaDescriptionCompat> playQueue) {
        FuLog.d(TAG, "setPlayQueue : playQueue=" + playQueue);
        this.playQueue = playQueue;

        ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource();
        for (MediaDescriptionCompat media : playQueue) {
            mediaSource.addMediaSource(ExoMediaSourceUtil.buildMediaSource(media.getMediaUri(), null, dataSourceFactory, media));
        }
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }
    private class PlayerEventListener implements FuPlayer.EventListener{
        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            FuLog.d(TAG, "onTimelineChanged : timeline=" + timeline+",manifest="+manifest+",reason="+reason);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            FuLog.d(TAG, "onTracksChanged : trackGroups=" + trackGroups+",trackSelections="+trackSelections);
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            FuLog.d(TAG, "onLoadingChanged : isLoading=" + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            FuLog.d(TAG, "onPlayerStateChanged : playWhenReady=" + playWhenReady+",playbackState="+playbackState);
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            FuLog.d(TAG, "onIsPlayingChanged : isPlaying=" + isPlaying);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            FuLog.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            FuLog.d(TAG, "onShuffleModeEnabledChanged : shuffleModeEnabled=" + shuffleModeEnabled);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            FuLog.d(TAG, "onPlayerError : error=" + error);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            FuLog.d(TAG, "onPositionDiscontinuity : reason=" + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            FuLog.d(TAG, "onPlaybackParametersChanged : playbackParameters=" + playbackParameters);
        }

        @Override
        public void onSeekProcessed() {

        }
    }

    private class ComponentListener extends MediaSessionCompat.Callback {
        @Override
        public void onSkipToNext() {
            player.next();
        }
    }
}
