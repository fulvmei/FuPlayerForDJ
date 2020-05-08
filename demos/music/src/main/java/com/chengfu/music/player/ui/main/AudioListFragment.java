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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.chengfu.android.fuplayer.achieve.dj.audio.DataBaseManager;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.util.ConverterUtil;
import com.chengfu.music.player.MainActivity;
import com.chengfu.music.player.R;
import com.chengfu.music.player.util.MusicUtil;

import java.util.ArrayList;
import java.util.Collections;
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

    List<String> list = new ArrayList<>();

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
                adapter.getList().addAll(MusicUtil.getTestMedias(1, false));
                adapter.notifyDataSetChanged();
            }
        });

        list.add("1");
        list.add("2");

        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> newList = new ArrayList<>();
                newList.add("2");
                newList.add("1");
                newList.add("3");
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemDiffCallBack(list, newList),true);
                diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                    @Override
                    public void onInserted(int position, int count) {
                        list.add(position,"3");
                        System.out.println("onInserted position=" + position + ",count=" + count+",list="+list);
                    }

                    @Override
                    public void onRemoved(int position, int count) {
                        System.out.println("onRemoved position=" + position + ",count=" + count+",list="+list);
                    }

                    @Override
                    public void onMoved(int fromPosition, int toPosition) {
                        Collections.swap(list,fromPosition,toPosition);
                        System.out.println("onMoved fromPosition=" + fromPosition + ",toPosition=" + toPosition+",list="+list);
                    }

                    @Override
                    public void onChanged(int position, int count, @Nullable Object payload) {
                        System.out.println("onChanged position=" + position + ",count=" + count + ",payload=" + payload+",list="+list);
                    }
                });
            }
        });
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

    public class ItemDiffCallBack extends DiffUtil.Callback {
        List<String> oldList;
        List<String> newList;

        public ItemDiffCallBack(List<String> oldList, List<String> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    }
}
