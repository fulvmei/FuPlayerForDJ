package com.chengfu1;

import android.os.Bundle;

import com.chengfu.android.fuplayer.achieve.dj.audio.SessionActivity;


public class FuSessionActivity extends SessionActivity {

    @Override
    protected void onSessionActivity(Bundle extras) {
        super.onSessionActivity(extras);
        System.out.println("33333333333333333333333333333");
    }

}
