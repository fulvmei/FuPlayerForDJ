package com.chengfu.android.fuplayer.dj.audio;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.chengfu.android.fuplayer.dj.audio.library.MusicSource;
import com.chengfu.android.fuplayer.util.FuLog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;

public class UampPlaybackPreparer implements MediaSessionConnector.PlaybackPreparer {

    private static final String TAG = "UampPlaybackPreparer";

    private MusicSource musicSource;
    private ExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;

    public UampPlaybackPreparer(ExoPlayer exoPlayer, DataSource.Factory dataSourceFactory, MusicSource musicSource) {
        this.musicSource = musicSource;
        this.exoPlayer = exoPlayer;
        this.dataSourceFactory = dataSourceFactory;
        Log.d("UampPlaybackPreparer", "UampPlaybackPreparer");
    }

    @Override
    public long getSupportedPrepareActions() {
        Log.d("UampPlaybackPreparer", "getSupportedPrepareActions");
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
        Log.d("UampPlaybackPreparer", "onPrepareFromMediaId : mediaId="+mediaId+",musicSource="+musicSource);
        MediaDescriptionCompat itemToPlay = musicSource.findByMediaId(mediaId);

        if (itemToPlay == null) {
            FuLog.w(TAG, "Content not found: MediaID=$mediaId");

            // TODO: Notify caller of the error.
        } else {
            ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource();

            for (MediaDescriptionCompat media : musicSource.getMediaList("")) {
                ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .setTag(media.getDescription())
                        .createMediaSource(media.getMediaUri());
                mediaSource.addMediaSource(extractorMediaSource);
            }
            // Since the playlist was probably based on some ordering (such as tracks
            // on an album), find which window index to play first so that the song the
            // user actually wants to hear plays first.
            int initialWindowIndex = musicSource.indexOf(itemToPlay);
//
//            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                    .setTag("222")
//                    .createMediaSource(Uri.parse("http://mvoice.spriteapp.cn/voice/2016/0517/573b1240d0118.mp3"));
//            mediaSource.addMediaSource(extractorMediaSource);
            exoPlayer.prepare(mediaSource);
            exoPlayer.seekTo(initialWindowIndex, 0);
        }
    }

    @Override
    public void onPrepareFromSearch(String query, Bundle extras) {
        Log.d("UampPlaybackPreparer", "onPrepareFromSearch");
    }

    @Override
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        Log.d("UampPlaybackPreparer", "onPrepareFromUri");
    }

    @Override
    public String[] getCommands() {
        Log.d("UampPlaybackPreparer", "getCommands");
        return new String[0];
    }

    @Override
    public void onCommand(Player player, String command, Bundle extras, ResultReceiver cb) {
        Log.d("UampPlaybackPreparer", "onCommand command=" + command);
    }
}
