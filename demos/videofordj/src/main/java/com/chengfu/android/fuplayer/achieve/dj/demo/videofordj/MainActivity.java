package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been.VideoIcon;

public class MainActivity extends AppCompatActivity {

    private static final String url1 = "https://wsvod.gzstv.com/uploads/media/gzxwlb/1106jinbohuiVA1.mp4";
    private static final String url2 = "https://wsvod.gzstv.com/uploads/media/dj/44f1b00b7deaa0beeeab0530e32e3d20.mp4";
    private static final String url3 = "https://hls-qnhi-live.gzstv.com/livegztv-hi/CH01_hi.m3u8?s=7abebe4f136c9802a2df4090a06e15f9&ts=5ddf42b8";
    private static final String url4 = "https://wsvod.gzstv.com/uploads/media/dj/44f1b00b7deaa0beeeab0530e32e3d20.mp4";
    private static final String img = "https://mstatic.gzstv.com/media/streams/images/2019/11/13/26d163e0bc7d4a95af4f6ff6bacbd433.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play1).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VodPlayActivity.class);
            intent.putExtra("video",testVideo(url1));
            startActivity(intent);
        });

        findViewById(R.id.play2).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VodPlayActivity.class);
            intent.putExtra("video",testVideo(url2));
            startActivity(intent);
        });

        findViewById(R.id.play3).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VodPlayActivity.class);
            intent.putExtra("video",testVideo(url3));
            startActivity(intent);
        });

        findViewById(R.id.play4).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VodPlayActivity.class);
            intent.putExtra("video",testVideo(url4));
            startActivity(intent);
        });
    }

    private Video testVideo(String url) {
        Video video = new Video();
        video.setNeed_login(true);
        video.setStream_url(url);
        video.setStatus(Video.STATUS_SOON_START);
        video.setThumbnail(img);

        VideoIcon icon = new VideoIcon();
        icon.setOpacity(0.8);
        icon.setScale_to_y(12);
        icon.setUrl("https://mstatic.gzstv.com/media/default/t_Fw7N9jLZ-TVl_100x100_exT5XhbJ_2x.png");
        video.setIcon(icon);
        return video;
    }
}
