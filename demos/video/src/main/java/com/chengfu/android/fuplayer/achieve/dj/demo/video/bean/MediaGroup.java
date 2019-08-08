package com.chengfu.android.fuplayer.achieve.dj.demo.video.bean;

import java.util.List;

public class MediaGroup {

    public String name;
    public List<Video> mediaList;

    public MediaGroup() {

    }

    public MediaGroup(String name, List<Video> mediaList) {
        this.name = name;
        this.mediaList = mediaList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMediaList(List<Video> mediaList) {
        this.mediaList = mediaList;
    }

    public List<Video> getMediaList() {
        return mediaList;
    }
}
