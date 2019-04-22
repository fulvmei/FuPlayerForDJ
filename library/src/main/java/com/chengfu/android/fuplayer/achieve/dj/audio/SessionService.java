package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SessionService extends IntentService {

    public SessionService() {
        super("SessionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getBundleExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS);
            if (extras != null) {
                extras.setClassLoader(getClass().getClassLoader());
            }
            onSessionActivity(extras);
        }
    }

    protected void onSessionActivity(Bundle extras) {

    }
}
