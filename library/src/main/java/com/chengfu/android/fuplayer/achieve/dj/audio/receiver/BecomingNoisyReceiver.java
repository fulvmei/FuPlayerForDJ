package com.chengfu.android.fuplayer.achieve.dj.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

public class BecomingNoisyReceiver extends BroadcastReceiver {
    private Context context;
    private MediaSessionCompat.Token sessionToken;
    private IntentFilter noisyIntentFilter;
    private MediaControllerCompat controller;
    private boolean registered = false;

    public BecomingNoisyReceiver(Context context,
                                 MediaSessionCompat.Token sessionToken) {
        this.context = context;
        this.sessionToken = sessionToken;

        noisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        try {
            controller = new MediaControllerCompat(context, sessionToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter);
            registered = true;
        }
    }

    public void unregister() {
        if (registered) {
            context.unregisterReceiver(this);
            registered = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()) && controller != null) {
            controller.getTransportControls().pause();
        }
    }
}
