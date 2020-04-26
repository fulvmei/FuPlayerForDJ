package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.WeakHashMap;

public class AudioPlayManager {

    private static WeakHashMap<FragmentActivity, AudioPlayManager> audioPlayManagerMap = new WeakHashMap<>();

    public static AudioPlayManager get(@NonNull FragmentActivity activity, @NonNull Class<?> cls) {
        AudioPlayManager audioPlayManager = audioPlayManagerMap.get(activity);
        if (audioPlayManager == null) {
//            audioPlayManager = new AudioPlayManager();
            audioPlayManagerMap.put(activity, audioPlayManager);
        }
        return audioPlayManager;
    }

    private AudioPlayManager(@NonNull Context pkg, @NonNull Class<?> cls) {

    }
}
