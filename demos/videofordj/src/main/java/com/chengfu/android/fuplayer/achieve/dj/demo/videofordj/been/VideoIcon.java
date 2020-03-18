package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj.been;

import org.json.JSONObject;

import java.io.Serializable;

public class VideoIcon implements Serializable {

    private double opacity;//透明度(百分比)
    private String url;//图片地址
    private int scale_to_y;//图片高度占播放高度的百分比

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScale_to_y() {
        return scale_to_y;
    }

    public void setScale_to_y(int scale_to_y) {
        this.scale_to_y = scale_to_y;
    }
}
