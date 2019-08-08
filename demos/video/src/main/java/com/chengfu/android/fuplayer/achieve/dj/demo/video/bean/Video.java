package com.chengfu.android.fuplayer.achieve.dj.demo.video.bean;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.APP;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.util.MediaSourceUtil;
import com.google.android.exoplayer2.source.MediaSource;

import java.io.Serializable;

public class Video implements Serializable {
    private long id;
    private String name;
    private String path;
    private String image;
    private String type;
    private String tag;

    private MediaSource mediaSource;

    public Video() {

    }

    public Video(String name, String path, String type, String tag) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.tag = tag;
        mediaSource = MediaSourceUtil.getMediaSource(APP.application, path);
    }

    public Video(String name, String path, String image, String type, String tag) {
        this.name = name;
        this.path = path;
        this.image = image;
        this.type = type;
        this.tag = tag;
        mediaSource = MediaSourceUtil.getMediaSource(APP.application, path);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
        mediaSource = MediaSourceUtil.getMediaSource(APP.application, path);
    }

    public String getPath() {
        return path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public MediaSource getMediaSource() {
        return mediaSource;
    }
}
