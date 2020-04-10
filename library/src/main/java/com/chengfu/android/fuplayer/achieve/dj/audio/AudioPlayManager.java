package com.chengfu.android.fuplayer.achieve.dj.audio;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.AudioDatabase;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.CurrentPlayEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayManager {

    @WorkerThread
    public static void setCurrentPlayList(@NonNull Context context, List<MediaEntity> list, int currentPlayingIndex) {
        AudioDatabase.getInstance(context).currentPlayDao().deleteAll();
        if (list == null || list.size() == 0) {
            return;
        }
        AudioDatabase.getInstance(context).audioDao().insertAll(list);

        List<CurrentPlayEntity> currentPlayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CurrentPlayEntity currentPlay = new CurrentPlayEntity(list.get(i).media_id, i);
            if (i == currentPlayingIndex) {
                currentPlay.playing = true;
            }
            currentPlayList.add(currentPlay);
        }
        AudioDatabase.getInstance(context).currentPlayDao().insertAll(currentPlayList);
    }

    @WorkerThread
    public static void addToNextPlay(@NonNull Context context, MediaEntity entity) {
        if (entity == null) {
            return;
        }
        AudioDatabase.getInstance(context).audioDao().insert(entity);

        CurrentPlayEntity currentPlaying = AudioDatabase.getInstance(context).currentPlayDao().queryCurrentPlaying();
        if (currentPlaying != null) {
            List<CurrentPlayEntity> temp = AudioDatabase.getInstance(context).currentPlayDao().queryUpIndex(currentPlaying.position + 1);
            if (temp != null && temp.size() > 0) {
                for (int i = 0; i < temp.size(); i++) {
                    temp.get(i).position++;
                }
                AudioDatabase.getInstance(context).currentPlayDao().updateAll(temp);
            }
        }

        CurrentPlayEntity currentPlay = new CurrentPlayEntity(entity.media_id, currentPlaying != null ? currentPlaying.position + 1 : 0);
        AudioDatabase.getInstance(context).currentPlayDao().insert(currentPlay);
    }

    public static LiveData<List<CurrentPlay>> getCurrentPlayList(@NonNull Context context) {
        return AudioDatabase.getInstance(context).currentPlayDao().getCurrentPlayList();
    }

}
