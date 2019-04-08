package com.chengfu.android.fuplayer.dj.audio.library;

import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicSource {

    private static final String TAG = "MusicSource";

    private final List<MediaDescriptionCompat> mediaList = new ArrayList<>();

    public int size() {
        return mediaList.size();
    }

    public List<MediaDescriptionCompat> getMediaList(String parentId) {
        return mediaList;
    }

    public void add(MediaDescriptionCompat media) {
        if (media == null) {
            return;
        }
        mediaList.add(media);
    }

    public void add(int index, MediaDescriptionCompat media) {
        if (media == null || index < 0 || index > (size() - 1)) {
            return;
        }
        mediaList.add(index, media);
    }

    public void addAll(List<MediaDescriptionCompat> list) {
        if (list == null) {
            return;
        }
        mediaList.addAll(list);
    }


    public void remove(MediaDescriptionCompat media) {
        if (media == null) {
            return;
        }
        mediaList.remove(media);
    }

    public int indexOf(MediaDescriptionCompat media) {
        return mediaList.indexOf(media);
    }

    public void clear() {
        mediaList.clear();
    }


    public MediaDescriptionCompat findByMediaId(@NonNull String mediaId) {
        for (MediaDescriptionCompat media : mediaList) {
            if (mediaId.equals(media.getMediaId())) {
                return media;
            }
        }
        return null;
    }
}
