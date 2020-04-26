package com.chengfu.music.player.ui.main;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.DataBaseManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.music.player.MainActivity;
import com.chengfu.music.player.R;
import com.chengfu.music.player.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

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

        view.findViewById(R.id.playAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MediaDescriptionCompat> medias = (ArrayList<MediaDescriptionCompat>) adapter.getList();
                MainActivity.audioPlayClient.setPlayList(medias, true);
            }
        });
        System.out.println("Fragment context 对象  context="+getContext());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new AudioListAdapter();

        recyclerView.setAdapter(adapter);

        adapter.setData(MusicUtil.getTestMedias(6, false));

//        AudioDatabase.getInstance(requireContext()).audioDao().queryAll().observe(this, new Observer<List<MediaEntity>>() {
//            @Override
//            public void onChanged(List<MediaEntity> audioEntities) {
//                adapter.setData(audioEntities);
//            }
//        });

    }
}
