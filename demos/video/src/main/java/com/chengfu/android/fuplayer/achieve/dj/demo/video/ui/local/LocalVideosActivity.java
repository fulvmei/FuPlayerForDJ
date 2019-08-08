package com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.local;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.R;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.util.VideoUtil;

import java.util.List;


public class LocalVideosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    LocalVideoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loacl_videos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);

        adapter = new LocalVideoListAdapter();

        recyclerView.setAdapter(adapter);

        List<Video> list = VideoUtil.getLocalVideoList(this);

        list.addAll(VideoUtil.getVideoList());

        list.addAll(VideoUtil.getAudiooList());

        adapter.setData(list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
