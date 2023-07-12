package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;

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

        appVideoPlayView.setEventListener(new AppVideoPlayView.EventListener() {

            @Override
            public void onScreenChanged(boolean fullScreen, boolean portrait) {
                if (requireActivity() instanceof IPlayActivity) {
                    IPlayActivity playActivity = (IPlayActivity) requireActivity();
                    playActivity.onVideoScreenChanged(fullScreen, portrait);
                }
            }

            @Override
            public boolean onRetryClick(View view) {
                if (requireActivity() instanceof IPlayActivity) {
                    IPlayActivity playActivity = (IPlayActivity) requireActivity();
                    return playActivity.onVideoRetryClick(video);
                }
                return false;
            }

            @Override
            public void onLoginClick(View view) {
                video.setNeed_login(false);
                appVideoPlayView.setVideo(video);
            }

            @Override
            public void onLiveStateBtnClick(View view, int state) {
                if (state == Video.STATUS_SOON_START) {
                    video.setStatus(Video.STATUS_STARTED);
                    appVideoPlayView.setVideo(video);
                } else if (state == Video.STATUS_STARTED) {
                    video.setStatus(Video.STATUS_REVIEW);
                    appVideoPlayView.setVideo(video);
                } else if (state == Video.STATUS_REVIEW) {
                    video.setStatus(Video.STATUS_STARTING);
                    appVideoPlayView.setVideo(video);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        video = Objects.requireNonNull(getArguments() != null ? (Video) getArguments().getSerializable(EXTRA_KEY_VIDEO) : null);

        Player player = new ExoPlayer.Builder(requireActivity()).build();

        appVideoPlayView.setPlayer(player);

        appVideoPlayView.setVideo(video);
    }

    @Override
    public void onResume() {
        super.onResume();
        appVideoPlayView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        appVideoPlayView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appVideoPlayView.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if (appVideoPlayView != null) {
            return appVideoPlayView.onBackPressed();
        }
        return false;
    }
}
