package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SessionActivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && MusicContract.ACTION_SESSION_ACTIVITY.equals(intent.getAction())) {
            Bundle extras = intent.getBundleExtra(MusicContract.KEY_MEDIA_DESCRIPTION_EXTRAS);
            if (extras != null) {
                extras.setClassLoader(getClass().getClassLoader());
            }
            onSessionActivity(context, extras);
        }
    }

    protected void onSessionActivity(Context context, Bundle extras) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
