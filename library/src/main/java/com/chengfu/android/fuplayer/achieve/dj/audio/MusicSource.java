package com.chengfu.android.fuplayer.achieve.dj.audio;

import androidx.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;

import com.chengfu.android.fuplayer.util.FuLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MusicSource {

    private static final String TAG = "MusicSource";

    private final Map<String, List<MediaDescriptionCompat>> mediaMap = new HashMap<>();

    public int size() {
        return size(MusicContract.KEY_DEFAULT_PARENT_ID);
    }

    public int size(String parentId) {
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        return mediaList != null ? mediaList.size() : 0;
    }

    public List<MediaDescriptionCompat> getMediaList() {
        return mediaMap.get(MusicContract.KEY_DEFAULT_PARENT_ID);
    }

    public List<MediaDescriptionCompat> getMediaList(String parentId) {
        return mediaMap.get(parentId);
    }

    public void add(MediaDescriptionCompat media) {
        add(MusicContract.KEY_DEFAULT_PARENT_ID, Integer.MAX_VALUE, media);
    }

    public void add(int index, MediaDescriptionCompat media) {
        add(MusicContract.KEY_DEFAULT_PARENT_ID, index, media);
    }

    public void add(String parentId, int index, MediaDescriptionCompat media) {
        if (media == null) {
            FuLog.e(TAG, "media is null");
            return;
        }
        if (media.getMediaId() == null) {
            FuLog.e(TAG, "mediaId is null");
            return;
        }
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        if (mediaList == null) {
            mediaList = new ArrayList<>();
            mediaList.add(media);
            return;
        }
        boolean needAdd = true;
        for (MediaDescriptionCompat item : mediaList) {
            if (media.getMediaId().equals(item.getMediaId())) {
                needAdd = false;
                break;
            }
        }
        int addIndex;
        if (index < 0) {
            addIndex = 0;
        } else if (index > mediaList.size()) {
            addIndex = mediaList.size();
        } else {
            addIndex = index;
        }
        if (needAdd) {
            mediaList.add(addIndex, media);
        }
    }

    public void addAll(List<MediaDescriptionCompat> list) {
        addAll(MusicContract.KEY_DEFAULT_PARENT_ID, list);
    }

    public void addAll(String parentId, List<MediaDescriptionCompat> list) {
        if (list == null) {
            FuLog.e(TAG, "media list is null");
            return;
        }
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        if (mediaList == null) {
            mediaList = new ArrayList<>();
        }
        mediaList.addAll(list);
        mediaMap.put(parentId, mediaList);
    }

    public void replaceAll(List<MediaDescriptionCompat> list) {
        replaceAll(MusicContract.KEY_DEFAULT_PARENT_ID, list);
    }

    public void replaceAll(String parentId, List<MediaDescriptionCompat> list) {
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        if (mediaList == null && list == null) {
            return;
        }
        if (mediaList == null) {
            mediaList = new ArrayList<>();
        }
        mediaList.clear();
        mediaList.addAll(list);
    }


    public void remove(MediaDescriptionCompat media) {
        remove(MusicContract.KEY_DEFAULT_PARENT_ID, media);
    }

    public void remove(String parentId, MediaDescriptionCompat media) {
        if (media == null) {
            return;
        }
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        if (mediaList != null) {
            mediaList.remove(media);
        }
    }

    public int indexOf(MediaDescriptionCompat media) {
        return indexOf(MusicContract.KEY_DEFAULT_PARENT_ID, media);
    }

    public int indexOf(String parentId, MediaDescriptionCompat media) {
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        if (mediaList != null) {
            return mediaList.indexOf(media);
        }
        return -1;
    }

    public void clear() {
        clear(MusicContract.KEY_DEFAULT_PARENT_ID);
    }

    public void clear(String parentId) {
        List<MediaDescriptionCompat> mediaList = mediaMap.get(parentId);
        if (mediaList != null) {
            mediaList.clear();
        }
    }

    public String findParentIdByMediaId(@NonNull String mediaId) {
        Iterator<Map.Entry<String, List<MediaDescriptionCompat>>> iterator = mediaMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MediaDescriptionCompat>> entry = iterator.next();
            List<MediaDescriptionCompat> mediaList = entry.getValue();
            for (MediaDescriptionCompat media : mediaList) {
                if (mediaId.equals(media.getMediaId())) {
                    return entry.getKey();
                }
            }
            iterator.next();
        }
        return null;
    }

    public MediaDescriptionCompat findByMediaId(@NonNull String mediaId) {
        Iterator<Map.Entry<String, List<MediaDescriptionCompat>>> iterator = mediaMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MediaDescriptionCompat>> entry = iterator.next();
            List<MediaDescriptionCompat> mediaList = entry.getValue();
            for (MediaDescriptionCompat media : mediaList) {
                if (mediaId.equals(media.getMediaId())) {
                    return media;
                }
            }
            iterator.next();
        }
        return null;
    }
}
