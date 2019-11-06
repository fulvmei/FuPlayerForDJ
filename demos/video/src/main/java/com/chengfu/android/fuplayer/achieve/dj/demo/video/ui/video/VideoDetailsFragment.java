package com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.R;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.local.LocalVideoListAdapter;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.util.VideoUtil;

import java.util.List;

public class VideoDetailsFragment extends Fragment  {

    private RecyclerView recyclerView;

    private LocalVideoListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Video> videos= VideoUtil.getVideoList();
        adapter = new LocalVideoListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setData(videos);
    }
}
