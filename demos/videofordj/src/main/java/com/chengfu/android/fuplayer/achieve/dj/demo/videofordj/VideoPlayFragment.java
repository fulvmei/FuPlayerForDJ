package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;
import com.chengfu.android.fuplayer.ext.exo.FuExoPlayerFactory;
import com.chengfu.android.fuplayer.ext.exo.util.ExoMediaSourceUtil;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

public class VideoPlayFragment extends VideoPlayFragmentApi {

    private AppVideoPlayView appVideoPlayView;

    @NonNull
    private Video video;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appVideoPlayView = view.findViewById(R.id.appVideoPlayView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        video = Objects.requireNonNull(getArguments() != null ? (Video) getArguments().getSerializable(EXTRA_KEY_VIDEO) : null);

        appVideoPlayView.setPlayer(new FuExoPlayerFactory(requireContext()).create());

        MediaSource mediaSource = ExoMediaSourceUtil.buildMediaSource(Uri.parse(video.getStream_url()), null, new DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(),requireContext().getPackageName()), new DefaultBandwidthMeter()));

        appVideoPlayView.getPlayer().prepare(mediaSource);
    }
}
