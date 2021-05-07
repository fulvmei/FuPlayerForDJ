package com.chengfu.android.fuplayer.achieve.dj.demo.video.player;

import androidx.annotation.Nullable;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.io.IOException;

import timber.log.Timber;

public class PlayerAnalytics implements AnalyticsListener {

    @Override
    public void onAudioAttributesChanged(EventTime eventTime, AudioAttributes audioAttributes) {
        Timber.d("audioAttributes=" + audioAttributes);
    }

//    @Override
//    public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
//
//    }

    @Override
    public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

    }

    @Override
    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {

    }

    @Override
    public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

    }

    @Override
    public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

    }

    @Override
    public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {

    }

    @Override
    public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {

    }

//    @Override
//    public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
//
//    }

    @Override
    public void onDrmKeysLoaded(EventTime eventTime) {

    }

    @Override
    public void onDrmKeysRemoved(EventTime eventTime) {

    }

    @Override
    public void onDrmKeysRestored(EventTime eventTime) {

    }

    @Override
    public void onDrmSessionAcquired(EventTime eventTime) {

    }

    @Override
    public void onDrmSessionManagerError(EventTime eventTime, Exception error) {

    }

    @Override
    public void onDrmSessionReleased(EventTime eventTime) {

    }

    @Override
    public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {

    }

//    @Override
//    public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
//
//    }
//
//    @Override
//    public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
//
//    }
//
//    @Override
//    public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
//
//    }

    @Override
    public void onLoadingChanged(EventTime eventTime, boolean isLoading) {

    }

//    @Override
//    public void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
//        Timber.d("onLoadStarted loadEventInfo=" + loadEventInfo+",mediaLoadData="+mediaLoadData);
//    }
//
//    @Override
//    public void onMediaPeriodCreated(EventTime eventTime) {
//
//    }
//
//    @Override
//    public void onMediaPeriodReleased(EventTime eventTime) {
//
//    }

    @Override
    public void onMetadata(EventTime eventTime, Metadata metadata) {
        Timber.d("onPlayerError ： metadata=%s", metadata);
    }

    @Override
    public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {

    }

    @Override
    public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
        Timber.d("onPlayerError ： error=%s", error);
    }

    @Override
    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
        Timber.d("onPlayerStateChanged : playWhenReady=" + playWhenReady + ",playbackState=" + playbackState);
    }

    @Override
    public void onPositionDiscontinuity(EventTime eventTime, int reason) {
        Timber.d("onPositionDiscontinuity ： eventTime=" + eventTime.realtimeMs);
    }

//    @Override
//    public void onReadingStarted(EventTime eventTime) {
//
//    }

    @Override
    public void onRenderedFirstFrame(EventTime eventTime, @Nullable Surface surface) {
        Timber.d("onRenderedFirstFrame ： eventTime=" + eventTime.realtimeMs);
    }

    @Override
    public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {

    }

    @Override
    public void onSeekProcessed(EventTime eventTime) {

    }

    @Override
    public void onSeekStarted(EventTime eventTime) {

    }

    @Override
    public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {

    }

    @Override
    public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {
        Timber.d("onSurfaceSizeChanged ： eventTime=" + eventTime.realtimeMs+ ",width=" + width + ",height=" + height);
    }

    @Override
    public void onTimelineChanged(EventTime eventTime, int reason) {

    }

    @Override
    public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

//    @Override
//    public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
//
//    }

    @Override
    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Timber.d("onVideoSizeChanged ： eventTime=" + eventTime.realtimeMs+ ",width=" + width + ",height=" + height);
    }

    @Override
    public void onVolumeChanged(EventTime eventTime, float volume) {

    }
}
