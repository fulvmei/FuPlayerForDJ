package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.support.v4.media.session.PlaybackStateCompat;

public class PlaybackStateCompatExt {

    /**
     * Useful extension methods for [PlaybackStateCompat].
     */
    public static boolean isPrepared(PlaybackStateCompat playbackState) {
        int state = playbackState.getState();
        return (state == PlaybackStateCompat.STATE_BUFFERING) ||
                (state == PlaybackStateCompat.STATE_PLAYING) ||
                (state == PlaybackStateCompat.STATE_PAUSED);
    }


    public static boolean isPlaying(PlaybackStateCompat playbackState) {
        int state = playbackState.getState();
        long actions = playbackState.getActions();
        return (state == PlaybackStateCompat.STATE_BUFFERING ||
                state == PlaybackStateCompat.STATE_PLAYING) &&
                ((actions & PlaybackStateCompat.ACTION_PLAY) == 0L);
    }

    public static boolean isPlayEnabled(PlaybackStateCompat playbackState) {
        int state = playbackState.getState();
        long actions = playbackState.getActions();
        return ((actions & PlaybackStateCompat.ACTION_PLAY) != 0L) ||
                ((actions & PlaybackStateCompat.ACTION_PLAY_PAUSE) != 0L) &&
                        (state == PlaybackStateCompat.STATE_PAUSED);
    }

    public static boolean isPauseEnabled(PlaybackStateCompat playbackState) {
        int state = playbackState.getState();
        long actions = playbackState.getActions();
        return ((actions & PlaybackStateCompat.ACTION_PAUSE) != 0L) ||
                (((actions & PlaybackStateCompat.ACTION_PLAY_PAUSE) != 0L) &&
                        (state == PlaybackStateCompat.STATE_BUFFERING ||
                                state == PlaybackStateCompat.STATE_PLAYING));
    }

    public static boolean isSkipToNextEnabled(PlaybackStateCompat playbackState) {
        long actions = playbackState.getActions();
        return (actions & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0L;
    }

    public static boolean isSkipToPreviousEnabled(PlaybackStateCompat playbackState) {
        long actions = playbackState.getActions();
        return (actions & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0L;
    }

    public static boolean isSeekToEnabled(PlaybackStateCompat playbackState) {
        long actions = playbackState.getActions();
        return (actions & PlaybackStateCompat.ACTION_SEEK_TO) != 0L;
    }

    public static String stateName(PlaybackStateCompat playbackStateCompat) {
        int state = playbackStateCompat.getState();
        switch (state) {
            case PlaybackStateCompat.STATE_NONE:
                return "STATE_NONE";
            case PlaybackStateCompat.STATE_STOPPED:
                return "STATE_STOPPED";
            case PlaybackStateCompat.STATE_PAUSED:
                return "STATE_PAUSED";
            case PlaybackStateCompat.STATE_PLAYING:
                return "STATE_PLAYING";
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
                return "STATE_FAST_FORWARDING";
            case PlaybackStateCompat.STATE_REWINDING:
                return "STATE_REWINDING";
            case PlaybackStateCompat.STATE_BUFFERING:
                return "STATE_BUFFERING";
            case PlaybackStateCompat.STATE_ERROR:
                return "STATE_ERROR";
            default:
                return "UNKNOWN_STATE";
        }
    }
}
