package com.chengfu1.fupalyer;

import android.content.Intent;
import android.os.Bundle;

import com.chengfu.android.fuplayer.achieve.dj.audio.SessionActivity;

public class FuSessionActivity extends SessionActivity {

    @Override
    protected void onSessionActivity(Bundle extras) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
