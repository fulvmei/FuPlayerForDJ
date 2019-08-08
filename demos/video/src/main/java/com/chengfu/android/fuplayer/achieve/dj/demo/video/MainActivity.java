package com.chengfu.android.fuplayer.achieve.dj.demo.video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.local.LocalVideosActivity;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video.VideoListActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btnLocalVideos).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LocalVideosActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnNetVideos).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MediaChooseActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnVideoListPlay).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
            startActivity(intent);
        });

        requestReadExternalPermission();

    }

    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        } else {
        }
    }

}
