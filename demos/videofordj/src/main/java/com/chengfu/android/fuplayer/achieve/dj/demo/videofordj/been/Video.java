package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been;

import java.io.Serializable;

public class Video implements Serializable {

    private long id;
    private String stream_url;

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

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", stream_url='" + stream_url + '\'' +
                '}';
    }
}
