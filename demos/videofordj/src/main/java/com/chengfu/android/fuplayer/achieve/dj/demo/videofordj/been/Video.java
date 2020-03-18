package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been;

import java.io.Serializable;

public class Video implements Serializable {

    public static final int STATUS_SOON_START = 0;
    public static final int STATUS_STARTING = 1;
    public static final int STATUS_STARTED = 2;
    public static final int STATUS_REVIEW = 3;

    private long id;
    private String stream_url;
    private String title;
    private boolean need_login;
    private int status = STATUS_STARTING;

    private String thumbnail;

    private VideoIcon icon;

    public Video() {
    }

    public Video(long id) {
        this.id = id;
    }

    public Video(long id, String stream_url) {
        this.id = id;
        this.stream_url = stream_url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isNeed_login() {
        return need_login;
    }

    public void setNeed_login(boolean need_login) {
        this.need_login = need_login;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public VideoIcon getIcon() {
        return icon;
    }

    public void setIcon(VideoIcon icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", stream_url='" + stream_url + '\'' +
                ", title='" + title + '\'' +
                ", need_login=" + need_login +
                ", status=" + status +
                ", thumbnail='" + thumbnail + '\'' +
                ", icon=" + icon +
                '}';
    }
}
