package com.chengfu.music.player.ui.player;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.music.player.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class RecentListFragment extends BottomSheetDialogFragment {

    RecyclerView recyclerView;
    RecentPlayListAdapter adapter;
//    AudioPlayClient audioPlayClient;

    public static RecentListFragment newInstance() {
        Bundle args = new Bundle();

        RecentListFragment fragment = new RecentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        audioPlayClient=new AudioPlayClient(requireContext());
        adapter = new RecentPlayListAdapter();

        adapter.setOnItemClickListener(new RecentPlayListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, MediaDescriptionCompat item) {
//                audioPlayClient.playFromMediaId(item.getMediaId());
            }
        });

        recyclerView.setAdapter(adapter);

        AudioPlayClient.getRecentList(requireContext(),5).observe(this, new Observer<List<MediaDescriptionCompat>>() {
            @Override
            public void onChanged(List<MediaDescriptionCompat> medias) {
                adapter.setData(medias);
            }
        });
    }
}
