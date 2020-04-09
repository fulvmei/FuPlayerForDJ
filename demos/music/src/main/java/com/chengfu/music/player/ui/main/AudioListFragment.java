package com.chengfu.music.player.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.AudioEntity;
import com.chengfu.music.player.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AudioListFragment extends Fragment {

    RecyclerView recyclerView;
    AudioListAdapter adapter;

    public static AudioListFragment newInstance() {
        Bundle args = new Bundle();

        AudioListFragment fragment = new AudioListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));



        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getList() == null) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AudioPlayManager.setCurrentPlayList(requireContext(), adapter.getList(),0);
                    }
                }).start();

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new AudioListAdapter();

        recyclerView.setAdapter(adapter);

        AudioDatabase.getInstance(requireContext()).audioDao().queryAll().observe(this, new Observer<List<AudioEntity>>() {
            @Override
            public void onChanged(List<AudioEntity> audioEntities) {
                adapter.setData(audioEntities);
            }
        });

    }
}
