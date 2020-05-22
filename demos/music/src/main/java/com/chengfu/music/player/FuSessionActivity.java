package com.chengfu.music.player;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chengfu.android.fuplayer.achieve.dj.audio.SessionActivity;


public class FuSessionActivity extends SessionActivity {

    @Override
    protected void onSessionActivity(Bundle extras) {
        super.onSessionActivity(extras);
        Log.d("hhh", "收到播放器消息 extras=" + extras.getSerializable("video"));

        Intent intent = new Intent(this, AudioPlayActivity.class);
        startActivity(intent);
        finish();
    }
}
