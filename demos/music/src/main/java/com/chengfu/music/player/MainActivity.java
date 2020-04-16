package com.chengfu.music.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;


import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chengfu.android.fuplayer.achieve.dj.audio.MusicContract;
import com.chengfu.android.fuplayer.achieve.dj.audio.MusicService;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.music.player.ui.main.SectionsPagerAdapter;
import com.chengfu.music.player.util.MusicUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    //媒体浏览器
    private MediaBrowserCompat mMediaBrowser;
    //媒体控制器
    private MediaControllerCompat mMediaController;

    View audioControlView;
    ImageView img;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(SectionsPagerAdapter.TAB_TITLES[position]);
        });

        tabLayoutMediator.attach();


        audioControlView = findViewById(R.id.audioControlView);

        audioControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AudioPlayActivity.class);
                startActivity(intent);
            }
        });


        img = findViewById(R.id.img);


        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().skipToPrevious();
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().skipToNext();
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().prepare();
                mMediaController.getTransportControls().play();
            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().pause();
            }
        });

        findViewById(R.id.fast_rewind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().rewind();
            }
        });

        findViewById(R.id.fast_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaController.getTransportControls().fastForward();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaBrowser.disconnect();
    }
}
