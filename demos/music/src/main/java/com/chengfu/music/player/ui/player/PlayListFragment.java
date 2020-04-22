package com.chengfu.music.player.ui.player;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.chengfu.android.fuplayer.achieve.dj.audio.AudioPlayClient;
import com.chengfu.music.player.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;

public class PlayListFragment extends SuperBottomSheetFragment {

    RecyclerView recyclerView;
    PlayListAdapter adapter;
    AudioPlayViewModel viewModel;

    public static PlayListFragment newInstance() {
        Bundle args = new Bundle();

        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getPeekHeight() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels * 2/ 3;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog()!=null){
            FrameLayout sheetContainer= getDialog().findViewById(R.id.super_bottom_sheet);
            ViewGroup.LayoutParams layoutParams = sheetContainer.getLayoutParams();
            layoutParams.height=getPeekHeight();
            sheetContainer.setLayoutParams(layoutParams);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        view.findViewById(R.id.close).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PlayListAdapter();

        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(requireActivity()).get(AudioPlayViewModel.class);

        viewModel.getQueueItems().observe(this, new Observer<List<MediaSessionCompat.QueueItem>>() {
            @Override
            public void onChanged(List<MediaSessionCompat.QueueItem> queueItems) {
                adapter.setData(queueItems);
            }
        });

        viewModel.getActiveQueueItemId().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                adapter.setActiveQueueItemId(aLong);
            }
        });

    }
}
