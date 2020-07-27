package com.chengfu.music.player.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.player.TimingOff;
import com.chengfu.music.player.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class TimingOffListFragment extends BottomSheetDialogFragment {

    RecyclerView recyclerView;
    TimingOffAdapter adapter;
    OnConfirmListener onConfirmListener;
    TimingOff timingOff;

    public static TimingOffListFragment newInstance(TimingOff timingOff) {
        Bundle args = new Bundle();
        args.putParcelable("timingOff", timingOff);
        TimingOffListFragment fragment = new TimingOffListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnConfirmListener {
        void onConfirm(TimingOff timingOff);
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timingoff_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm(timingOff);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        timingOff = (TimingOff) getArguments().getParcelable("timingOff");

        adapter = new TimingOffAdapter();

        recyclerView.setAdapter(adapter);

        List<TimingOff> list = new ArrayList<>();


        TimingOff timingOff1 = new TimingOff(TimingOff.TIMING_OFF_MODE_OFF,0, false);
        TimingOff timingOff2 = new TimingOff(TimingOff.TIMING_OFF_MODE_ONE,0, false);
        TimingOff timingOff3 = new TimingOff(TimingOff.TIMING_OFF_MODE_TIME,5,  false);
        TimingOff timingOff4 = new TimingOff(TimingOff.TIMING_OFF_MODE_TIME,10,  false);
        TimingOff timingOff5 = new TimingOff(TimingOff.TIMING_OFF_MODE_TIME,15,  false);

        boolean c=false;

        if (TimingOff.areItemsTheSame(timingOff,timingOff1)){
            timingOff1.setChecked(true);
            c=true;
        }

        if (TimingOff.areItemsTheSame(timingOff,timingOff2)){
            timingOff2.setChecked(true);
            c=true;
        }

        if (TimingOff.areItemsTheSame(timingOff,timingOff3)){
            timingOff3.setChecked(true);
            c=true;
        }

        if (TimingOff.areItemsTheSame(timingOff,timingOff4)){
            timingOff4.setChecked(true);
            c=true;
        }

        if (TimingOff.areItemsTheSame(timingOff,timingOff5)){
            timingOff5.setChecked(true);
            c=true;
        }

        if (!c){
            timingOff1.setChecked(true);
        }

        list.add(timingOff1);
        list.add(timingOff2);
        list.add(timingOff3);
        list.add(timingOff4);
        list.add(timingOff5);

        adapter.setData(list);

        adapter.setOnCheckedChangeListener(new TimingOffAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View v, TimingOff item) {
                timingOff = item;
            }
        });
    }
}
