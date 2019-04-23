package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SessionActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Bundle extras = intent.getBundleExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS);
            if (extras != null) {
                extras.setClassLoader(getClass().getClassLoader());
            }
            onSessionActivity(extras);
        }
        finish();
    }

    protected void onSessionActivity(Bundle extras) {

    }
}
