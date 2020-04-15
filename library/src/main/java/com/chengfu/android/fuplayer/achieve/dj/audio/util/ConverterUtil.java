package com.chengfu.android.fuplayer.achieve.dj.audio.util;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.chengfu.android.fuplayer.achieve.dj.audio.db.entity.MediaEntity;
import com.chengfu.android.fuplayer.achieve.dj.audio.db.vo.CurrentPlay;

import java.util.ArrayList;
import java.util.List;

public class ConverterUtil {

    public static MediaDescriptionCompat mediaEntityToMediaDescription(MediaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MediaDescriptionCompat.Builder()
                .setMediaId(entity.mediaId)
                .setTitle(entity.title)
                .setSubtitle(entity.subTitle)
                .setDescription(entity.description)
                .setIconBitmap(entity.icon)
                .setIconUri(entity.iconUri)
                .setExtras(entity.extras)
                .setMediaUri(entity.mediaUri)
                .build();
    }

    public static MediaEntity mediaDescriptionToMediaEntity(MediaDescriptionCompat description) {
        if (description == null || description.getMediaId() == null) {
            return null;
        }
        MediaEntity entity = new MediaEntity(description.getMediaId());
        entity.title = description.getTitle() != null ? description.getTitle().toString() : null;
        entity.subTitle = description.getSubtitle() != null ? description.getSubtitle().toString() : null;
        entity.description = description.getDescription() != null ? description.getDescription().toString() : null;
        entity.icon = description.getIconBitmap();
        entity.iconUri = description.getIconUri();
        entity.extras = description.getExtras();
        entity.mediaUri = description.getMediaUri();
        return entity;
    }

    public static MediaDescriptionCompat currentPlayToMediaDescription(CurrentPlay entity) {
        if (entity == null) {
            return null;
        }
        return mediaEntityToMediaDescription(entity.audio);
    }

    public static List<MediaDescriptionCompat> currentPlayListToMediaDescriptionList(List<CurrentPlay> list) {
        List<MediaDescriptionCompat> temp = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return temp;
        }
        for (CurrentPlay item : list) {
            temp.add(currentPlayToMediaDescription(item));
        }
        return temp;
    }


    public static List<MediaDescriptionCompat> mediaEntityListToMediaDescriptionList(List<MediaEntity> list) {
        List<MediaDescriptionCompat> temp = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return temp;
        }
        for (MediaEntity item : list) {
            temp.add(mediaEntityToMediaDescription(item));
        }
        return temp;
    }

    public static List<MediaEntity> mediaDescriptionListToMediaEntityList(List<MediaDescriptionCompat> list) {
        List<MediaEntity> temp = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return temp;
        }
        for (MediaDescriptionCompat item : list) {
            temp.add(mediaDescriptionToMediaEntity(item));
        }
        return temp;
    }

    public static MediaSessionCompat.QueueItem currentPlayToQueueItem(CurrentPlay value, long id) {
        if (value == null) {
            return null;
        }
        return new MediaSessionCompat.QueueItem(currentPlayToMediaDescription(value), id);
    }

    public static List<MediaSessionCompat.QueueItem> currentPlayListToQueueItemList(List<CurrentPlay> list) {
        List<MediaSessionCompat.QueueItem> temp = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return temp;
        }
        for (int i = 0; i < list.size(); i++) {
            temp.add(currentPlayToQueueItem(list.get(i), i + 1));
        }
        return temp;
    }


}
