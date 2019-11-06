package com.chengfu.android.fuplayer.achieve.dj.demo.video.ui.video;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Looper;
import androidx.annotation.VisibleForTesting;

import android.os.Handler;

import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Resource;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.bean.Video;
import com.chengfu.android.fuplayer.achieve.dj.demo.video.util.VideoUtil;

public class VideoDetailsViewModel extends ViewModel {

    private Handler handler;
    private final MediatorLiveData<Resource<Video>> videoResource;
    private MutableLiveData<Video> videoSource;

    private String id;

    public VideoDetailsViewModel() {
        handler = new Handler(Looper.getMainLooper());

        videoResource = new MediatorLiveData<>();
        videoResource.setValue(Resource.loading(null, null));
    }

    public void setParams(String id) {
        this.id = id;
        refreshVideo();
    }

    @VisibleForTesting
    public LiveData<Resource<Video>> getVideoResource() {
        return videoResource;
    }

    public void refreshVideo() {
        videoResource.setValue(Resource.loading(null, null));
        handler.postDelayed(() -> {
            Video video = VideoUtil.getVideo(id);
            if (video == null) {
                videoResource.setValue(Resource.error(null, null));
            } else {
                videoResource.setValue(Resource.success(video, null));
            }

        }, 3000);
    }
}
