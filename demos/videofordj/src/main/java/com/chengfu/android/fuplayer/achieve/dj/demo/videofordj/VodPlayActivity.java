package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.VideoIcon;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

public class VodPlayActivity extends AppCompatActivity implements IPlayActivity {

    //    private FrameLayout videoPlayerContainer;
    private VideoPlayFragmentApi videoPlayFragment;

    private FrameLayout video_player_container;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vod_play);

        video_player_container = findViewById(R.id.video_player_container);

        findViewById(R.id.btn).setOnClickListener(view -> startActivity(getIntent()));

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Video video= (Video) intent.getSerializableExtra("video");

        videoPlayFragment = VideoPlayFragmentApi.newInstance(video);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.video_player_container, videoPlayFragment)
                .commit();

        ImmersionBar.with(this)
                .statusBarColorInt(Color.BLACK)
                .navigationBarColorInt(Color.WHITE)
                .statusBarDarkFont(false, 0.3f)
                .navigationBarDarkIcon(true, 0.3f)
                .fitsSystemWindows(true)
                .hideBar(BarHide.FLAG_SHOW_BAR)
                .init();
    }

    @Override
    public void onVideoScreenChanged(boolean fullScreen, boolean portrait) {
        if (fullScreen) {
            ViewGroup.LayoutParams layoutParams = video_player_container.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            video_player_container.setLayoutParams(layoutParams);
            if (portrait) {
//                ImmersionBar.with(this)
//                        .fitsSystemWindows(false)
//                        .navigationBarColorInt(Color.BLACK)
//                        .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
//                        .navigationBarDarkIcon(false, 0.3f)
//                        .fitsSystemWindows(false)
//                        .init();
            } else {
                ImmersionBar.with(this)
                        .fitsSystemWindows(false)
                        .hideBar(BarHide.FLAG_HIDE_BAR)
                        .init();
            }

        } else {
            ViewGroup.LayoutParams layoutParams = video_player_container.getLayoutParams();
            layoutParams.height = 600;
            video_player_container.setLayoutParams(layoutParams);

            ImmersionBar.with(this)
                    .statusBarColorInt(Color.BLACK)
                    .navigationBarColorInt(Color.WHITE)
                    .statusBarDarkFont(false, 0.3f)
                    .navigationBarDarkIcon(true, 0.3f)
                    .fitsSystemWindows(true)
                    .hideBar(BarHide.FLAG_SHOW_BAR)
                    .init();
        }
    }

    @Override
    public void onBackPressed() {
        if (videoPlayFragment != null && videoPlayFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
