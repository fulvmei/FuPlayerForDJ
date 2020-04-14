package com.chengfu.android.fuplayer.achieve.dj.audio.repository;

import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

public class QueueRepository {

    private final MediaControllerCompat mediaController;

    public QueueRepository(Context context, @NonNull MediaSessionCompat.Token sessionToken) throws RemoteException {
        mediaController = new MediaControllerCompat(context, sessionToken);
    }



}
