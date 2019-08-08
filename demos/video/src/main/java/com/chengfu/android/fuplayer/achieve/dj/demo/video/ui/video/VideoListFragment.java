package com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.chengfu.android.fuplayer.achieve.dj.demo.video.R;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.util.VideoUtil;

import java.util.List;

public class VideoListFragment extends Fragment implements IBackPressed {

    private RecyclerView recyclerView;

    private VideoListAdapter adapter;

    //    private ExoPlayer player;
    String title;


    public static VideoListFragment newInstance(String title) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        player = ExoPlayerFactory.newSimpleInstance(getActivity());

        title = getArguments().getString("title");

        List<Video> videos;
        if ("在线".equals(title)) {
            videos = VideoUtil.getVideoList();

        } else if ("本地".equals(title)) {
            videos = VideoUtil.getLocalVideoList(getActivity());
        } else if ("全部".equals(title)) {
            videos = VideoUtil.getVideoList();
            videos.addAll(VideoUtil.getLocalVideoList(getActivity()));
        } else {
            videos = VideoUtil.getAudiooList();
        }

        adapter = new VideoListAdapter(getActivity());

        recyclerView.setAdapter(adapter);

        adapter.setData(videos);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        System.out.println("onConfigurationChanged title=" + title);
        if (adapter != null) {
            adapter.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.onResume();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (adapter != null) {
            if (hidden) {
                adapter.maybeStopPlay();
            } else {
                adapter.maybeStartPlay();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (adapter != null) {
            if (!isVisibleToUser) {
                adapter.maybeStopPlay();
            } else {
                adapter.maybeStartPlay();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (adapter != null) {
            return adapter.onBackPressed();
        }
        return false;
    }
}
