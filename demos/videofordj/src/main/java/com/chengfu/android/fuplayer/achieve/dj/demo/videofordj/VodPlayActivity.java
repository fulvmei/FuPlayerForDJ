package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;

public class VodPlayActivity extends AppCompatActivity {

    //    private FrameLayout videoPlayerContainer;
    private VideoPlayFragmentApi videoPlayFragment;
    int i = 0;
    private static final String url="https://wsvod.gzstv.com/uploads/media/gzxwlb/1106jinbohuiVA1.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vod_play);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                videoPlayFragment = VideoPlayFragmentApi.newInstance(new Video(1,url));

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.video_player_container, videoPlayFragment)
                        .commit();
            }
        });


        videoPlayFragment = VideoPlayFragmentApi.newInstance(new Video(1,url));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.video_player_container, videoPlayFragment)
                .commit();
    }
}
